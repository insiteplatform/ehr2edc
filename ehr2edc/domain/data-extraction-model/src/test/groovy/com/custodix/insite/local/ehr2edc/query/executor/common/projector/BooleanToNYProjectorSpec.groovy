package com.custodix.insite.local.ehr2edc.query.executor.common.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import spock.lang.Specification
import spock.lang.Unroll

class BooleanToNYProjectorSpec extends Specification {

    @Unroll("Projecting '#given' to NY results in: '#expected'")
    def "Can project an optional boolean to an NY-code"() {
        when: "The value is projected to NY"
        def result = booleanToNY().project(given, ProjectionContext.newBuilder().build())

        then: "The result should match the expected value"
        result.isPresent()
        result.get() == expected

        where:
        given              | expected
        Optional.of(true)  | "Y"
        Optional.of(false) | "N"
        Optional.empty()   | "U"
    }

    def booleanToNY() {
        return new BooleanToNYProjector()
    }
}
