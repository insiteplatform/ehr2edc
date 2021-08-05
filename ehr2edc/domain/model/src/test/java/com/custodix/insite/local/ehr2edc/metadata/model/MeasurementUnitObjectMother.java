package com.custodix.insite.local.ehr2edc.metadata.model;

public class MeasurementUnitObjectMother {

	public static MeasurementUnit aDefaultMeasurementUnit() {
		return MeasurementUnit.newBuilder()
				.withId("123-123")
				.withName("aName")
				.build();
	}

}