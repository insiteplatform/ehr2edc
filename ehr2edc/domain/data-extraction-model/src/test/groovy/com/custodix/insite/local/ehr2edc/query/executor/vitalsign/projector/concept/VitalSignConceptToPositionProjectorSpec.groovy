package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.concept

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.*

class VitalSignConceptToPositionProjectorSpec extends Specification {
    def "Projecting a vital sign concept to its position"() {
        given: "A diastolic blood pressure concept with position #conceptPosition"
        VitalSignConcept concept = diastolicBloodPressureConcept()

        when: "I project to the concept position"
        Optional<String> result = new VitalSignConceptToPositionProjector().project(Optional.of(concept), aProjectionContext())

        then: "The concept position #conceptPosition is returned"
        result.get() == conceptPostion

        where:
        conceptPostion = POSITION_SITTING
    }

    def "Projecting an empty concept"() {
        given: "An empty vital sign concept"

        when: "I project to the concept position"
        Optional<String> result = new VitalSignConceptToPositionProjector().project(Optional.ofNullable(concept), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        concept                   | _
        null                      | _
        anEmptyVitalSignConcept() | _
    }
}
