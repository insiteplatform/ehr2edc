package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ConvertProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import spock.lang.Specification
import spock.lang.Unroll

import java.math.RoundingMode

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectedValueObjectMother.aProjectedValueWithValueAndUnit

class ConvertProjectedValueSpec extends Specification {
    @Unroll
    def "Converting a projected value #sourceValue and unit #sourceUnit to its value in specified unit #targetUnit"(
            BigDecimal sourceValue, String sourceUnit, String targetUnit, BigDecimal expectedValue) {
        given: "A projected value #sourceValue and unit #sourceUnit"
        ProjectedValue projectedValue = aProjectedValueWithValueAndUnit(sourceValue, sourceUnit)

        when: "I convert the projected value in unit #targetUnit"
        Optional<ProjectedValue> value = convert(targetUnit).project(Optional.of(projectedValue), aProjectionContext())

        then: "A projected value with value '#expectedValue' and unit '#targetUnit' is returned"
        value.isPresent()
        with(value.get()) {
            it.value.setScale(6, RoundingMode.HALF_UP) == expectedValue
            it.unit == targetUnit
            it.code == projectedValue.code
        }

        where:
        sourceValue           | sourceUnit | targetUnit || expectedValue
        new BigDecimal("2.0") | "mg"       | "kg"       || new BigDecimal("0.000002")
        new BigDecimal("2.0") | "mg"       | "g"        || new BigDecimal("0.002")
        new BigDecimal("2.0") | "mg"       | "mg"       || new BigDecimal("2.0")
        new BigDecimal("2.0") | "mg"       | "ug"       || new BigDecimal("2000")
    }

    @Unroll
    def "Converting a projected value with unit #sourceUnit to its value in specified unit #targetUnit between which no conversion is possible"() {
        given: "A projected value with unit #sourceUnit"
        ProjectedValue projectedValue = aProjectedValueWithValueAndUnit(new BigDecimal("2.0"), sourceUnit)

        when: "I convert the projected value in unit #targetUnit"
        Optional<ProjectedValue> value = convert(targetUnit).project(Optional.of(projectedValue), aProjectionContext())

        then: "An empty result is returned"
        !value.isPresent()

        where:
        sourceUnit    | targetUnit
        "kg"          | "cm"
        "g"           | "invalid_ucum"
        "invalid_ucm" | "g"
    }

    @Unroll
    def "Converting an empty projected value or unit in a specified unit"() {
        given: "An empty projected value or unit"

        when: "I convert the projected value in a specified unit"
        Optional<ProjectedValue> value = convert("ug").project(Optional.ofNullable(projectedValue), aProjectionContext())

        then: "An empty result is returned"
        !value.isPresent()

        where:
        projectedValue                                               | _
        null                                                         | _
        aProjectedValueWithValueAndUnit(null, null)                  | _
        aProjectedValueWithValueAndUnit(null, "mg")                  | _
        aProjectedValueWithValueAndUnit(new BigDecimal("2.0"), null) | _
        aProjectedValueWithValueAndUnit(new BigDecimal("2.0"), "")   | _
    }

    def convert(String unit) {
        return new ConvertProjectedValue(unit)
    }
}
