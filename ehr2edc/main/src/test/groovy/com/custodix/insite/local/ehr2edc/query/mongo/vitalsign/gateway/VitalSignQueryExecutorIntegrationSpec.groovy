package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.gateway


import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSigns
import com.custodix.insite.local.ehr2edc.query.mongo.AbstractMongoQueryDataSpec
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.gateway.VitalSignDocumentGateway
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.repository.VitalSignRepository
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl.vitalSignQueryDsl

class VitalSignQueryExecutorIntegrationSpec extends AbstractMongoQueryDataSpec {

    private static final String PATIENT_ID = "7"
    private static final String NAMESPACE = "MASTER_PATIENT_INDEX"
    private static final String SUBJECT_ID = "999-888"
    private static final LocalDate REFERENCE_DATE = LocalDate.now()

    @Autowired
    VitalSignDocumentGateway vitalSignQueryExecutor
    @Autowired
    VitalSignRepository vitalSignRepository

    def setup() {
        vitalSignRepository.deleteAll()
    }

    @Unroll
    def "can execute a vital sign query for a subject"(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "A subject with 2 vital sign data points"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        when: "I execute a query for the given subject"
        def query = vitalSignQueryDsl()
                .forSubject(com.custodix.insite.local.ehr2edc.vocabulary.SubjectId.of(SUBJECT_ID))
                .forConcept(ConceptCode.conceptFor("106241006"))
                .getQuery()
        VitalSigns result = vitalSignQueryExecutor.execute(query, REFERENCE_DATE)

        then: "All 2 vital sign of the subject are returned"
        result.values.size() == 2
        assertVitalSign(result.values.get(0))
        assertVitalSign(result.values.get(1))
        assertNoModifiers(result.values.get(0))
        assertModifiers(result.values.get(1))

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

    private void assertVitalSign(VitalSign vitalSign) {
        assert vitalSign.vitalSignConcept.concept.code == '106241006'
        assert vitalSign.subjectId.id == SUBJECT_ID
        assert vitalSign.measurement.unit == '%'
        assert vitalSign.measurement.upperLimit == BigDecimal.valueOf(1.5)
        assert vitalSign.measurement.lowerLimit == BigDecimal.valueOf(0.5)
        assert vitalSign.measurement.value == BigDecimal.valueOf(1.0)
        assert vitalSign.effectiveDateTime == LocalDate.parse('2016-03-02').atStartOfDay()
    }

    private void assertNoModifiers(VitalSign vitalSign) {
        assert vitalSign.vitalSignConcept.location == null
        assert vitalSign.vitalSignConcept.laterality == null
        assert vitalSign.vitalSignConcept.position == null
    }

    private void assertModifiers(VitalSign vitalSign) {
        assert vitalSign.vitalSignConcept.location == "40983000"
        assert vitalSign.vitalSignConcept.laterality == "7771000"
        assert vitalSign.vitalSignConcept.position == "33586001"
    }
}
