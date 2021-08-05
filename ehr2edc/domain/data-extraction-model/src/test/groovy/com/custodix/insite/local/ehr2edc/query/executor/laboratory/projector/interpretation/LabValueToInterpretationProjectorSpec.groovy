package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.interpretation

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValueInterpretation
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.anInterpretation
import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.insulinLabValueBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class LabValueToInterpretationProjectorSpec extends Specification {
    def "Projecting a lab value to its interpretation"() {
        given: "A lab value with a interpretation"
        LabValue labValue = insulinLabValueBuilder()
                .withQualitativeResult(labInterpretation)
                .build()

        when: "I project for the interpretation"
        Optional<LabValueInterpretation> interpretation = new LabValueToInterpretationProjector().project(Optional.of(labValue), aProjectionContext())

        then: "The interpretation is returned"
        interpretation.isPresent()
        with(interpretation.get()) {
            parsedInterpretation == labInterpretation.parsedInterpretation
            originalInterpretation == labInterpretation.originalInterpretation
        }

        where:
        labInterpretation = anInterpretation()
    }

    @Unroll
    def "Projecting an empty lab value interpretation"() {
        given: "An empty lab value interpretation"

        when: "I project for the interpretation"
        Optional<LabValueInterpretation> interpretation = new LabValueToInterpretationProjector().project(Optional.ofNullable(labValue), aProjectionContext())

        then: "An empty result is returned"
        !interpretation.isPresent()

        where:
        labValue        | _
        null            | _
        aLabValue(null) | _
    }

    static aLabValue(LabValueInterpretation interpretation) {
        insulinLabValueBuilder()
                .withQualitativeResult(interpretation)
                .build()
    }
}
