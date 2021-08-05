package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign
import spock.lang.Specification

import java.time.LocalDateTime

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.*

class VitalSignToDateProjectorSpec extends Specification {
    def "Projecting a vital sign to its measurement date"() {
        given: "A diastolic blood pressure finding on #measurementDate"
        VitalSign vitalSign = diastolicBloodPressure()

        when: "I project to the measurement date"
        Optional<LocalDateTime> result = new VitalSignToDateProjector().project(Optional.of(vitalSign), aProjectionContext())

        then: "The measurement date #measurementDate is returned"
        result.get() == measurementDate

        where:
        measurementDate = TIMESTAMP
    }

    def "Projecting an empty vital sign"() {
        given: "An empty vital sign"

        when: "I project to the measurement date"
        Optional<LocalDateTime> date = new VitalSignToDateProjector().project(Optional.ofNullable(vitalSign), aProjectionContext())

        then: "An empty result is returned"
        !date.isPresent()

        where:
        vitalSign          | _
        null               | _
        anEmptyVitalSign() | _
    }
}
