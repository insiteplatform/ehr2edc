package com.custodix.insite.local.ehr2edc.query.mongo.medication


import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptExpander
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.OrdinalCriterion
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationConceptFieldObjectMother
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.Medications
import com.custodix.insite.local.ehr2edc.query.mongo.AbstractQueryExecutorSpec
import com.custodix.insite.local.ehr2edc.query.mongo.medication.gateway.MedicationDocumentGateway
import com.custodix.insite.local.ehr2edc.query.mongo.medication.repository.MedicationRepository
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.util.stream.Collectors

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor
import static com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl.medicationQueryDsl
import static com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationDocumentObjectMother.anOmeprazoleMedicationDocument
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

class MedicationQueryExecutorSpec extends AbstractQueryExecutorSpec {

    private static final String OMEPRAZOLE_NAME = "omeprazole"
    private static final String OMEPRAZOLE_CODE = "A02BC01"
    private static final String MEDICATION_CODE_1 = "8306-2"
    private static final String MEDICATION_CODE_2 = "8306-3"
    private static final String MEDICATION_CODE_3 = "8308-9"
    private static final String PROTAMINE_CODE = "V03AB14"

    private static final LocalDate REFERENCE_DATE = LocalDate.now()
    private static final String SUBJECT_ID = "MY_SUBJECT_ID"
    private static final String SUBJECT_ID_2 = "MY_SUBJECT_ID_2"

    @Autowired
    protected MedicationRepository medicationRepository
    @Autowired
    protected MedicationDocumentGateway medicationQueryExecutor

    def setup() {
        medicationRepository.deleteAll()
    }

    def "As an investigator, I cannot query medication without a subjectId"() {
        given: "a query without a subjectId"
        def query = medicationQueryDsl()
                .getQuery()

        when: "executing the query"
        medicationQueryExecutor.execute(query, REFERENCE_DATE)

        then: "an exception is thrown"
        def ex = thrown(DomainException)
        ex.message == "The subject criterion is missing"
    }

    @Unroll
    def "As an investigator, I can query medication with only a subjectId ."(String subjectId, String subjectId2, String omeprazoleCode, String omeprazoleName) {
        given: "a query with a subject id '#subjectId'"
        def query = medicationQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .getQuery()
        and: "medication omeprazole for subject id '#subjectId'"
        storeOmeprazoleMedicationWith(subjectId)
        and: "medication omeprazole for subject id '#subjectId2'"
        storeOmeprazoleMedicationWith(subjectId2)

        when: "executing the query"
        Medications medications = medicationQueryExecutor.execute(query, REFERENCE_DATE)

        then: "the medication with omeprazoleCode '#omeprazoleCode' and name `#omeprazoleName` for subject id '#subjectId' is returned"
        List<Medication> medicationList = medications.getValues()
        medicationList.size() == 1
        def medication = medicationList.get(0)
        medication.concept.code == OMEPRAZOLE_CODE
        medication.medicationConcept.name == OMEPRAZOLE_NAME
        medication.subjectId.id == subjectId

        where:
        subjectId  | subjectId2   | omeprazoleCode  | omeprazoleName  | _
        SUBJECT_ID | SUBJECT_ID_2 | OMEPRAZOLE_CODE | OMEPRAZOLE_NAME | _
    }

    @Unroll
    def "As an investigator, I can query medication with subject id and concept"(String subjectId, String omeprazoleCode, String medicationCode1) {
        given: "a query with a subject id '#subjectId' and code '#omeprazoleCode'"
        def query = medicationQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .forConcept(conceptFor(omeprazoleCode))
                .getQuery()

        and: "a medication for subject id '#subjectId' and concept code '#omeprazoleCode'"
        storeOmeprazoleMedicationWith(subjectId, omeprazoleCode)

        and: "a medication for subject id '#subjectId' and concept code '#medicationCode1'"
        storeOmeprazoleMedicationWith(subjectId, medicationCode1)

        when: "executing the query"
        Medications medications = medicationQueryExecutor.execute(query, REFERENCE_DATE)

        then: "the medication with omeprazoleCode '#omeprazoleCode' for subject id '#subjectId' is returned"
        List<Medication> medicationList = medications.getValues()
        medicationList.size() == 1
        def medication = medicationList.get(0)
        medication.concept.code == omeprazoleCode
        medication.medicationConcept.name == omeprazoleCode + "name"
        medication.subjectId.id == subjectId

        where:
        subjectId  | omeprazoleCode  | medicationCode1   | _
        SUBJECT_ID | OMEPRAZOLE_CODE | MEDICATION_CODE_1 | _
    }

    @Unroll
    def "As an investigator, I can query medication with subject id and medication expansion"(String subjectId, String omeprazoleConceptCode, String medicationConceptCode1, String medicationConceptCode2, String medicationConceptCode3) {
        given: "a query with a subject id '#subjectId' and  with expansion for medication concept code 'medicationConceptCode1'"
        def query = medicationQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .forConceptsRelatedTo(conceptFor(medicationConceptCode1), createConceptExpansionFor(medicationConceptCode1, medicationConceptCode2, medicationConceptCode3))
                .getQuery()

        and: "a medication for subject id '#subjectId' and concept code '#omeprazoleConceptCode'"
        storeOmeprazoleMedicationWith(subjectId, omeprazoleConceptCode)

        and: "a medication for subject id '#subjectId' and concept code '#medicationConceptCode1'"
        storeOmeprazoleMedicationWith(subjectId, medicationConceptCode1)

        and: "a medication for subject id '#subjectId' and concept code '#medicationConceptCode2'"
        storeOmeprazoleMedicationWith(subjectId, medicationConceptCode2)

        and: "a medication for subject id '#subjectId' and concept code '#medicationConceptCode3'"
        storeOmeprazoleMedicationWith(subjectId, medicationConceptCode3)

        when: "executing the query"
        Medications medications = medicationQueryExecutor.execute(query, REFERENCE_DATE)

        then: "the medications with concept code '#medicationConceptCode1', '#medicationConceptCode2',' #medicationConceptCode3', for subject id '#subjectId' are returned"
        List<Medication> medicationList = medications.getValues()
        medicationList.size() == 3
        medicationList.stream().anyMatch({ medication -> medication.subjectId.id == subjectId && medication.getConcept().getCode() == medicationConceptCode1 })
        medicationList.stream().anyMatch({ medication -> medication.subjectId.id == subjectId && medication.getConcept().getCode() == medicationConceptCode2 })
        medicationList.stream().anyMatch({ medication -> medication.subjectId.id == subjectId && medication.getConcept().getCode() == medicationConceptCode3 })

        where:
        subjectId  | omeprazoleConceptCode | medicationConceptCode1 | medicationConceptCode2 | medicationConceptCode3 | _
        SUBJECT_ID | OMEPRAZOLE_CODE       | MEDICATION_CODE_1      | MEDICATION_CODE_2      | MEDICATION_CODE_3      | _
    }

    @Unroll
    def "As an investigator, I can query medication with subject id and multiple concepts"(String subjectId, String omeprazoleConceptCode, String medicationCode1, String protamineCode) {
        given: "a query with a subject id '#subjectId' and code '#omeprazoleConceptCode' and code '#medicationCode1'"
        def query = medicationQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .forConcepts(conceptFor(omeprazoleConceptCode), conceptFor(medicationCode1))
                .getQuery()

        and: "a medication for subject id '#subjectId' and concept code '#omeprazoleConceptCode'"
        storeOmeprazoleMedicationWith(subjectId, omeprazoleConceptCode)

        and: "a medication for subject id '#subjectId' and concept code '#medicationCode1'"
        storeOmeprazoleMedicationWith(subjectId, medicationCode1)

        and: "a medication for subject id '#subjectId' and concept code '#protamineCode'"
        storeOmeprazoleMedicationWith(subjectId, protamineCode)

        when: "executing the query"
        Medications medications = medicationQueryExecutor.execute(query, REFERENCE_DATE)

        then: "the medications with concept code '#omeprazoleConceptCode', '#medicationCode1' for subject id '#subjectId' are returned"
        List<Medication> medicationList = medications.getValues()
        medicationList.size() == 2
        medicationList.stream().anyMatch({ medication -> medication.subjectId.id == subjectId && medication.getConcept().getCode() == omeprazoleConceptCode })
        medicationList.stream().anyMatch({ medication -> medication.subjectId.id == subjectId && medication.getConcept().getCode() == medicationCode1 })

        where:
        subjectId  | omeprazoleConceptCode | medicationCode1   | protamineCode  | _
        SUBJECT_ID | OMEPRAZOLE_CODE       | MEDICATION_CODE_1 | PROTAMINE_CODE | _
    }

    @Unroll
    def "Get medications based on excluded concepts"(String subjectId, String omeprazoleConceptCode, String medicationCode1, String protamineCode) {
        given: "a query with a subject id '#subjectId' and excluded codes '#omeprazoleConceptCode' and '#medicationCode1'"
        def query = medicationQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .excludingConcepts(conceptFor(omeprazoleConceptCode), conceptFor(medicationCode1))
                .getQuery()

        and: "a medication for subject id '#subjectId' and concept code '#omeprazoleConceptCode'"
        storeOmeprazoleMedicationWith(subjectId, omeprazoleConceptCode)

        and: "a medication for subject id '#subjectId' and concept code '#medicationCode1'"
        storeOmeprazoleMedicationWith(subjectId, medicationCode1)

        and: "a medication for subject id '#subjectId' and concept code '#protamineCode'"
        storeOmeprazoleMedicationWith(subjectId, protamineCode)

        when: "executing the query"
        Medications medications = medicationQueryExecutor.execute(query, REFERENCE_DATE)

        then: "the medication with concept code '#protamineCode' is returned"
        List<Medication> medicationList = medications.getValues()
        medicationList.size() == 1
        medicationList.get(0).concept.code == protamineCode

        where:
        subjectId  | omeprazoleConceptCode | medicationCode1   | protamineCode  | _
        SUBJECT_ID | OMEPRAZOLE_CODE       | MEDICATION_CODE_1 | PROTAMINE_CODE | _
    }

    @Unroll("Get medications with start dated in #period before #referenceDate")
    def "Get medications based on freshness criterion"(LocalDate referenceDate, Period period, String subjectId, String medicationCode1, String medicationStartDate, int expectedAmountOfObservations) {
        given: "a query with a subject id '#subjectId' within #period before #referenceDate"
        def query = medicationQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .freshFor(period)
                .getQuery()

        and: "a medication for subject id '#subjectId' and concept code '#medicationCode1' and effective date 'medicationStartDate'"
        storeOmeprazoleMedicationWith(SUBJECT_ID, medicationCode1, LocalDateTime.parse(medicationStartDate, ISO_LOCAL_DATE_TIME))

        when: "executing the query"
        Medications medications = medicationQueryExecutor.execute(query, referenceDate)

        then: "#expectedAmountOfObservations medications with concept code '#medicationCode1' for subject id '#subjectId' are returned"
        List<Medication> medicationList = medications.getValues()
        medicationList.size() == expectedAmountOfObservations
        medicationList.every {
            it.subjectId.id == subjectId
            it.concept.code == MEDICATION_CODE_1
        }

        where:
        referenceDate       | period           | subjectId  | medicationCode1   | medicationStartDate   || expectedAmountOfObservations
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | MEDICATION_CODE_1 | '2011-12-06T00:00:00' || 0
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | MEDICATION_CODE_1 | '2011-12-05T23:59:59' || 1
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | MEDICATION_CODE_1 | '2011-12-05T10:15:30' || 1
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | MEDICATION_CODE_1 | '2011-12-04T10:15:30' || 1
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | MEDICATION_CODE_1 | '2011-12-04T00:00:00' || 1
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | MEDICATION_CODE_1 | '2011-12-03T23:59:59' || 0
        dateOf(2011, 12, 5) | Period.ofDays(2) | SUBJECT_ID | MEDICATION_CODE_1 | '2011-12-03T10:15:30' || 0
    }

    @Unroll
    def "As an investigator, I can query the #ordinal medications with subject id"(String subjectId, OrdinalCriterion.Ordinal ordinal, expectedDate) {
        given: "a query with a subject id '#subjectId' and ordinal '#ordinal'"
        def query = medicationQueryDsl()
                .forSubject(SubjectId.of(subjectId))
                .forOrdinal(ordinal)
                .getQuery()

        and: "a medication for subject id '#subjectId', on 10/12/2018 at 09:10"
        LocalDateTime latest = LocalDateTime.of(2018, 12, 10, 9, 10)
        LocalDateTime first = LocalDateTime.of(2013, 4, 20, 10, 40)
        storeOmeprazoleMedicationWith(subjectId, OMEPRAZOLE_CODE, latest)
        and: "a medication for subject id '#subjectId', on 20/04/2013 at 10:40"
        storeOmeprazoleMedicationWith(subjectId, OMEPRAZOLE_CODE, first)

        when: "executing the query"
        Medications medications = medicationQueryExecutor.execute(query, REFERENCE_DATE)

        then: "the #ordinal medication at #expectedDate, for subject id '#subjectId' is returned"
        List<Medication> listOfMedications = medications.getValues()
        listOfMedications.size() == 1
        listOfMedications.get(0).subjectId.id == subjectId
        listOfMedications.get(0).startDate == expectedDate

        where:
        subjectId  | ordinal                        || expectedDate
        SUBJECT_ID | OrdinalCriterion.Ordinal.LAST  || LocalDateTime.of(2018, 12, 10, 9, 10)
        SUBJECT_ID | OrdinalCriterion.Ordinal.FIRST || LocalDateTime.of(2013, 4, 20, 10, 40)
    }

    def storeOmeprazoleMedicationWith(final String subjectId, final String conceptCode) {
        def medication = anOmeprazoleMedicationDocument().toBuilder()
                .withSubjectId(SubjectId.of(subjectId))
                .withConcept(MedicationConceptFieldObjectMother.aDefaultConcept().toBuilder()
                        .withConcept(conceptFor(conceptCode))
                        .withName(conceptCode + "name")
                        .build())
                .build()
        medicationRepository.save(medication)
    }

    def storeOmeprazoleMedicationWith(final String subjectId, final String omeprazoleConceptCode, LocalDateTime startDate) {
        def medication = anOmeprazoleMedicationDocument().toBuilder()
                .withSubjectId(SubjectId.of(subjectId))
                .withConcept(MedicationConceptFieldObjectMother.aDefaultConcept().toBuilder()
                        .withConcept(conceptFor(omeprazoleConceptCode))
                        .build())
                .withStartDate(startDate)
                .build()
        medicationRepository.save(medication)
    }

    def storeOmeprazoleMedicationWith(final String subjectId) {
        def medication = anOmeprazoleMedicationDocument().toBuilder().withSubjectId(SubjectId.of(subjectId)).build()
        medicationRepository.save(medication)
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
