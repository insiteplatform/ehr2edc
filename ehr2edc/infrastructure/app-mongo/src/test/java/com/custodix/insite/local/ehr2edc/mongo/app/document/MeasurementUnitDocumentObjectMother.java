package com.custodix.insite.local.ehr2edc.mongo.app.document;

public class MeasurementUnitDocumentObjectMother {

	public static MeasurementUnitDocument aDefaultMeasurementUnitDocument() {
		return aDefaultMeasurementUnitDocumentBuilder().build();
	}

	private static MeasurementUnitDocument.Builder aDefaultMeasurementUnitDocumentBuilder() {
		return MeasurementUnitDocument.newBuilder()
				.withId("MeasurementUnitSnapshot.id")
				.withName("MeasurementUnitSnapshot.name");
	}
}
