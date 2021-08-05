package com.custodix.insite.local.ehr2edc.query.executor.medication.projector

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

import static com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationObjectMother.omeprazoleMedicationBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class MedicationToEndDateProjectorSpec extends Specification {
    def "Projecting a end date"() {
        given: "A medication with an end date of 01-01-2010"
        Medication medication = aMedication(LocalDateTime.of(2010, 1, 1, 0, 0))

        when: "I project for the end date"
        Optional<LocalDateTime> endDate = new MedicationToEndDateProjector().project(Optional.of(medication), aProjectionContext())

        then: "01-01-2010 gets returned"
        endDate.isPresent()
        endDate.get() == LocalDateTime.of(2010, 1, 1, 0, 0)
    }

    @Unroll
    def "Projecting an empty end date"() {
        given: "An empty end date"

        when: "I project for the end date"
        Optional<LocalDateTime> endDate = new MedicationToEndDateProjector().project(Optional.ofNullable(medication), aProjectionContext())

        then: "An empty result is returned"
        !endDate.isPresent()

        where:
        medication        | _
        null              | _
        aMedication(null) | _
    }

    static aMedication(LocalDateTime endDate) {
        return omeprazoleMedicationBuilder()
                .withEndDate(endDate)
                .build()
    }
}
