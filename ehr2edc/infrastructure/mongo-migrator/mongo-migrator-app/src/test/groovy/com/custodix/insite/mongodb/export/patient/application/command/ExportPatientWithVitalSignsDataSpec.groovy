package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign.VitalSignDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.VitalSignDocumentRepository
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

class ExportPatientWithVitalSignsDataSpec extends AbstractExportPatientSpec {

    private static final String PATIENT_ID = "7"
    private static final String NAMESPACE = "MASTER_PATIENT_INDEX"

    @Autowired
    VitalSignDocumentRepository vitalSignDocumentRepository

    @Autowired
    ExportPatient exportPatient

    def "I can register subject with vital sign data" (PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "request"
        def request = ExportPatient.Request.newBuilder()
                .withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId))
                .build()

        when: "exporting patient"
        exportPatient.export(request)

        then: "2 vital signs data points are exported"
        List<VitalSignDocument> vitalSignDocuments = vitalSignDocumentRepository.findAll()
        vitalSignDocuments.size() == 2
        and: "Both data points contain basic information"
        vitalSignDocuments.each {
            assert it.subjectId == subjectId
            assert it.effectiveDateTime == LocalDate.parse('2016-03-02').atStartOfDay()
            assert it.getConcept().concept.code == '106241006'
            assert it.getMeasurement().unit == '%'
            assert it.getMeasurement().lowerLimit == BigDecimal.valueOf(0.5)
            assert it.getMeasurement().upperLimit == BigDecimal.valueOf(1.5)
            assert it.getMeasurement().value == BigDecimal.valueOf(1)
        }
        and: "The first data point has no laterality, position or location information"
        with(vitalSignDocuments.get(0)) {
            it.getConcept().position == null
            it.getConcept().location == null
            it.getConcept().laterality == null
        }
        and: "The second data point has laterality, position and location information"
        with(vitalSignDocuments.get(1)) {
            it.getConcept().position == "33586001"
            it.getConcept().location == "40983000"
            it.getConcept().laterality == "7771000"
        }

        where:
        patientId                   | namespace                     | subjectId                   | _
        PatientId.of(PATIENT_ID)    | Namespace.of(NAMESPACE)       | SubjectId.of("999-888") | _
    }
}
