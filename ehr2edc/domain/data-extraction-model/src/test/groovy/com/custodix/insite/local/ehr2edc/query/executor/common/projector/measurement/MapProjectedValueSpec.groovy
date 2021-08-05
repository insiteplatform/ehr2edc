package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.MapProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValueField
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectedValueObjectMother.*

class MapProjectedValueSpec extends Specification {
    @Unroll
    def "Mapping a projected value while skipping missing"() {
        given: "A projected value with value '#value' and code '#code' and unit '#unit'"
        def projectedValue = aProjectedValue(code, value, unit)

        when: "I map the field '#field' using the mapping '#mapping'"
        Optional<ProjectedValue> result = new MapProjectedValue(mapping, field, false).project(Optional.of(projectedValue), aProjectionContext())

        then: "A projected value with value '#expectedValue' and code '#expectedCode' and unit '#expectedUnit' is returned"
        result.isPresent()
        with(result.get()) {
            it.value == expectedValue
            it.unit == expectedUnit
            it.code == expectedCode
        }

        where:
        field                     | mapping                                || expectedValue | expectedCode | expectedUnit
        null                      | [2.0: "2.000"]                         || "2.000"       | "code1"      | "mg"
        ProjectedValueField.VALUE | [2.0: "2.000"]                         || "2.000"       | "code1"      | "mg"
        ProjectedValueField.UNIT  | ["mg": "[mg]", "kg": "[kg]"]           || 2.0           | "code1"      | "[mg]"
        ProjectedValueField.CODE  | ["code1": "code-1", "code2": "code-2"] || 2.0           | "code-1"     | "mg"
        and:
        value = 2.0
        code = "code1"
        unit = "mg"
    }

    @Unroll
    def "Mapping a projected value while taking over missing"() {
        given: "A projected value with value '#value' and code '#code' and unit '#unit'"
        def projectedValue = aProjectedValue(code, value, unit)

        when: "I map the field '#field' using the mapping '#mapping'"
        Optional<ProjectedValue> result = new MapProjectedValue(mapping, field, true).project(Optional.of(projectedValue), aProjectionContext())

        then: "A projected value with value '#expectedValue' and code '#expectedCode' and unit '#expectedUnit' is returned"
        result.isPresent()
        with(result.get()) {
            it.value == expectedValue
            it.unit == expectedUnit
            it.code == expectedCode
        }

        where:
        field                     | mapping                                || expectedValue | expectedCode | expectedUnit
        null                      | [2.0: "2.000"]                         || "2.000"       | "code1"      | "mg"
        ProjectedValueField.VALUE | [2.0: "2.000"]                         || "2.000"       | "code1"      | "mg"
        ProjectedValueField.UNIT  | ["mg": "[mg]", "kg": "[kg]"]           || 2.0           | "code1"      | "[mg]"
        ProjectedValueField.CODE  | ["code1": "code-1", "code2": "code-2"] || 2.0           | "code-1"     | "mg"
        and:
        value = 2.0
        code = "code1"
        unit = "mg"
    }

    @Unroll
    def "Mapping a projected value with missing or unmapped field '#field' while skipping missing"() {
        given: "A projected value with missing or unmapped field '#field'"

        when: "I map the field '#field' using the mapping '#mapping'"
        Optional<ProjectedValue> result = new MapProjectedValue(mapping, field, false).project(Optional.ofNullable(projectedValue), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        projectedValue                        | field                     | mapping
        null                                  | ProjectedValueField.VALUE | ["2.0": "2.000"]
        aProjectedValueWithNumericValue(null) | ProjectedValueField.VALUE | ["2.0": "2.000"]
        aProjectedValueWithNumericValue(5.0)  | ProjectedValueField.VALUE | ["2.0": "2.000"]
        null                                  | ProjectedValueField.UNIT  | ["mg": "[mg]", "kg": "[kg]"]
        aProjectedValueWithUnit(null)         | ProjectedValueField.UNIT  | ["mg": "[mg]", "kg": "[kg]"]
        aProjectedValueWithUnit("g")          | ProjectedValueField.UNIT  | ["mg": "[mg]", "kg": "[kg]"]
        null                                  | ProjectedValueField.CODE  | ["code1": "code-1", "code2": "code-2"]
        aProjectedValueWithCode(null)         | ProjectedValueField.CODE  | ["code1": "code-1", "code2": "code-2"]
        aProjectedValueWithCode("code3")      | ProjectedValueField.CODE  | ["code1": "code-1", "code2": "code-2"]
    }

    @Unroll
    def "Mapping a projected value with missing '#field' while taking over missing"() {
        given: "A projected value with missing or unmapped field '#field'"

        when: "I map the field '#field' using the mapping '#mapping'"
        Optional<ProjectedValue> result = new MapProjectedValue(mapping, field, true).project(Optional.ofNullable(projectedValue), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        projectedValue | field                     | mapping
        null           | ProjectedValueField.VALUE | ["2.0": "2.000"]
        null           | ProjectedValueField.UNIT  | ["mg": "[mg]", "kg": "[kg]"]
        null           | ProjectedValueField.CODE  | ["code1": "code-1", "code2": "code-2"]
    }

    @Unroll
    def "Mapping a projected value with unmapped field '#field' while taking over missing"() {
        given: "A projected value with missing or unmapped field '#field'"

        when: "I map the field '#field' using the mapping '#mapping'"
        Optional<ProjectedValue> result = new MapProjectedValue(mapping, field, true).project(Optional.ofNullable(projectedValue), aProjectionContext())

        then: "The expected result is returned"
        result.isPresent()
        field.getFieldValue(result.get()) == field.getFieldValue(projectedValue)

        where:
        projectedValue                        | field                     | mapping
        aProjectedValue("code3", "5.0", "g")  | ProjectedValueField.VALUE | ["2.0": "2.000"]
        aProjectedValue("code3", null, "g")   | ProjectedValueField.VALUE | ["2.0": "2.000"]
        aProjectedValue("code3", "5.0", "g")  | ProjectedValueField.UNIT  | ["mg": "[mg]", "kg": "[kg]"]
        aProjectedValue("code3", "5.0", null) | ProjectedValueField.UNIT  | ["mg": "[mg]", "kg": "[kg]"]
        aProjectedValue("code3", "5.0", "g")  | ProjectedValueField.CODE  | ["code1": "code-1", "code2": "code-2"]
        aProjectedValue(null, "5.0", "g")     | ProjectedValueField.CODE  | ["code1": "code-1", "code2": "code-2"]
    }
}
