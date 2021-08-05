package com.custodix.insite.local.ehr2edc.query.executor.medication.projector

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationObjectMother.*
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class MedicationToAdministrationRouteSpec extends Specification {
    def "Projecting a medication to its administration route"() {
        given: "An Omeprazole medication"
        Medication omeprazole = omeprazole()

        when: "I project to the administration route"
        Optional<String> result = new MedicationToAdministrationRoute().project(Optional.of(omeprazole), aProjectionContext())

        then: "The administration route #administrationRoute is returned"
        result.get() == administrationRoute

        where:
        administrationRoute = ADMINISTRATION_ROUTE_ORAL
    }

    def "Projecting an empty administration route"() {
        given: "An empty administration route"

        when: "I project to the administration route"
        Optional<String> result = new MedicationToAdministrationRoute().project(Optional.ofNullable(medication), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        medication                   | _
        null                         | _
        anEmptyAdministrationRoute() | _
    }

    def anEmptyAdministrationRoute() {
        omeprazoleMedicationBuilder().withAdministrationRoute(null).build()
    }
}
