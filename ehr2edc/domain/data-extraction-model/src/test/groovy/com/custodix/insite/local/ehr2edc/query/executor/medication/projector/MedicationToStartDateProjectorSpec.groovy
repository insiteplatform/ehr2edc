package com.custodix.insite.local.ehr2edc.query.executor.medication.projector

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

import static com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationObjectMother.omeprazoleMedicationBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class MedicationToStartDateProjectorSpec extends Specification {
    def "Projecting a start date"() {
        given: "A medicationwith an start date of 01-01-2000"
        Medication medication = aMedication(LocalDateTime.of(2000, 1, 1, 0, 0))

        when: "I project for the start date"
        Optional<LocalDateTime> startDate = new MedicationToStartDateProjector().project(Optional.of(medication), aProjectionContext())

        then: "01-01-2000 gets returned"
        startDate.isPresent()
        startDate.get() == LocalDateTime.of(2000, 1, 1, 0, 0)
    }

    @Unroll
    def "Projecting an empty start date"() {
        given: "An empty start date"

        when: "I project for the start date"
        Optional<LocalDateTime> startDate = new MedicationToStartDateProjector().project(Optional.ofNullable(medication), aProjectionContext())

        then: "An empty result is returned"
        !startDate.isPresent()

        where:
        medication        | _
        null              | _
        aMedication(null) | _
    }

    static aMedication(LocalDateTime startDate) {
        return omeprazoleMedicationBuilder()
                .withStartDate(startDate)
                .build()
    }
}
