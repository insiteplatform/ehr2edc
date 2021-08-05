package com.custodix.insite.local.ehr2edc.query.executor.objectmother

import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement

class MeasurementObjectMother {
    public static final BigDecimal DIASTOLIC_BLOOD_PRESSURE_VALUE = 70.0
    public static final BigDecimal DIASTOLIC_BLOOD_PRESSURE_LLN = 60.0
    public static final BigDecimal DIASTOLIC_BLOOD_PRESSURE_ULN = 90.0
    public static final String DIASTOLIC_BLOOD_PRESSURE_UNIT = "mm[Hg]"
    public static final BigDecimal INSULIN_VALUE = 85
    public static final BigDecimal INSULIN_LLN = 75
    public static final BigDecimal INSULIN_ULN = 100
    public static final String INSULIN_UNIT = "mg/dL"

    static Measurement aMeasurement() {
        defaultMeasurementBuilder().build()
    }

    static Measurement aMeasurement(BigDecimal value, String unit) {
        Measurement.newBuilder()
                .withValue(value)
                .withUnit(unit)
                .build()
    }

    static Measurement diastolicBloodPressureMeasurement() {
        return Measurement.newBuilder()
                .withValue(DIASTOLIC_BLOOD_PRESSURE_VALUE)
                .withLowerLimit(DIASTOLIC_BLOOD_PRESSURE_LLN)
                .withUpperLimit(DIASTOLIC_BLOOD_PRESSURE_ULN)
                .withUnit(DIASTOLIC_BLOOD_PRESSURE_UNIT)
                .build()
    }

    static Measurement insulinMeasurement() {
        return Measurement.newBuilder()
                .withValue(INSULIN_VALUE)
                .withLowerLimit(INSULIN_LLN)
                .withUpperLimit(INSULIN_ULN)
                .withUnit(INSULIN_UNIT)
                .build()
    }

    static Measurement.Builder defaultMeasurementBuilder() {
        Measurement.newBuilder()
                .withValue(2.0)
                .withUnit("mg")
                .withLowerLimit(1.5)
                .withUpperLimit(2.5)
    }
}
