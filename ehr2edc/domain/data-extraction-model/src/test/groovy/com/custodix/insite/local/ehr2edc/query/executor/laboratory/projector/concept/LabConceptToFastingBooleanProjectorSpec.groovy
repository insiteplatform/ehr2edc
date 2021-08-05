package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.labConceptBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus.*
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class LabConceptToFastingBooleanProjectorSpec extends Specification {
    @Unroll
    def "Projecting a concept fasting boolean from fasting status #conceptFastingStatus"() {
        given: "A concept fasting status #conceptFastingStatus"
        LabConcept labConcept = aLabConcept(conceptFastingStatus)

        when: "I project for the concept fasting status"
        Optional<Boolean> fasting = new LabConceptToFastingBooleanProjector().project(Optional.of(labConcept), aProjectionContext())

        then: "The concept fasting boolean is #expectedFastingBoolean"
        fasting == expectedFastingBoolean

        where:
        conceptFastingStatus | expectedFastingBoolean
        FASTING              | Optional.of(true)
        NOT_FASTING          | Optional.of(false)
        UNDEFINED            | Optional.empty()
    }

    @Unroll
    def "Projecting an empty concept fasting status"() {
        given: "An empty concept fasting status"

        when: "I project for the concept fasting status"
        Optional<Boolean> fasting = new LabConceptToFastingBooleanProjector().project(Optional.ofNullable(labConcept), aProjectionContext())

        then: "An empty result is returned"
        !fasting.isPresent()

        where:
        labConcept        | _
        null              | _
        aLabConcept(null) | _
    }

    static aLabConcept(FastingStatus fastingStatus) {
        labConceptBuilder()
                .withFastingStatus(fastingStatus)
                .build()
    }
}
