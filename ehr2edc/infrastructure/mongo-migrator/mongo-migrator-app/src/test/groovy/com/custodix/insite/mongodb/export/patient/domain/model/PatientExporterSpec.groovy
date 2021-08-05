package com.custodix.insite.mongodb.export.patient.domain.model

import com.custodix.insite.mongodb.export.patient.domain.service.ExportPatientRunner
import spock.lang.Specification

class PatientExporterSpec extends Specification {

    def "When patient exporter is instantiated with a id."() {
        given: "a patient export runner"
        ExportPatientRunner exportPatientRunner = Mock()

        when: "instantiating a patient exporter"
        def patientExporter = PatientExporter.getInstance(exportPatientRunner)

        then: "the patient exporter id is not null"
        patientExporter.id != null
    }
}
