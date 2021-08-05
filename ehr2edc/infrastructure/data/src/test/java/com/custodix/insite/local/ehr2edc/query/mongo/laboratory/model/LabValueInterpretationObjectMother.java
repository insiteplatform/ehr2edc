package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model;

public class LabValueInterpretationObjectMother {
	public static LabValueInterpretationField aDefaultLabValueInterpretation() {
		return aDefaultLabValueInterpretationBuilder().build();
	}

	public static LabValueInterpretationField.Builder aDefaultLabValueInterpretationBuilder() {
		return LabValueInterpretationField.newBuilder()
				.withOriginalInterpretation("OriginalInterpretation(")
				.withParsedInterpretation(12);
	}
}
