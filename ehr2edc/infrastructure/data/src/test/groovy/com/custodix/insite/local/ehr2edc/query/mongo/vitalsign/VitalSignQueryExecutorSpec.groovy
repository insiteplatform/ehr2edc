package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign


import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptExpander
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.OrdinalCriterion
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignConceptFieldObjectMother
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSigns
import com.custodix.insite.local.ehr2edc.query.mongo.AbstractQueryExecutorSpec
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.gateway.VitalSignDocumentGateway
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.repository.VitalSignRepository
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.util.stream.Collectors

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor
import static com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl.vitalSignQueryDsl
import static com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignDocumentObjectMother.aNormalAdultBmiVitalSignDocument
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

class VitalSignQueryExecutorSpec extends AbstractQueryExecutorSpec {

    private static final String BMI_LOINC_CODE = "39156-5"
    private static final String BODY_HEIGHT_LOINC_CODE_1 = "8302-2"
    private static final String BODY_HEIGHT_LOINC_CODE_2 = "8306-3"
    private static final String BODY_HEIGHT_LOINC_CODE_3 = "8308-9"
    private static final String BODY_WEIGHT_LOINC_CODE = "29463-7"

    private static final LocalDate REFERENCE_DATE = LocalDate.now()
    private static final String SUBJECT_ID = "MY_SUBJECT_ID"
    private static final String SUBJECT_ID_2 = "MY_SUBJECT_ID_2"

    @Autowired
    protected VitalSignRepository vitalSignRepository
    @Autowired
    protected VitalSignDocumentGateway vitalSignQueryExecutor

    def setup() {
        vitalSignRepository.deleteAll()
    }

    def "As an investigator, I cannot query vital sign without a subjectId"() {
        given: "a query without a subjectId"
        def query = vitalSignQueryDsl()
                .getQuery()

        when: "executing the query"
        vitalSignQueryExecutor.execute(query, REFERENCE_DATE)

        then: "an exception is thrown"
        def ex = thrown(DomainException)
        ex.message == "The subject criterion is missing"
    }

    @Unroll
    def "As an investigator, I can not query vital sign with only a subjectId ."(String subjectId, String subjectId2, String bmiConceptCode) {
        given: "a query with a subject id '#subjectId' and no additional criteria"
        def query = vitalSignQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .getQuery()

        when: "executing the query"
        vitalSignQueryExecutor.execute(query, REFERENCE_DATE)

        then: "an exception is thrown"
        def ex = thrown(DomainException)
        ex.message == "An additional criterion is missing"

        where:
        subjectId  | subjectId2   | bmiConceptCode | _
        SUBJECT_ID | SUBJECT_ID_2 | BMI_LOINC_CODE | _
    }

    @Unroll
    def "As an investigator, I can query vital sign with subject id and concept"(String subjectId, String bmiConceptCode, String bodyHeightConceptCode) {
        given: "a query with a subject id '#subjectId' and code '#bmiConceptCode'"
        def query = vitalSignQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .forConcept(conceptFor(bmiConceptCode))
                .getQuery()

        and: "a vital sign for subject id '#subjectId' and concept code '#bmiConceptCode'"
        storeVitalSignWith(subjectId, bmiConceptCode)

        and: "a vital sign for subject id '#subjectId' and concept code '#bodyHeightConceptCode'"
        storeVitalSignWith(subjectId, bodyHeightConceptCode)

        when: "executing the query"
        VitalSigns vitalSigns = vitalSignQueryExecutor.execute(query, REFERENCE_DATE)

        then: "the vital sign with bmiConceptCode '#bmiConceptCode' for subject id '#subjectId' is returned"
        List<VitalSign> vitalSignList = vitalSigns.getValues()
        vitalSignList.size() == 1
        vitalSignList.get(0).concept.code == bmiConceptCode
        vitalSignList.get(0).subjectId.id == subjectId

        where:
        subjectId  | bmiConceptCode | bodyHeightConceptCode    | _
        SUBJECT_ID | BMI_LOINC_CODE | BODY_HEIGHT_LOINC_CODE_1 | _
    }

    @Unroll
    def "As an investigator, I can query vital sign with subject id and body height expansion"(String subjectId, String bmiConceptCode, String bodyHeightConceptCode1, String bodyHeightConceptCode2, String bodyHeightConceptCode3) {
        given: "a query with a subject id '#subjectId' and  with expansion for body height concept code 'bodyHeightConceptCode1'"
        def query = vitalSignQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .forConceptsRelatedTo(conceptFor(bodyHeightConceptCode1), createConceptExpansionFor(bodyHeightConceptCode1, bodyHeightConceptCode2, bodyHeightConceptCode3))
                .getQuery()

        and: "a vital sign for subject id '#subjectId' and concept code '#bmiConceptCode'"
        storeVitalSignWith(subjectId, bmiConceptCode)

        and: "a vital sign for subject id '#subjectId' and concept code '#bodyHeightConceptCode1'"
        storeVitalSignWith(subjectId, bodyHeightConceptCode1)

        and: "a vital sign for subject id '#subjectId' and concept code '#bodyHeightConceptCode2'"
        storeVitalSignWith(subjectId, bodyHeightConceptCode2)

        and: "a vital sign for subject id '#subjectId' and concept code '#bodyHeightConceptCode3'"
        storeVitalSignWith(subjectId, bodyHeightConceptCode3)

        when: "executing the query"
        VitalSigns vitalSigns = vitalSignQueryExecutor.execute(query, REFERENCE_DATE)

        then: "the vital signs with body height code '#bodyHeightConceptCode1', '#bodyHeightConceptCode2',' #bodyHeightConceptCode3', for subject id '#subjectId' are returned"
        List<VitalSign> vitalSignList = vitalSigns.getValues()
        vitalSignList.size() == 3
        vitalSignList.stream().anyMatch({ vitalSign -> vitalSign.subjectId.id == subjectId && vitalSign.getConcept().getCode() == bodyHeightConceptCode1 })
        vitalSignList.stream().anyMatch({ vitalSign -> vitalSign.subjectId.id == subjectId && vitalSign.getConcept().getCode() == bodyHeightConceptCode2 })
        vitalSignList.stream().anyMatch({ vitalSign -> vitalSign.subjectId.id == subjectId && vitalSign.getConcept().getCode() == bodyHeightConceptCode3 })

        where:
        subjectId  | bmiConceptCode | bodyHeightConceptCode1   | bodyHeightConceptCode2   | bodyHeightConceptCode3   | _
        SUBJECT_ID | BMI_LOINC_CODE | BODY_HEIGHT_LOINC_CODE_1 | BODY_HEIGHT_LOINC_CODE_2 | BODY_HEIGHT_LOINC_CODE_3 | _
    }

    @Unroll
    def "As an investigator, I can query vital sign with subject id and multiple concepts"(String subjectId, String bmiConceptCode, String bodyHeightConceptCode, String bodyWeightConceptCode) {
        given: "a query with a subject id '#subjectId' and code '#bmiConceptCode' and code '#bodyHeighConceptCode'"
        def query = vitalSignQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .forConcepts(conceptFor(bmiConceptCode), conceptFor(bodyHeightConceptCode))
                .getQuery()

        and: "a vital sign for subject id '#subjectId' and concept code '#bmiConceptCode'"
        storeVitalSignWith(subjectId, bmiConceptCode)

        and: "a vital sign for subject id '#subjectId' and concept code '#bodyHeightConceptCode'"
        storeVitalSignWith(subjectId, bodyHeightConceptCode)

        and: "a vital sign for subject id '#subjectId' and concept code '#bodyWeightConceptCode'"
        storeVitalSignWith(subjectId, bodyWeightConceptCode)

        when: "executing the query"
        VitalSigns vitalSigns = vitalSignQueryExecutor.execute(query, REFERENCE_DATE)

        then: "the vital signs with concept code '#bmiConceptCode', '#bodyHeightConceptCode' for subject id '#subjectId' are returned"
        List<VitalSign> vitalSignList = vitalSigns.getValues()
        vitalSignList.size() == 2
        vitalSignList.stream().anyMatch({ vitalSign -> vitalSign.subjectId.id == subjectId && vitalSign.getConcept().getCode() == bmiConceptCode })
        vitalSignList.stream().anyMatch({ vitalSign -> vitalSign.subjectId.id == subjectId && vitalSign.getConcept().getCode() == bodyHeightConceptCode })

        where:
        subjectId  | bmiConceptCode | bodyHeightConceptCode    | bodyWeightConceptCode  | _
        SUBJECT_ID | BMI_LOINC_CODE | BODY_HEIGHT_LOINC_CODE_1 | BODY_WEIGHT_LOINC_CODE | _
    }

    @Unroll
    def "As an investigator, I can query missing vital signs with subject id and multiple concepts"(LocalDate referenceDate,
                                                                                                    String[] queryConcepts,
                                                                                                    Period period,
                                                                                                    LocalDateTime bmiEffectiveDate,
                                                                                                    Boolean includeMissing,
                                                                                                    String[] expectedConcepts) {
        given: "A query for a subjectId with concepts #queryConcepts and include missing concepts #includeMissing within the last #period"
        List<ConceptCode> codes = queryConcepts.collect { conceptFor(it) }
        def query = vitalSignQueryDsl()
                .forSubject(SubjectId.of(SUBJECT_ID))
                .forConcepts(codes, includeMissing)
                .freshFor(period)
                .getQuery()
        and: "A BMI measurement with date #bmiEffectiveDate"
        storeVitalSignWith(SUBJECT_ID, BMI_LOINC_CODE, bmiEffectiveDate)

        when: "executing the query with referenceDate #referenceDate"
        VitalSigns vitalSigns = vitalSignQueryExecutor.execute(query, referenceDate)

        then: "Missing concepts #expectedConcepts are returned"
        vitalSigns.values.size() == expectedConcepts.size()
        vitalSigns.values.every {
            expectedConcepts.contains(it.concept.code)
            it.subjectId.id == SUBJECT_ID
        }
        and: "If a body weight vital sign is included, it has no measurement"
        !vitalSigns.values.find {
            it.concept.code == BODY_WEIGHT_LOINC_CODE && it.measurement != null
        }

        where:
        referenceDate        | queryConcepts                            | period           | bmiEffectiveDate                   | includeMissing || expectedConcepts
        dateOf(2018, 12, 10) | [BMI_LOINC_CODE, BODY_WEIGHT_LOINC_CODE] | Period.ofDays(5) | dateOf(2018, 12, 6).atStartOfDay() | true           || [BMI_LOINC_CODE, BODY_WEIGHT_LOINC_CODE]
        dateOf(2018, 12, 10) | [BMI_LOINC_CODE, BODY_WEIGHT_LOINC_CODE] | Period.ofDays(5) | dateOf(2018, 12, 1).atStartOfDay() | true           || [BMI_LOINC_CODE, BODY_WEIGHT_LOINC_CODE]
        dateOf(2018, 12, 10) | [BODY_WEIGHT_LOINC_CODE]                 | Period.ofDays(5) | dateOf(2018, 12, 1).atStartOfDay() | true           || [BODY_WEIGHT_LOINC_CODE]
        dateOf(2018, 12, 10) | [BODY_WEIGHT_LOINC_CODE]                 | Period.ofDays(5) | dateOf(2018, 12, 1).atStartOfDay() | true           || [BODY_WEIGHT_LOINC_CODE]
        dateOf(2018, 12, 10) | [BMI_LOINC_CODE]                         | Period.ofDays(5) | dateOf(2018, 12, 6).atStartOfDay() | true           || [BMI_LOINC_CODE]
        dateOf(2018, 12, 10) | []                                       | Period.ofDays(5) | dateOf(2018, 12, 1).atStartOfDay() | true           || []
        dateOf(2018, 12, 10) | [BMI_LOINC_CODE, BODY_WEIGHT_LOINC_CODE] | Period.ofDays(5) | dateOf(2018, 12, 6).atStartOfDay() | false          || [BMI_LOINC_CODE]
        dateOf(2018, 12, 10) | [BMI_LOINC_CODE, BODY_WEIGHT_LOINC_CODE] | Period.ofDays(5) | dateOf(2018, 12, 1).atStartOfDay() | false          || []
        dateOf(2018, 12, 10) | [BODY_WEIGHT_LOINC_CODE]                 | Period.ofDays(5) | dateOf(2018, 12, 1).atStartOfDay() | false          || []
        dateOf(2018, 12, 10) | [BODY_WEIGHT_LOINC_CODE]                 | Period.ofDays(5) | dateOf(2018, 12, 1).atStartOfDay() | false          || []
        dateOf(2018, 12, 10) | [BMI_LOINC_CODE]                         | Period.ofDays(5) | dateOf(2018, 12, 6).atStartOfDay() | false          || [BMI_LOINC_CODE]
        dateOf(2018, 12, 10) | []                                       | Period.ofDays(5) | dateOf(2018, 12, 1).atStartOfDay() | false          || []
    }

    @Unroll
    def "Get vital signs based on excluded concepts"(String subjectId, String bmiConceptCode, String bodyHeightConceptCode, String bodyWeightConceptCode) {
        given: "a query with a subject id '#subjectId' and excluded codes '#bmiConceptCode' and '#bodyHeighConceptCode'"
        def query = vitalSignQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .excludingConcepts(conceptFor(bmiConceptCode), conceptFor(bodyHeightConceptCode))
                .getQuery()

        and: "a vital sign for subject id '#subjectId' and concept code '#bmiConceptCode'"
        storeVitalSignWith(subjectId, bmiConceptCode)

        and: "a vital sign for subject id '#subjectId' and concept code '#bodyHeightConceptCode'"
        storeVitalSignWith(subjectId, bodyHeightConceptCode)

        and: "a vital sign for subject id '#subjectId' and concept code '#bodyWeightConceptCode'"
        storeVitalSignWith(subjectId, bodyWeightConceptCode)

        when: "executing the query"
        VitalSigns vitalSigns = vitalSignQueryExecutor.execute(query, REFERENCE_DATE)

        then: "the vital sign with concept code '#bodyWeightConceptCode' is returned"
        List<VitalSign> vitalSignList = vitalSigns.getValues()
        vitalSignList.size() == 1
        vitalSignList.get(0).concept.code == bodyWeightConceptCode

        where:
        subjectId  | bmiConceptCode | bodyHeightConceptCode    | bodyWeightConceptCode  | _
        SUBJECT_ID | BMI_LOINC_CODE | BODY_HEIGHT_LOINC_CODE_1 | BODY_WEIGHT_LOINC_CODE | _
    }

    @Unroll("Get vital signs dated #period before #referenceDate")
    def "Get vital signs based on freshness criterion"(LocalDate referenceDate, Period period, String subjectId, String bodyHeightConceptCode, String bodyHeightConceptEffectiveDate, int expectedAmountOfObservations) {
        given: "a query with a subject id '#subjectId' within #period before #referenceDate"
        def query = vitalSignQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .freshFor(period)
                .getQuery()

        and: "a vital sign for subject id '#subjectId' and concept code '#bodyHeightConceptCode' and effective date 'bodyHeightConceptEffectiveDate'"
        storeVitalSignWith(SUBJECT_ID, bodyHeightConceptCode, LocalDateTime.parse(bodyHeightConceptEffectiveDate, ISO_LOCAL_DATE_TIME))

        when: "executing the query"
        VitalSigns vitalSigns = vitalSignQueryExecutor.execute(query, referenceDate)

        then: "#expectedAmountOfObservations vital signs with concept code '#bodyHeightConceptCode' for subject id '#subjectId' are returned"
        List<VitalSign> vitalSignList = vitalSigns.getValues()
        vitalSignList.size() == expectedAmountOfObservations
        vitalSignList.every {
            it.subjectId.id == subjectId
            it.concept.code == BODY_HEIGHT_LOINC_CODE_1
        }

        where:
        referenceDate       | period           | subjectId  | bodyHeightConceptCode    | bodyHeightConceptEffectiveDate || expectedAmountOfObservations
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | BODY_HEIGHT_LOINC_CODE_1 | '2011-12-06T00:00:00'          || 0
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | BODY_HEIGHT_LOINC_CODE_1 | '2011-12-05T23:59:59'          || 1
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | BODY_HEIGHT_LOINC_CODE_1 | '2011-12-05T10:15:30'          || 1
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | BODY_HEIGHT_LOINC_CODE_1 | '2011-12-04T10:15:30'          || 1
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | BODY_HEIGHT_LOINC_CODE_1 | '2011-12-04T00:00:00'          || 1
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | BODY_HEIGHT_LOINC_CODE_1 | '2011-12-03T23:59:59'          || 0
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | BODY_HEIGHT_LOINC_CODE_1 | '2011-12-03T10:15:30'          || 0
    }

    @Unroll
    def "As an investigator, I can query the #ordinal vital sign with subject id"(String subjectId, OrdinalCriterion.Ordinal ordinal, expectedDate) {
        given: "a query with a subject id '#subjectId' and ordinal '#ordinal'"
        def query = vitalSignQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .forOrdinal(ordinal)
                .getQuery()

        and: "a vital sign for subject id '#subjectId', on 10/12/2018 at 09:10"
        LocalDateTime latest = LocalDateTime.of(2018, 12, 10, 9, 10)
        LocalDateTime first = LocalDateTime.of(2013, 4, 20, 10, 40)
        storeVitalSignWith(subjectId, BODY_WEIGHT_LOINC_CODE, latest)
        and: "a vital sign for subject id '#subjectId', on 20/04/2013 at 10:40"
        storeVitalSignWith(subjectId, BODY_WEIGHT_LOINC_CODE, first)

        when: "executing the query"
        VitalSigns vitalSigns = vitalSignQueryExecutor.execute(query, REFERENCE_DATE)

        then: "the #ordinal vital sign at #expectedDate, for subject id '#subjectId' is returned"
        List<VitalSign> vitalSignList = vitalSigns.getValues()
        vitalSignList.size() == 1
        vitalSignList.get(0).subjectId.id == subjectId
        vitalSignList.get(0).effectiveDateTime == expectedDate

        where:
        subjectId  | ordinal                        || expectedDate
        SUBJECT_ID | OrdinalCriterion.Ordinal.LAST  || LocalDateTime.of(2018, 12, 10, 9, 10)
        SUBJECT_ID | OrdinalCriterion.Ordinal.FIRST || LocalDateTime.of(2013, 4, 20, 10, 40)
    }

    def storeVitalSignWith(final String subjectId, final String conceptCode) {
        def vitalSign = aNormalAdultBmiVitalSignDocument().toBuilder()
                .withSubjectId(SubjectId.of(subjectId))
                .withConcept(VitalSignConceptFieldObjectMother.aDefaultConcept().toBuilder()
                        .withConcept(conceptFor(conceptCode))
                        .build())
                .build()
        vitalSignRepository.save(vitalSign)
    }

    def storeVitalSignWith(final String subjectId, final String conceptCode, LocalDateTime effectiveDateTime) {
        def vitalSign = aNormalAdultBmiVitalSignDocument().toBuilder()
                .withSubjectId(SubjectId.of(subjectId))
                .withConcept(VitalSignConceptFieldObjectMother.aDefaultConcept().toBuilder()
                        .withConcept(conceptFor(conceptCode))
                        .build())
                .withEffectiveDateTime(effectiveDateTime)
                .build()
        vitalSignRepository.save(vitalSign)
    }

    def storeBmiVitalSignWith(final String subjectId) {
        def bmiVitalSign = aNormalAdultBmiVitalSignDocument().toBuilder().withSubjectId(SubjectId.of(subjectId)).build()
        vitalSignRepository.save(bmiVitalSign)
    }

    def createConceptExpansionFor(final String conceptCode1, final String... conceptCodes) {
        ConceptExpander conceptExpander = new ConceptExpander() {
            @Override
            List<ConceptCode> resolve(final ConceptCode concept) {
                if (concept != null && concept.equals(conceptFor(conceptCode1))) {
                    List<ConceptCode> concepts = Arrays.stream(conceptCodes)
                            .map({ c -> conceptFor(c) })
                            .collect(Collectors.toList())
                    concepts.add(conceptFor(conceptCode1))
                    return concepts
                }
            }
        }
        return conceptExpander
    }

    private static LocalDate dateOf(Integer year, Integer month, Integer day) {
        LocalDate.of(year, month, day)
    }
}
