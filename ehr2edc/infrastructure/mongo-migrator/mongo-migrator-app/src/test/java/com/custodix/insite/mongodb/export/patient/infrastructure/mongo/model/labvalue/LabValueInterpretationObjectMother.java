package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue;

public class LabValueInterpretationObjectMother {
	public static LabValueInterpretation aDefaultLabValueInterpretation() {
		return aDefaultLabValueInterpretationBuilder().build();
	}

	public static LabValueInterpretation.Builder aDefaultLabValueInterpretationBuilder() {
		return LabValueInterpretation.newBuilder()
				.withOriginalInterpretation("OriginalInterpretation(")
				.withParsedInterpretation(12);
	}
}
