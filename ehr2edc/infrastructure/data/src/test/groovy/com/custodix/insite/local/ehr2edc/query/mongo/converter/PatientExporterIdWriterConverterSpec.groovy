package com.custodix.insite.local.ehr2edc.query.mongo.converter


import com.custodix.insite.local.ehr2edc.vocabulary.PatientExporterId
import spock.lang.Specification
import spock.lang.Unroll

class PatientExporterIdWriterConverterSpec extends Specification {

    private PatientExporterIdWriterConverter patientExporterIdConverter = new PatientExporterIdWriterConverter()

    @Unroll
    def "Convert patient exporter id correctly to string"(String patientExporterIdValue) {
        given: "Patient exporter id with value #patientExporterIdValue"
        PatientExporterId patientExporterId = PatientExporterId.of(patientExporterIdValue)

        when: "converting"
        def convertedPatientExporterId = patientExporterIdConverter.convert(patientExporterId)

        then: "the converted patient exporter id value is #patientExporterIdValue"
        convertedPatientExporterId == patientExporterIdValue

        where:
        patientExporterIdValue | _
        "123-5678" | _
    }
}
