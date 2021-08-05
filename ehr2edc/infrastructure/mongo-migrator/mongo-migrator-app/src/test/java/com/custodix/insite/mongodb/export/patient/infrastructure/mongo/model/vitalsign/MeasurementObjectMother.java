package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign;

import java.math.BigDecimal;

import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.measurement.Measurement;

public class MeasurementObjectMother {
	public static Measurement aDefaultMeasurement() {
		return aDefaultMeasurementBuilder()
				.build();
	}

	public static Measurement.Builder aDefaultMeasurementBuilder() {
		return Measurement.newBuilder()
				.withUnit("unit")
				.withValue(BigDecimal.ONE)
				.withUpperLimit(BigDecimal.TEN)
				.withLowerLimit(BigDecimal.ZERO);
	}
}
