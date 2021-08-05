package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue;

import java.math.BigDecimal;

public class LabMeasurementObjectMother {
	public static LabMeasurement aDefaultLabMeasurement() {
		return aDefaultLabMeasurementBuilder().build();
	}

	public static LabMeasurement.Builder aDefaultLabMeasurementBuilder() {
		return LabMeasurement.newBuilder()
				.withLowerLimit(BigDecimal.ZERO)
				.withUnit("UNIT")
				.withUpperLimit(BigDecimal.TEN)
				.withValue(BigDecimal.ONE);
	}
}
