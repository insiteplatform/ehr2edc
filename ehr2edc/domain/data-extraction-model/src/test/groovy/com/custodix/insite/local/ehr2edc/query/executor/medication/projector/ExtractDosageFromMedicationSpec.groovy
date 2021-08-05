package com.custodix.insite.local.ehr2edc.query.executor.medication.projector

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationObjectMother.omeprazoleMedicationBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class ExtractDosageFromMedicationSpec extends Specification {
    def "Projecting a dosage"() {
        given: "A medication with a dosage of value #value and unit #unit"
        Medication medication = aMedication(Dosage.newBuilder().withValue(value).withUnit(unit).build())

        when: "I project for dosage"
        Optional<Dosage> dosage = new ExtractDosageFromMedication().project(Optional.of(medication), aProjectionContext())

        then: "#value #unit gets returned"
        dosage.isPresent()
        dosage.get().value == 10.0
        dosage.get().unit == "unit"

        where:
        value = 10.0
        unit = "unit"
    }

    @Unroll
    def "Projecting an empty dosage"() {
        given: "An empty dosage"

        when: "I project for the dosage"
        Optional<Dosage> dosage = new ExtractDosageFromMedication().project(Optional.ofNullable(medication), aProjectionContext())

        then: "An empty result is returned"
        !dosage.isPresent()

        where:
        medication        | _
        null              | _
        aMedication(null) | _
    }

    static aMedication(Dosage dosage) {
        return omeprazoleMedicationBuilder()
                .withDosage(dosage)
                .build()
    }
}
