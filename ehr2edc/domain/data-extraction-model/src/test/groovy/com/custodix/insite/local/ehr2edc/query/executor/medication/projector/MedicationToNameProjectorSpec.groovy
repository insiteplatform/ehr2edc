package com.custodix.insite.local.ehr2edc.query.executor.medication.projector

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationObjectMother.omeprazoleMedicationBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationObjectMother.omeprazoleMedicationConceptBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class MedicationToNameProjectorSpec extends Specification {
    def "Projecting a name"() {
        given: "A medication with a name"
        Medication medication = aMedication(medicationName)

        when: "I project for the name"
        Optional<String> medicationNameResult = new MedicationToNameProjector().project(Optional.of(medication), aProjectionContext())

        then: "'#medicationName' gets returned"
        medicationNameResult.isPresent()
        medicationNameResult.get() == medicationName

        where:
        medicationName = "omeprazole"
    }

    @Unroll
    def "Projecting a empty name"() {
        given: "An empty name"

        when: "I project for the name"
        Optional<String> medicationName = new MedicationToNameProjector().project(Optional.ofNullable(medication), aProjectionContext())

        then: "An empty result is returned"
        !medicationName.isPresent()

        where:
        medication                   | _
        null                         | _
        aMedication(null)            | _
        aMedicationWithNullConcept() | _
    }

    def aMedicationWithNullConcept() {
        omeprazoleMedicationBuilder()
                .withConcept(null)
                .build()
    }

    def aMedication(String name) {
        return omeprazoleMedicationBuilder()
                .withConcept(omeprazoleMedicationConceptBuilder().withName(name).build())
                .build()
    }
}
