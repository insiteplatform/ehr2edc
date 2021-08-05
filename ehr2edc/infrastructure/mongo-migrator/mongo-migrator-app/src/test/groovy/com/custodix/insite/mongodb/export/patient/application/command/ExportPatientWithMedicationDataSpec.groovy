package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication.MedicationDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.MedicationDocumentRepository
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

class ExportPatientWithMedicationDataSpec extends AbstractExportPatientSpec {

    private static final String PATIENT_ID = "7"
    private static final String NAMESPACE = "MASTER_PATIENT_INDEX"

    @Autowired
    MedicationDocumentRepository medicationDocumentRepository

    @Autowired
    ExportPatient exportPatient

    def "I can register subject with medication data"(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "request"
        def request = ExportPatient.Request.newBuilder()
                .withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId))
                .build()

        when: "exporting patient"
        exportPatient.export(request)

        then: "medication documents are exported"
        List<MedicationDocument> medicationDocuments = medicationDocumentRepository.findAll()
        medicationDocuments.size() == 4

        medicationDocuments.each {
            assert it.subjectId == subjectId
            assert it.startDate == LocalDate.parse('2016-03-02').atStartOfDay()
            assert it.endDate == LocalDate.parse('2016-03-02').atStartOfDay()
            assert it.concept.concept.code == 'A10A'
            assert it.concept.name == "INS LEVEMIR_INNOLET 100U/ML 3ML PF PEN"
            // TODO: E2E-351 - eventType needs to be mapped in I2B2 in order to export to EHR2EDCMongo
            assert it.eventType == null
        }

        def firstMedication = medicationDocuments.get(0)
        firstMedication.getRoute() == "261754007"
        firstMedication.getDoseFormat() == "11190007"
        firstMedication.getFrequency() == "396134005"
        firstMedication.getDosage().getValue() == 100.0
        firstMedication.getDosage().getUnit() == "unit"

        where:
        patientId                | namespace               | subjectId               | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of("999-888") | _
    }
}
