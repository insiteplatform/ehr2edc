package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.gateway


import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LabValues
import com.custodix.insite.local.ehr2edc.query.mongo.AbstractMongoQueryDataSpec
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.gateway.LabValueDocumentGateway
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.repository.LabValueRepository
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.stream.Collectors

import static com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl.labValueQuery

class LabValueQueryExecutorIntegrationSpec extends AbstractMongoQueryDataSpec {
    private static final LocalDate REFERENCE_DATE = LocalDate.now()
    private static final String PATIENT_ID = "7"
    private static final String NAMESPACE = "MASTER_PATIENT_INDEX"
    private static final String SUBJECT_ID = "999-888"
    private static final String CONCEPT_CODE_4548_4 = "4548-4"
    private static final String COMPONENT = "Hemoglobin A1c/Hemoglobin.total"
    private static final String METHOD_EMPTY = ""
    private static final String VENDOR_SOURCE_7 = "SOURCE_7"
    private static final FastingStatus FASTING_STATUS_NOT_FASTING = FastingStatus.NOT_FASTING
    private static final String SPECIMEN_BLOOD = "Bld"
    public static final String UNIT_PERCENTAGE = "%"

    @Autowired
    LabValueDocumentGateway labValueQueryExecutor
    @Autowired
    LabValueRepository labValueRepository

    def setup() {
        labValueRepository.deleteAll()
    }

    @Unroll
    def "can execute a lab values query for a subject"(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "A subject with 7 lab values"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        when: "I execute a query for the given subject"
        def query = labValueQuery()
                .forSubject(com.custodix.insite.local.ehr2edc.vocabulary.SubjectId.of(SUBJECT_ID))
                .forConcept(ConceptCode.conceptFor(CONCEPT_CODE_4548_4))
                .getQuery()
        LabValues labValues = labValueQueryExecutor.execute(query, REFERENCE_DATE)

        then: "All 7 lab values of the subject are returned"
        labValues.getValues().size() == 7
        and: "The result only contains data for the given subject"
        List<LabValue> values = labValues.getValues().stream().sorted({ l1, l2 -> l1.startDate <=> l2.startDate }).collect(Collectors.toList())

        assertLabValue(values.get(0),
                "2005-11-09",
                "2005-11-09",
                7.10000)

        assertLabValue(values.get(1),
                "2006-05-03",
                "2006-05-03",
                7.90000)

        assertLabValue(values.get(2),
                "2006-08-03",
                "2006-08-03",
                6.80000)

        assertLabValue(values.get(3),
                "2006-10-19",
                "2006-10-19",
                7.10000)

        assertLabValue(values.get(4),
                "2007-04-18",
                "2007-04-18",
                6.20000)

        assertLabValue(values.get(5),
                "2007-10-18",
                "2007-10-18",
                5.70000)

        assertLabValue(values.get(6),
                "2008-05-09",
                "2008-05-09",
                6.10000)

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

    private void assertLabValue(LabValue labValue,
                                String startDate,
                                String endDate,
                                double value) {
        assert labValue.subjectId.id == SUBJECT_ID
        assert labValue.startDate == LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIDNIGHT)
        assert labValue.endDate == LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MIDNIGHT)
        assert labValue.labConcept.concept.code == CONCEPT_CODE_4548_4
        assert labValue.labConcept.component == COMPONENT
        assert labValue.labConcept.method == METHOD_EMPTY
        assert labValue.labConcept.fastingStatus == FASTING_STATUS_NOT_FASTING
        assert labValue.labConcept.specimen == SPECIMEN_BLOOD
        assert labValue.vendor == VENDOR_SOURCE_7
        assert labValue.quantitativeResult.lowerLimit == null
        assert labValue.quantitativeResult.upperLimit == null
        assert labValue.quantitativeResult.value == BigDecimal.valueOf(value)
        assert labValue.quantitativeResult.unit == UNIT_PERCENTAGE
    }
}
