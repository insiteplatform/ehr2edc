package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.concept

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.*

class VitalSignToVitalSignConceptProjectorSpec extends Specification {
    def "Projecting a vital sign to its concept"() {
        given: "A diastolic blood pressure finding"
        VitalSign vitalSign = diastolicBloodPressure()

        when: "I project to the concept"
        Optional<VitalSignConcept> result = new VitalSignToVitalSignConceptProjector().project(Optional.of(vitalSign), aProjectionContext())

        then: "The concept is returned"
        VitalSignConcept concept = result.get()
        and: "with code #code"
        concept.concept.code == code
        and: "with location #location"
        concept.location == location
        and: "with laterality #laterality"
        concept.laterality == laterality
        and: "with position #position"
        concept.position == position

        where:
        code = DIASTOLIC_BLOOD_PRESSURE_CODE
        location = LOCATION_ARM
        laterality = LATERALITY_LEFT
        position = POSITION_SITTING
    }

    def "Projecting an empty vital sign"() {
        given: "An empty vital sign"

        when: "I project to the concept"
        Optional<VitalSignConcept> result = new VitalSignToVitalSignConceptProjector().project(Optional.ofNullable(vitalSign), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        vitalSign          | _
        null               | _
        anEmptyVitalSign() | _
    }
}
