package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.interpretation

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValueInterpretation
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.insulinLabValueBuilder

class InterpretationToIsPositiveBooleanProjectorSpec extends Specification {
    @Unroll("Projecting the parsed, qualitative interpretation '#value' of a labvalue to a boolean, returns: #expected")
    def "Projecting the parsed interpretation when a qualitative result is present"() {
        given: "A lab value with parsed interpretation '#value'"
        LabValue labValue = insulinLabValueBuilder()
                .withQualitativeResult(LabValueInterpretation.newBuilder()
                        .withParsedInterpretation(value)
                        .build())
                .build()

        when: "I project the labvalue to isPositiveInterpretation"
        Optional<LabValueInterpretation> interpretation = new LabValueToInterpretationProjector().project(Optional.of(labValue), ProjectionContext.newBuilder().build())
        Optional<Boolean> result = new InterpretationToIsPositiveBooleanProjector().project(interpretation, ProjectionContext.newBuilder().build())

        then: "The expected result is returned"
        result == expected

        where:
        value || expected
        1     || Optional.ofNullable(true)
        0     || Optional.ofNullable(false)
        2     || Optional.ofNullable(null)
        null  || Optional.ofNullable(null)
    }

    def "Projecting the interpretation when no parsed interpretation is present"() {
        given: "A lab value without parsed interpretation"
        LabValue labValue = insulinLabValueBuilder()
                .withQualitativeResult(LabValueInterpretation.newBuilder()
                        .withOriginalInterpretation("no parsed interpretation")
                        .build())
                .build()

        when: "I project the labvalue to isPositiveInterpretation"
        Optional<LabValueInterpretation> interpretation = new LabValueToInterpretationProjector().project(Optional.of(labValue), ProjectionContext.newBuilder().build())
        Optional<Boolean> result = new InterpretationToIsPositiveBooleanProjector().project(interpretation, ProjectionContext.newBuilder().build())

        then: "No result is present"
        !result.isPresent()
    }
}
