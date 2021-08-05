package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.SetProjectedValueUnit
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectedValueObjectMother.aProjectedValue

class SetProjectedValueUnitSpec extends Specification {
    def "Setting a projected value to a specified unit"() {
        given: "A projected value"
        ProjectedValue projectedValue = aProjectedValue()

        when: "I set a specified unit"
        def unit = "13899.CM/IN.cm"
        Optional<ProjectedValue> result = new SetProjectedValueUnit(unit).project(Optional.of(projectedValue), aProjectionContext())

        then: "The specified unit is returned"
        result.isPresent()
        with(result.get()) {
            it.value == projectedValue.value
            it.unit == unit
            it.code == projectedValue.code
        }
    }

    @Unroll
    def "Setting an empty projected value to a specified unit"() {
        given: "An empty projected value"

        when: "I set a specified unit"
        def unit = "13899.CM/IN.cm"
        Optional<ProjectedValue> result = new SetProjectedValueUnit(unit).project(Optional.ofNullable(projectedValue), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        projectedValue | _
        null           | _
    }
}
