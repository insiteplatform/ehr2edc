package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue;

import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.concept.ConceptCode;

public class LabConceptObjectMother {
	public static LabConcept aDefaultLabConcept() {
		return aDefaultLabConceptBuilder().build();
	}

	public static LabConcept.Builder aDefaultLabConceptBuilder() {
		return LabConcept.newBuilder()
				.withComponent("component")
				.withConcept(ConceptCode.conceptFor("concept"))
				.withFastingStatus("fastingStatus")
				.withMethod("method")
				.withSpecimen("specimen");
	}
}
