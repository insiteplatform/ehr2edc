package com.custodix.insite.local.ehr2edc.query.executor.medication.projector

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationObjectMother.*
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class MedicationToDosingFrequencySpec extends Specification {
    def "Projecting a medication to its dosing frequency"() {
        given: "An Omeprazole medication"
        Medication omeprazole = omeprazole()

        when: "I project to the dosing frequency"
        Optional<String> result = new MedicationToDosingFrequency().project(Optional.of(omeprazole), aProjectionContext())

        then: "The dosing frequency #dosingFrequency is returned"
        result.get() == dosingFrequency

        where:
        dosingFrequency = DOSING_FREQUENCY_DAILY
    }

    def "Projecting an empty dosing frequency"() {
        given: "An empty dosing frequency"

        when: "I project to the dosing frequency"
        Optional<String> result = new MedicationToDosingFrequency().project(Optional.ofNullable(medication), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        medication               | _
        null                     | _
        anEmptyDosingFrequency() | _
    }

    def anEmptyDosingFrequency() {
        omeprazoleMedicationBuilder().withDosingFrequency(null).build()
    }
}
