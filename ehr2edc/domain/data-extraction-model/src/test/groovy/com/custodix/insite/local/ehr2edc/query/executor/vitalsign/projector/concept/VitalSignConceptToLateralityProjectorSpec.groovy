package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.concept

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.*

class VitalSignConceptToLateralityProjectorSpec extends Specification {
    def "Projecting a vital sign concept to its laterality"() {
        given: "A diastolic blood pressure concept with laterality #conceptLaterality"
        VitalSignConcept concept = diastolicBloodPressureConcept()

        when: "I project to the concept laterality"
        Optional<String> result = new VitalSignConceptToLateralityProjector().project(Optional.of(concept), aProjectionContext())

        then: "The concept laterality #conceptLaterality is returned"
        result.get() == conceptLaterality

        where:
        conceptLaterality = LATERALITY_LEFT
    }

    def "Projecting an empty concept"() {
        given: "An empty vital sign concept"

        when: "I project to the concept laterality"
        Optional<String> result = new VitalSignConceptToLateralityProjector().project(Optional.ofNullable(concept), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        concept                   | _
        null                      | _
        anEmptyVitalSignConcept() | _
    }
}
