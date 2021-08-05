package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.concept

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.*

class VitalSignConceptToCodeProjectorSpec extends Specification {
    def "Projecting a vital sign concept to its code"() {
        given: "A diastolic blood pressure concept with code #conceptCode"
        VitalSignConcept concept = diastolicBloodPressureConcept()

        when: "I project to the concept code"
        Optional<String> result = new VitalSignConceptToCodeProjector().project(Optional.of(concept), aProjectionContext())

        then: "The concept code #conceptCode is returned"
        result.get() == conceptCode

        where:
        conceptCode = DIASTOLIC_BLOOD_PRESSURE_CODE
    }

    def "Projecting an empty concept"() {
        given: "An empty vital sign concept"

        when: "I project to the concept code"
        Optional<String> result = new VitalSignConceptToCodeProjector().project(Optional.ofNullable(concept), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        concept                       | _
        null                          | _
        anEmptyVitalSignConcept()     | _
        anEmptyVitalSignConceptCode() | _
    }
}
