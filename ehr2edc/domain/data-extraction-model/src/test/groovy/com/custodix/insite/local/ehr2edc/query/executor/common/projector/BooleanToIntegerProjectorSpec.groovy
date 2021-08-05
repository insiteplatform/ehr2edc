package com.custodix.insite.local.ehr2edc.query.executor.common.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import spock.lang.Specification
import spock.lang.Unroll

class BooleanToIntegerProjectorSpec extends Specification {

    @Unroll("Projecting '#given.value' to an integer results in: '#expected'")
    def "Can project an optional boolean to an integer"() {
        when: "The value is projected to an Integer"
        def result = booleanToInteger().project(Optional.of(given), ProjectionContext.newBuilder().build())

        then: "The result should match the expected value"
        result.isPresent()
        result.ifPresent {
            assert it.value == expected
            assert it.unit == given.unit
            assert it.code == given.code
        }


        where:
        given                                     | expected
        new ProjectedValue(true, "unit", "code")  | 1
        new ProjectedValue(false, "unit", "code") | 0
    }

    def "Can project an empty projectedValue to an empty integer"() {
        given: "An empty optional boolean"
        Optional given = Optional.empty()

        when: "The value is projected to NY"
        def result = booleanToInteger().project(given, ProjectionContext.newBuilder().build())

        then: "The result should match the expected value"
        !result.isPresent()
    }

    def booleanToInteger() {
        return new BooleanToIntegerProjector()
    }
}
