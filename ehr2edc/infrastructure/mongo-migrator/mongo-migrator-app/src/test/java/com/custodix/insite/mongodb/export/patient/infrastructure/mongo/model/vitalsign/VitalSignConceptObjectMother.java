package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign;

import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.concept.ConceptCode;

public class VitalSignConceptObjectMother {
	public static VitalSignConcept aDefaultVitalConcept() {
		return aDefaultVitalSignConceptBuilder()
				.build();
	}

	public static VitalSignConcept.Builder aDefaultVitalSignConceptBuilder() {
		return VitalSignConcept.newBuilder()
				.withLaterality("laterality")
				.withPosition("position")
				.withLocation("location")
				.withComponent("component")
				.withConcept(ConceptCode.conceptFor("concept"));
	}
}
