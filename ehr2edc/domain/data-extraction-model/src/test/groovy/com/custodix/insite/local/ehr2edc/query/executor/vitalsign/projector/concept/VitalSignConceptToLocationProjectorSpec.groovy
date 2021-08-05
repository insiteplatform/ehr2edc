package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.concept

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.*

class VitalSignConceptToLocationProjectorSpec extends Specification {
    def "Projecting a vital sign concept to its location"() {
        given: "A diastolic blood pressure concept with location #conceptLocation"
        VitalSignConcept concept = diastolicBloodPressureConcept()

        when: "I project to the concept location"
        Optional<String> result = new VitalSignConceptToLocationProjector().project(Optional.of(concept), aProjectionContext())

        then: "The concept location #conceptLocation is returned"
        result.get() == conceptLocation

        where:
        conceptLocation = LOCATION_ARM
    }

    def "Projecting an empty concept"() {
        given: "An empty vital sign concept"

        when: "I project to the concept location"
        Optional<String> result = new VitalSignConceptToLocationProjector().project(Optional.ofNullable(concept), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        concept                   | _
        null                      | _
        anEmptyVitalSignConcept() | _
    }
}
