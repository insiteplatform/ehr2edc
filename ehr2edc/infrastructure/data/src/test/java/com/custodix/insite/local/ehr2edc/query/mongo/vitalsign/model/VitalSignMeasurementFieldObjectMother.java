package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model;

import java.math.BigDecimal;

public class VitalSignMeasurementFieldObjectMother {

	public static final String BMI_UNIT = "kg/m2";
	public static final BigDecimal BMI_VALUE = BigDecimal.valueOf(18.5);
	public static final BigDecimal BMI_LLN = BigDecimal.valueOf(18.5);
	public static final BigDecimal BMI_ULN = BigDecimal.valueOf(25);

	public static VitalSignMeasurementField aNormalAdultBmiVitalSignMeasurementField() {
		return VitalSignMeasurementField.newBuilder()
				.withUnit(BMI_UNIT)
				.withValue(BMI_VALUE)
				.withLowerLimit(BMI_LLN)
				.withUpperLimit(BMI_ULN)
				.build();
	}

	public static VitalSignMeasurementField aDefaultVitalSignMeasurementField() {
		return VitalSignMeasurementField.newBuilder()
				.withUnit("anUnit")
				.withValue(BigDecimal.valueOf(123))
				.withLowerLimit(BigDecimal.valueOf(100))
				.withUpperLimit(BigDecimal.valueOf(250))
				.build();
	}
}
