package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model;

import java.math.BigDecimal;

public class LabMeasurementObjectMother {
	public static final BigDecimal INSULIN_VALUE = new BigDecimal("85");
	public static final BigDecimal INSULIN_LLN = new BigDecimal("75");
	public static final BigDecimal INSULIN_ULN = new BigDecimal("100");
	public static final String INSULIN_UNIT = "mg/dL";

	public static LabMeasurementField aDefaultLabMeasurement() {
		return aDefaultLabMeasurementBuilder().build();
	}

	public static LabMeasurementField.Builder aDefaultLabMeasurementBuilder() {
		return LabMeasurementField.newBuilder()
				.withLowerLimit(BigDecimal.ZERO)
				.withUnit("UNIT")
				.withUpperLimit(BigDecimal.TEN)
				.withValue(BigDecimal.ONE);
	}

	public static LabMeasurementField insulinMeasurement() {
		return LabMeasurementField.newBuilder()
				.withValue(INSULIN_VALUE)
				.withLowerLimit(INSULIN_LLN)
				.withUpperLimit(INSULIN_ULN)
				.withUnit(INSULIN_UNIT)
				.build();
	}
}
