package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.postprocessing;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ExcludeConceptsCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;

class ExcludeConceptsCriterionVitalSignProcessor implements VitalSignProcessor {
	private final Set<String> codes;

	ExcludeConceptsCriterionVitalSignProcessor(final ExcludeConceptsCriterion criterion) {
		codes = criterion.getConcepts()
				.stream()
				.map(ConceptCode::getCode)
				.collect(Collectors.toSet());
	}

	public Stream<VitalSign> process(final Stream<VitalSign> vitalSigns) {
		return vitalSigns.filter(this::isNotExcluded);
	}

	private boolean isNotExcluded(VitalSign vitalSign) {
		return !codes.contains(vitalSign.getConcept()
				.getCode());
	}
}
