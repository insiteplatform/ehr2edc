package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement


import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.MapUnitToCommonModel
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValueField
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectedValueObjectMother.aProjectedValue

class MapUnitToCommonModelSpec extends Specification {
    @Unroll
    def "Mapping a ucum unit to the common model"() {
        given: "A ucum unit '#unit'"
        def projectedValue = aProjectedValue(code, value, unit)

        when: "I map it to the common model"
        Optional<ProjectedValue> result = new MapUnitToCommonModel(ProjectedValueField.UNIT, false).project(Optional.of(projectedValue), aProjectionContext())

        then: "I expect unit '#expectedUnit' to be returned"
        result.isPresent()
        with(result.get()) {
            it.value == value
            it.unit == expectedUnit
            it.code == code
        }

        where:
        unit               || expectedUnit
        "10^7/l"           || "10^7/L"
        "10^7/L"           || "10^7/L"
        "10^9.{organisms}" || "10^9 organisms"
        "A"                || "amp"
        "[drp]"            || "gtt"
        "d.ug/mL/(mg/g/d)" || "day*ug/mL/(mg/g/day)"
        and:
        value = 2.0
        code = "code1"
    }

    @Unroll
    def "Mapping an unknown unit should result in an empty result"() {
        given: "A invalid ucum unit '#unit'"
        def projectedValue = aProjectedValue(code, value, unit)

        when: "I map it to the common model"
        Optional<ProjectedValue> result = new MapUnitToCommonModel(ProjectedValueField.UNIT, false).project(Optional.of(projectedValue), aProjectionContext())

        then: "I expect unit no result"
        !result.isPresent()

        where:
        unit << ["10^7/day", "10^9.[bla]"]

        and:
        value = 2.0
        code = "code1"
    }
}
