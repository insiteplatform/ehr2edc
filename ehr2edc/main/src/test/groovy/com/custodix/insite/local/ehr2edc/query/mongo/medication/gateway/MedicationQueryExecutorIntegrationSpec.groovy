package com.custodix.insite.local.ehr2edc.query.mongo.medication.gateway


import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.Medications
import com.custodix.insite.local.ehr2edc.query.mongo.AbstractMongoQueryDataSpec
import com.custodix.insite.local.ehr2edc.query.mongo.medication.gateway.MedicationDocumentGateway
import com.custodix.insite.local.ehr2edc.query.mongo.medication.repository.MedicationRepository
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl.medicationQueryDsl

class MedicationQueryExecutorIntegrationSpec extends AbstractMongoQueryDataSpec {

    private static final String PATIENT_ID = "7"
    private static final String NAMESPACE = "MASTER_PATIENT_INDEX"
    private static final String SUBJECT_ID = "999-888"
    private static final LocalDate REFERENCE_DATE = LocalDate.now()

    @Autowired
    MedicationDocumentGateway medicationQueryExecutor
    @Autowired
    MedicationRepository medicationRepository

    def setup() {
        medicationRepository.deleteAll()
    }

    @Unroll
    def "can execute a medication query for a subject"(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "A subject with medication data points"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        when: "I execute a query for the given subject"
        def query = medicationQueryDsl()
                .forSubject(com.custodix.insite.local.ehr2edc.vocabulary.SubjectId.of(SUBJECT_ID))
                .forConcept(ConceptCode.conceptFor("A10A"))
                .getQuery()
        Medications result = medicationQueryExecutor.execute(query, REFERENCE_DATE)

        then: "All 4 medication observations of the subject are returned"
        result.values.size() == 4

        def firstMedication = result.values[0]
        firstMedication.subjectId.id == SUBJECT_ID
        firstMedication.startDate == LocalDate.parse('2016-03-02').atStartOfDay()
        firstMedication.endDate == LocalDate.parse('2016-03-02').atStartOfDay()
        firstMedication.concept.code == "A10A"
        firstMedication.medicationConcept.name == "INS LEVEMIR_INNOLET 100U/ML 3ML PF PEN"
        firstMedication.dosage.value == BigDecimal.valueOf(100)
        firstMedication.dosage.unit == "unit"
        firstMedication.administrationRoute == "261754007"
        firstMedication.doseForm == "11190007"
        firstMedication.dosingFrequency == "396134005"

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

}
