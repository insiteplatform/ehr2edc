package com.custodix.insite.local.ehr2edc.query.executor.medication.projector

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationObjectMother.omeprazole
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class LastMedicationProjectorSpec extends Specification {

    def "Deprecated LastMedicationProjector act as Identity-function"() {
        expect: "The result of the projector to equal the passed Result"
        medication == new LastMedicationProjector().project(medication, aProjectionContext())

        where:
        medication << [
                null,
                Optional.empty(),
                Optional.of(omeprazole()),
                Optional.of(Medication.newBuilder().build())
        ]
    }
}
