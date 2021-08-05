package com.custodix.insite.local.ehr2edc.query.executor.objectmother

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue

import java.time.temporal.TemporalAccessor

class ProjectedValueObjectMother {
    private static final BigDecimal VALUE = 2.0
    private static final String UNIT = "mg"
    private static final String CODE = "123456"

    static ProjectedValue aProjectedValue() {
        return new ProjectedValue(VALUE, UNIT, CODE)
    }

    static ProjectedValue aProjectedValueWithDateValue(TemporalAccessor value) {
        return new ProjectedValue(value, null, CODE)
    }

    static ProjectedValue aProjectedValueWithNumericValue(BigDecimal value) {
        return new ProjectedValue(value, UNIT, CODE)
    }

    static ProjectedValue aProjectedValueWithLabeledValue(LabeledValue value) {
        return new ProjectedValue(value, UNIT, CODE)
    }

    static ProjectedValue aProjectedValueWithUnit(String unit) {
        return new ProjectedValue(VALUE, unit, CODE)
    }

    static ProjectedValue aProjectedValueWithCode(String code) {
        return new ProjectedValue(VALUE, UNIT, code)
    }

    static ProjectedValue aProjectedValueWithValueAndUnit(BigDecimal value, String unit) {
        return new ProjectedValue(value, unit, CODE)
    }

    static ProjectedValue aProjectedValueWithCodeAndUnit(String code, String unit) {
        return new ProjectedValue(VALUE, unit, code)
    }

    static <T> ProjectedValue aProjectedValue(String code, T value, String unit) {
        return new ProjectedValue<T>(value, unit, code)
    }
}
