package com.custodix.insite.local.ehr2edc.query.executor.common.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import spock.lang.Specification


class DoNotOutputBooleanIfFalseSpec extends Specification {

    def "True projects to true"() {
        given: "True"
        ProjectedValue<Boolean> given = new ProjectedValue(Boolean.TRUE, "unit", "code")

        when: "The value is projected"
        def result = new DoNotOutputBooleanIfFalse().project(Optional.of(given), ProjectionContext.newBuilder().build())

        then: "True is expected"
        result.present
        result.ifPresent {
            assert it.value == given.value
            assert it.unit == given.unit
            assert it.code == given.code
        }
    }

    def "False projects to empty optional"() {
        given: "False"
        Optional<ProjectedValue<Boolean>> given = Optional.of(new ProjectedValue(Boolean.FALSE, "unit", "code"))

        when: "The value is projected"
        def result = new DoNotOutputBooleanIfFalse().project(given, ProjectionContext.newBuilder().build())

        then: "Empty optional is expected"
        !result.present

    }

    def "Empty optional projects to empty optional"() {
        given: "An empty optional boolean"
        Optional<Boolean> given = Optional.empty()

        when: "The value is projected"
        def result = new DoNotOutputBooleanIfFalse().project(given, ProjectionContext.newBuilder().build())

        then: "Empty optional is expected"
        !result.present
    }
}
