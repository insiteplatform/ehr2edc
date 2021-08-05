package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation.mapping;

import java.util.function.Consumer;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;

class VitalSignObservationCodeProcessor {
	static void process(CodeableConceptDt code, Consumer<ConceptCode> consumer) {
		code.getCoding()
				.stream()
				.findFirst()
				.map(CodingDt::getCode)
				.map(ConceptCode::conceptFor)
				.ifPresent(consumer);
	}
}
