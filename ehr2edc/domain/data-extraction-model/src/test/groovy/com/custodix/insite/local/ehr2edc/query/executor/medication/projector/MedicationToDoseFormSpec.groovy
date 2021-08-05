package com.custodix.insite.local.ehr2edc.query.executor.medication.projector

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationObjectMother.*
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class MedicationToDoseFormSpec extends Specification {
    def "Projecting a medication to its dose form"() {
        given: "An Omeprazole medication"
        Medication omeprazole = omeprazole()

        when: "I project to the dose form"
        Optional<String> result = new MedicationToDoseForm().project(Optional.of(omeprazole), aProjectionContext())

        then: "The dose form #doseForm is returned"
        result.get() == doseForm

        where:
        doseForm = DOSE_FORM_CAPSULE
    }

    def "Projecting an empty dose form"() {
        given: "An empty dose form"

        when: "I project to the dose form"
        Optional<String> result = new MedicationToDoseForm().project(Optional.ofNullable(medication), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        medication        | _
        null              | _
        anEmptyDoseForm() | _
    }

    def anEmptyDoseForm() {
        omeprazoleMedicationBuilder().withDoseForm(null).build()
    }
}
