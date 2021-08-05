package com.custodix.insite.local.ehr2edc.query.mongo.converter


import com.custodix.insite.local.ehr2edc.vocabulary.PatientExporterId
import spock.lang.Specification
import spock.lang.Unroll

class PatientExporterIdReaderConverterSpec extends Specification {

    private PatientExporterIdReaderConverter subjectIdConverter = new PatientExporterIdReaderConverter()

    @Unroll
    def "Convert patient exporter id correctly to string"(String patientExporterIdValue) {
        given: "Patient exporter id with value #patientExporterIdValue"

        when: "converting"
        def convertedPatientExporterId = subjectIdConverter.convert(patientExporterIdValue)

        then: "the converted patient exporter id value is #patientExporterIdValue"
        convertedPatientExporterId == PatientExporterId.of(patientExporterIdValue)

        where:
        patientExporterIdValue  | _
        "123-5678"      | _
    }

    @Unroll
    def "Throw exception when the patient exporter id value is null or blank"(String patientExporterIdValue) {
        given: "Subject id with value #subjectIdValue"

        when: "converting"
        def convertedPatientExporterId = subjectIdConverter.convert(patientExporterIdValue)

        then: "a system exception is thrown"
        convertedPatientExporterId == null

        where:
        patientExporterIdValue  | _
        null            | _
    }
}
