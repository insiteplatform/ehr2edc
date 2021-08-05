package com.custodix.insite.local.ehr2edc.query.executor.common.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import spock.lang.Specification
import spock.lang.Unroll

class NegateBooleanSpec extends Specification {

    @Unroll("Negating '#given.value' results in: '#expected'")
    def "Can negate a boolean projectedValue"() {
        when: "The value is negated"
        def result = new NegateBoolean().project(Optional.of(given), ProjectionContext.newBuilder().build())

        then: "The result should match the expected value"
        result.isPresent()
        result.ifPresent {
            assert it.value == expected
            assert it.unit == given.unit
            assert it.code == given.code
        }

        where:
        given                                   | expected
        new ProjectedValue(true, null, "code")  | false
        new ProjectedValue(false, null, "code") | true
    }

    def "Projecting an empty optional results in an empty optional"() {
        given: "An empty optional"
        Optional given = Optional.empty()

        when: "The value is negated"
        def result = new NegateBoolean().project(given, ProjectionContext.newBuilder().build())

        then: "The result should not be present"
        !result.isPresent()
    }

}
