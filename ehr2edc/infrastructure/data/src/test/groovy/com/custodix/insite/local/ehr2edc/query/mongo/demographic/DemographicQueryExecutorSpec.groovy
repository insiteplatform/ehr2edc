package com.custodix.insite.local.ehr2edc.query.mongo.demographic


import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.Demographics
import com.custodix.insite.local.ehr2edc.query.mongo.AbstractQueryExecutorSpec
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.gateway.DemographicDocumentGateway
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.model.DemographicDocument
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.repository.DemographicRepository
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl.demographicQuery
import static com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType.*
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId

class DemographicQueryExecutorSpec extends AbstractQueryExecutorSpec {
    private static final LocalDate REFERENCE_DATE = LocalDate.now()
    private static final String BIRTH_DATE_VALUE = "2000-01-01T00:00:00.000Z"
    private static final String DEATH_DATE_VALUE = "2010-01-01T00:00:00.000Z"
    private static final String GENDER_VALUE = "male"
    private static final String VITAL_STATUS_VALUE = "false"

    @Autowired
    DemographicDocumentGateway demographicQueryExecutor
    @Autowired
    DemographicRepository demographicRepository

    def setup() {
        demographicRepository.deleteAll()
    }

    def "can execute a demographics query for a subject"() {
        given: "A subject with 4 demographic data points"
        def subjectId = aRandomSubjectId()
        and: "the subject has a birth date on 01/01/2000"
        aDemographic(subjectId, BIRTH_DATE, BIRTH_DATE_VALUE)
        and: "the subject has a death date on 01/01/2010"
        aDemographic(subjectId, DEATH_DATE, DEATH_DATE_VALUE)
        and: "the subject is male"
        aDemographic(subjectId, GENDER, GENDER_VALUE)
        and: "the subject is not alive"
        aDemographic(subjectId, VITAL_STATUS, VITAL_STATUS_VALUE)

        when: "I execute a query for the given subject"
        def query = demographicQuery()
                .forSubject(subjectId)
                .getQuery()
        Demographics result = demographicQueryExecutor.execute(query, REFERENCE_DATE)

        then: "All 4 demographics of the subject are returned"
        result
        result.demographics.size() == 4
        and: "The result only contains data for the given subject"
        result.demographics.stream().allMatch { it.subjectId == subjectId }
    }

    @Unroll
    def "Get demographic data point by type '#type'"() {
        given: "A subject"
        def subjectId = aRandomSubjectId()
        and: "the subject has a birth date on 01/01/2000"
        aDemographic(subjectId, BIRTH_DATE, BIRTH_DATE_VALUE)
        and: "the subject has a death date on 01/01/2010"
        aDemographic(subjectId, DEATH_DATE, DEATH_DATE_VALUE)
        and: "the subject is male"
        aDemographic(subjectId, GENDER, GENDER_VALUE)
        and: "the subject is not alive"
        aDemographic(subjectId, VITAL_STATUS, VITAL_STATUS_VALUE)

        when: "I execute a query for the given subject and type '#type'"
        def query = demographicQuery()
                .forSubject(subjectId)
                .forType(type)
                .getQuery()
        Demographics result = demographicQueryExecutor.execute(query, REFERENCE_DATE)

        then: "I receive the subject's data point with type '#type' and value '#expectedValue'"
        result.demographics.size() == 1
        with(result.demographics.get(0)) {
            it.subjectId == subjectId
            it.demographicType == type
            it.value == expectedValue
        }

        where:
        type         || expectedValue
        BIRTH_DATE   || BIRTH_DATE_VALUE
        DEATH_DATE   || DEATH_DATE_VALUE
        GENDER       || GENDER_VALUE
        VITAL_STATUS || VITAL_STATUS_VALUE
    }

    def "returns empty demographics when no data is known for subject"() {
        given: "There is no data for a subject"

        when: "I execute a query for that subject"
        def query = demographicQuery()
                .forSubject(aRandomSubjectId())
                .getQuery()
        def result = demographicQueryExecutor.execute(query, REFERENCE_DATE)

        then: "Empty demographics are returned"
        result
        result.demographics.empty
    }

    def "cannot execute a demographics query without a subject"() {
        when: "I execute a query without a subjectId"
        def query = demographicQuery()
                .getQuery()
        demographicQueryExecutor.execute(query, REFERENCE_DATE)

        then: "An exception is thrown"
        def ex = thrown(DomainException)
        ex.message == "The subject criterion is missing"
    }

    private aDemographic(SubjectId subjectId, DemographicType type, String value) {
        def demographic = DemographicDocument.newBuilder()
                .withSubjectId(subjectId)
                .withDemographicType(type)
                .withValue(value)
                .build()
        demographicRepository.save(demographic)
    }
}
