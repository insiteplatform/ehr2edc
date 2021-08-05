package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ConvertProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ConvertProjectedValueComposite
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import spock.lang.Specification
import spock.lang.Unroll

import java.math.RoundingMode

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectedValueObjectMother.aProjectedValue

class ConvertProjectedValueCompositeSpec extends Specification {
    @Unroll
    def "Converting a projected value with code '#code' for which a composed projector is mapped that converts its value '#value' in '#unit' to 'ug'"() {
        given: "A projected value with code '#code', value '#value' and unit '#unit'"
        ProjectedValue projectedValue = aProjectedValue(code, value, unit)
        and: "A mapping '#mapping' that maps the projected code to the composed projector"

        when: "I convert the projected value"
        Optional<ProjectedValue> result = new ConvertProjectedValueComposite(mapping).project(Optional.of(projectedValue), aProjectionContext())

        then: "A projected value with value '#expectedValue' and unit 'ug' is returned"
        result.isPresent()
        with(result.get()) {
            it.value.setScale(6, RoundingMode.HALF_UP) == expectedValue
            it.unit == "ug"
            it.code == projectedValue.code
        }

        where:
        code  | value                 | unit | mapping                                           || expectedValue
        "123" | new BigDecimal("2.0") | "mg" | [["123", "456"]: new ConvertProjectedValue("ug")] || new BigDecimal("2000")
    }

    @Unroll
    def "Converting a projected value with code '#code' for which no composed projector is mapped"() {
        given: "A projected value with code '#code', value '#value' and unit '#unit'"
        ProjectedValue projectedValue = aProjectedValue(code, value, unit)
        and: "A mapping '#mapping' that does not map the projected code to a composed projector"

        when: "I convert the projected value"
        Optional<ProjectedValue> result = new ConvertProjectedValueComposite(mapping).project(Optional.of(projectedValue), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        code  | value                 | unit | mapping
        "789" | new BigDecimal("2.0") | "mg" | [["123", "456"]: new ConvertProjectedValue("ug")]
        null  | new BigDecimal("2.0") | "mg" | [["123", "456"]: new ConvertProjectedValue("ug")]
    }
}
