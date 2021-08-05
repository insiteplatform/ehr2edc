package com.custodix.insite.local.ehr2edc.query.fhir;

import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IIdType;

import ca.uhn.fhir.model.dstu2.resource.BaseResource;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;

public class FhirResourceRepository<T extends BaseResource> {

	private final IGenericClient client;
	private final Class<T> entryClass;

	public FhirResourceRepository(final IGenericClient client, final Class<T> entryClass) {
		this.client = client;
		this.entryClass = entryClass;
	}

	public T get(IIdType id) {
		return client.read()
				.resource(entryClass)
				.withId(id)
				.execute();
	}

	public Stream<T> find(final List<FhirQuery> fhirQueries) {
		return fhirQueries.stream()
				.flatMap(this::find)
				.filter(distinctByKey(BaseResource::getId));
	}

	public Stream<T> find(final FhirQuery fhirQuery) {
		final Bundle firstBundle = getFirstBundle(fhirQuery);
		return getBundleStream(firstBundle).flatMap(bundle -> bundle.getEntry()
				.stream())
				.filter(entry -> entryClass.isInstance(entry.getResource()))
				.map(entry -> entryClass.cast(entry.getResource()));
	}

	private Bundle getFirstBundle(final FhirQuery fhirQuery) {
		final IQuery<IBaseBundle> query = client.search()
				.forResource(entryClass);
		fhirQuery.getCriteria()
				.forEach(query::where);
		fhirQuery.getSortSpec()
				.ifPresent(query::sort);
		fhirQuery.getLimit()
				.ifPresent(query::count);
		fhirQuery.getIncludes()
				.forEach(query::include);
		return (Bundle) query.execute();
	}

	private Stream<Bundle> getBundleStream(final Bundle firstBundle) {
		return StreamSupport.stream(getBundleSpliterator(firstBundle), false);
	}

	private Spliterator<Bundle> getBundleSpliterator(final Bundle firstBundle) {
		return new Spliterators.AbstractSpliterator<Bundle>(Long.MAX_VALUE, Spliterator.ORDERED) {
			private Bundle current = null;

			@Override
			public boolean tryAdvance(Consumer<? super Bundle> action) {
				if (current == null) {
					current = firstBundle;
				} else {
					if (current.getLink(Bundle.LINK_NEXT) == null) {
						return false;
					}
					current = client.loadPage()
							.next(current)
							.execute();
				}
				action.accept(current);
				return true;
			}
		};
	}

	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
