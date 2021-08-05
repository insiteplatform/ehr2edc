package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model;

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor;

public class LabConceptObjectMother {
	public static final String INSULIN_CODE = "INSULIN";
	public static final String INSULIN_COMPONENT = "Component for insulin";
	public static final String INSULIN_SPECIMEN = "Blood";
	public static final String INSULIN_METHOD = "Radioimmunoassay";
	public static final String INSULIN_FASTING_STATUS = "NOT_FASTING";

	public static LabConceptField aDefaultLabConcept() {
		return aDefaultLabConceptBuilder().build();
	}

	public static LabConceptField.Builder aDefaultLabConceptBuilder() {
		return LabConceptField.newBuilder()
				.withComponent("component")
				.withConcept(conceptFor("concept"))
				.withFastingStatus("NOT_FASTING")
				.withMethod("method")
				.withSpecimen("specimen");
	}

	public static LabConceptField.Builder anInsulinLabConceptBuilder() {
		return LabConceptField.newBuilder()
				.withConcept(conceptFor(INSULIN_CODE))
				.withComponent(INSULIN_COMPONENT)
				.withSpecimen(INSULIN_SPECIMEN)
				.withMethod(INSULIN_METHOD)
				.withFastingStatus(INSULIN_FASTING_STATUS);
	}
}
