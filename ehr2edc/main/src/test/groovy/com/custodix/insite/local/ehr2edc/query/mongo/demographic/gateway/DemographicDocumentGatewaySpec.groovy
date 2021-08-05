package com.custodix.insite.local.ehr2edc.query.mongo.demographic.gateway

import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.Demographics
import com.custodix.insite.local.ehr2edc.query.mongo.AbstractMongoQueryDataSpec
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.gateway.DemographicDocumentGateway
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.repository.DemographicRepository
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl.demographicQuery

class DemographicDocumentGatewaySpec extends AbstractMongoQueryDataSpec {

    private static final String PATIENT_ID = "7"
    private static final String NAMESPACE = "MASTER_PATIENT_INDEX"
    private static final String SUBJECT_ID = "999-888"

    @Autowired
    DemographicDocumentGateway demographicQueryExecutor
    @Autowired
    DemographicRepository demographicRepository

    def setup() {
        demographicRepository.deleteAll()
    }

    @Unroll
    def "can execute a demographics query for a subject"(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "A subject with 4 demographic data points"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        when: "I execute a query for the given subject"
        def query = demographicQuery()
                .forSubject(com.custodix.insite.local.ehr2edc.vocabulary.SubjectId.of(SUBJECT_ID))
                .getQuery()
        Demographics result = demographicQueryExecutor.execute(query, LocalDate.now())

        then: "All 4 demographics of the subject are returned"
        result.demographics.size() == 4
        and: "The result only contains data for the given subject"
        result.demographics.stream().anyMatch {
            it.subjectId.id == subjectId.id
            it.demographicType == DemographicType.GENDER
            it.value == "male"
        }

        result.demographics.stream().anyMatch {
            it.subjectId.id == subjectId.id
            it.demographicType == DemographicType.BIRTH_DATE
            it.value == "1934-05-01"
        }

        result.demographics.stream().anyMatch {
            it.subjectId.id == subjectId.id
            it.demographicType == DemographicType.DEATH_DATE
            it.value == ""
        }

        result.demographics.stream().anyMatch {
            it.subjectId.id == subjectId.id
            it.demographicType == DemographicType.VITAL_STATUS
            it.value == "ALIVE"
        }

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

}
