package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;

public class VitalSignConceptFieldObjectMother {
	public static final String BMI_COMPONENT = "BMI";
	public static final String BMI_LATERALITY = "RIGHT";
	public static final String BMI_LOCATION = "Body";
	public static final String BMI_POSITION = "Sitting";
	public static final String BMI_CONCEPT_CODE = "39156-5";

	public static VitalSignConceptField aBmiConcept() {
		return VitalSignConceptField.newBuilder()
				.withComponent(BMI_COMPONENT)
				.withLaterality(BMI_LATERALITY)
				.withLocation(BMI_LOCATION)
				.withPosition(BMI_POSITION)
				.withConcept(ConceptCode.conceptFor(BMI_CONCEPT_CODE))
				.build();
	}

	public static VitalSignConceptField aDefaultConcept() {
		return VitalSignConceptField.newBuilder()
				.withComponent("component")
				.withLaterality("RIGHT")
				.withLocation("somewhere")
				.withConcept(ConceptCode.conceptFor("conceptCode"))
				.build();
	}
}
