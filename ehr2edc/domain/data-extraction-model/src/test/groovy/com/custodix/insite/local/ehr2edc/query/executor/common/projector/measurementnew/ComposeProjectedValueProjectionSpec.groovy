package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.DateToStringProjector
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectedValueObjectMother.aProjectedValueWithDateValue

class ComposeProjectedValueProjectionSpec extends Specification {
    @Unroll
    def "Composing a projector for a projected value"() {
        given: "A projected value with a date representing 2019/12/10"
        ProjectedValue projectedValue = aProjectedValueWithDateValue(LocalDate.of(2019, 12, 10))
        and: "A DateToString projector with pattern 'dd-MM-yyyy'"
        DateToStringProjector projector = new DateToStringProjector("dd-MM-yyyy")

        when: "I compose the projector"
        Optional<ProjectedValue> result = new ComposeProjectedValueProjection(projector).project(Optional.of(projectedValue), aProjectionContext())

        then: "The composed projector has projected its value to '10-12-2019'"
        result.isPresent()
        with(result.get()) {
            it.value == "10-12-2019"
            it.unit == projectedValue.unit
            it.code == projectedValue.code
        }
    }

    @Unroll
    def "Composing a projector for an empty projected value"() {
        given: "An empty projected value"
        and: "A DateToString projector with pattern 'dd-MM-yyyy'"
        DateToStringProjector projector = new DateToStringProjector("dd-MM-yyyy")

        when: "I compose the projector"
        Optional<ProjectedValue> result = new ComposeProjectedValueProjection(projector).project(Optional.ofNullable(projectedValue), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        projectedValue | _
        null           | _
    }
}
