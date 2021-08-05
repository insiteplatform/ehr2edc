package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.postprocessing;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ConceptCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;

class ConceptCriterionVitalSignProcessor implements VitalSignProcessor {
	private final Set<String> codes;

	ConceptCriterionVitalSignProcessor(ConceptCriterion criterion) {
		this.codes = criterion.getConcepts()
				.stream()
				.map(ConceptCode::getCode)
				.collect(Collectors.toSet());
	}

	@Override
	public Stream<VitalSign> process(Stream<VitalSign> vitalSigns) {
		return vitalSigns.filter(this::hasIncludedCode);
	}

	private boolean hasIncludedCode(VitalSign vitalSign) {
		return codes.contains(vitalSign.getConcept()
				.getCode());
	}
}
