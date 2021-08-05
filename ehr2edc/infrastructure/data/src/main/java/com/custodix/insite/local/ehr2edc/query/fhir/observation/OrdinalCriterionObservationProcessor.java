package com.custodix.insite.local.ehr2edc.query.fhir.observation;

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.OrdinalCriterion;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;

public class OrdinalCriterionObservationProcessor implements ObservationProcessor {
	private final OrdinalCriterion.Ordinal ordinal;
	private final Comparator<Observation> comparator;

	public OrdinalCriterionObservationProcessor(final OrdinalCriterion criterion) {
		this.ordinal = criterion.getOrdinal();
		this.comparator = createComparator();
	}

	public Stream<Observation> process(final Stream<Observation> observations) {
		return observations.min(comparator).map(Stream::of).orElse(Stream.empty());
	}

	private Comparator<Observation> createComparator() {
		final Comparator<Observation> byKey = Comparator.comparing(o -> getSortKey(o, ordinal));
		return Objects.equals(OrdinalCriterion.Ordinal.LAST, ordinal)
				? Comparator.nullsLast(byKey.reversed()) : Comparator.nullsLast(byKey);
	}

	private Date getSortKey(Observation observation, OrdinalCriterion.Ordinal ordinal) {
		final IDatatype effective = observation.getEffective();
		if (effective instanceof DateTimeDt) {
			final DateTimeDt date = (DateTimeDt) effective;
			return date.getValue();
		} else if (effective instanceof PeriodDt) {
			final PeriodDt period = (PeriodDt) effective;
			return Objects.equals(OrdinalCriterion.Ordinal.LAST, ordinal)
					? period.getEnd() : period.getStart();
		}
		return null;
	}
}
