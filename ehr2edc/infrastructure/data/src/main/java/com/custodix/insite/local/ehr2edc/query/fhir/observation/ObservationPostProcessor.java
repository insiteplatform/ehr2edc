package com.custodix.insite.local.ehr2edc.query.fhir.observation;

import java.util.Optional;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria;
import com.custodix.insite.local.ehr2edc.query.fhir.ReferencedResourceLoader;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Specimen;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class ObservationPostProcessor {

	public Stream<Observation> process(final Stream<ObservationProcessor> processors, final Stream<Observation> observations) {
		return sequentialProcessor(processors).process(observations);
	}

	public ObservationProcessor getCriteriaProcessor(final Criteria criteria) {
		return sequentialProcessor(getCriteriaProcessors(criteria));
	}

	private Stream<ObservationProcessor> getCriteriaProcessors(final Criteria criteria) {
		final Optional<ObservationProcessor> excludeConceptsProcessor =
				criteria.excludeConcepts().map(ExcludeConceptsCriterionObservationProcessor::new);
		final Optional<ObservationProcessor> conceptProcessor =
				criteria.concepts().map(ConceptCriterionObservationProcessor::new);
		final Optional<ObservationProcessor> ordinalProcessor =
				criteria.ordinal().map(OrdinalCriterionObservationProcessor::new);
		return Stream.of(excludeConceptsProcessor, conceptProcessor, ordinalProcessor)
				.filter(Optional::isPresent)
				.map(Optional::get);
	}

	public ObservationProcessor getSpecimenLoader(final IGenericClient client) {
		final ReferencedResourceLoader<Specimen> loader = new ReferencedResourceLoader<>(client, Specimen.class);
		return observations -> observations.peek(observation -> loader.load(observation.getSpecimen()));
	}

	private ObservationProcessor sequentialProcessor(Stream<ObservationProcessor> processors) {
		return processors
				.reduce((op1,op2) -> observations -> op2.process(op1.process(observations)))
				.orElse(p -> p);
	}
}
