package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.postprocessing;

import java.util.Comparator;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.OrdinalCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;

class OrdinalCriterionVitalSignProcessor implements VitalSignProcessor {
	private final OrdinalCriterion.Ordinal ordinal;
	private final Comparator<VitalSign> comparator;

	OrdinalCriterionVitalSignProcessor(final OrdinalCriterion criterion) {
		this.ordinal = criterion.getOrdinal();
		this.comparator = createComparator();
	}

	public Stream<VitalSign> process(final Stream<VitalSign> vitalSigns) {
		return vitalSigns.min(comparator)
				.map(Stream::of)
				.orElse(Stream.empty());
	}

	private Comparator<VitalSign> createComparator() {
		final Comparator<VitalSign> byKey = Comparator.comparing(VitalSign::getEffectiveDateTime);
		return ordinal == OrdinalCriterion.Ordinal.LAST ?
				Comparator.nullsLast(byKey.reversed()) :
				Comparator.nullsLast(byKey);
	}
}
