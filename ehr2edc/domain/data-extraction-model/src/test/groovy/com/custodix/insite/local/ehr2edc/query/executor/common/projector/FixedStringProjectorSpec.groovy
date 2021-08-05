package com.custodix.insite.local.ehr2edc.query.executor.common.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import spock.lang.Specification

class FixedStringProjectorSpec extends Specification {

    def "Returns an empty result for empty input"() {
        given: "A fixedString \"fixed\" projector"
        def projector = new FixedStringProjector("fixed");
        and: "Empty input for the projector"
        def input = Optional.empty()

        when: "Executing the projection"
        Optional<String> result = projector.project(input, aProjectionContext())

        then: "The result is empty"
        !result.isPresent()
    }

    def "Returns the provided string for existing input"() {
        given: "A fixedString \"fixed\" projector"
        def projector = new FixedStringProjector("fixed");
        and: "Non-empty input for the projector"
        def input = Optional.of(new Object())

        when: "Executing the projection"
        Optional<String> result = projector.project(input, aProjectionContext())

        then: "The result is not empty"
        result.isPresent()
        and: "The result contains the fixed string"
        result.get() == "fixed"
    }

    def aProjectionContext() {
        ProjectionContext.newBuilder().build()
    }
}
