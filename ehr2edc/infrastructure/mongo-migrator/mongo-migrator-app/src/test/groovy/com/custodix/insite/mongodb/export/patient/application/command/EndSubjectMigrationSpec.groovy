package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.patient.application.api.EndSubjectMigration
import com.custodix.insite.mongodb.export.patient.domain.exceptions.DomainException
import com.custodix.insite.mongodb.export.patient.domain.model.common.SubjectMigrationStatus
import com.custodix.insite.mongodb.export.patient.domain.repository.SubjectMigrationRepository
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.subjectmigration.SubjectMigrationDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.SubjectMigrationDocumentRepository
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.CriteriaDefinition
import org.springframework.data.mongodb.core.query.Query
import spock.lang.PendingFeature
import spock.lang.Unroll

import javax.validation.ConstraintViolationException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static com.custodix.insite.mongodb.export.patient.domain.model.DomainTime.now
import static org.springframework.data.mongodb.core.query.Criteria.where

class EndSubjectMigrationSpec extends AbstractSubjectMigrationSpec {

    public static final String NOW = now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    public static final String YESTERDAY = now().minusDays(1L).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    @Autowired
    private SubjectMigrationDocumentRepository subjectMigrationDocumentRepository
    @Autowired
    private SubjectMigrationRepository subjectMigrationRepository
    @Autowired
    private EndSubjectMigration endSubjectMigration;

    private static final String SUBJECT_ID = "999-888"

    @Unroll
    def "End subject migration correctly when the subject migration is started"(String subjectIdValue, String startDateValue, String endDateValue) {
        given: "a subject migration is started for subject id #subjectIdValue on startDateValue"
        def subjectId = SubjectId.of(subjectIdValue)
        def startDate = LocalDateTime.parse(startDateValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        createActiveSubjectMigrationFor(subjectId, startDate)
        and: "end date #endDateValue"
        def endDate = LocalDateTime.parse(endDateValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        when: "end subject migration"
        endSubjectMigration.end(EndSubjectMigration.Request.newBuilder().withSubjectId(subjectId).withEndDate(endDate).build())

        then: "assert that the subject migration is correctly ended"
        def subjectMigration = subjectMigrationDocumentRepository.findBySubjectId(subjectId).get()
        subjectMigration.subjectId.getId() == subjectIdValue
        subjectMigration.status == SubjectMigrationStatus.ENDED
        subjectMigration.startDate.isEqual(startDate)
        subjectMigration.endDate.isEqual(endDate)

        where:
         subjectIdValue | startDateValue    | endDateValue
         SUBJECT_ID     | YESTERDAY         | NOW
    }

    @Unroll
    @PendingFeature(reason = "No validation configuration in mongo migrator see (EHR2EDC-644)")
    def "End subject migration fails when request is invalid "(SubjectId subjectId, LocalDateTime endDate, String violationField, String violationMessage) {
        given: "an invalid request with subject id #subjectIdValue and endDate #endDateValue"
        def request = EndSubjectMigration.Request.newBuilder().withSubjectId(subjectId).withEndDate(endDate).build()

        when: "end subject migration"
        endSubjectMigration.end(request)

        then: "constraint violation exception is thrown"
        def violation = thrown(ConstraintViolationException)
        violation.constraintViolations.size() == 1
        violation.constraintViolations[0].message == violationMessage
        violation.constraintViolations[1].propertyPath.toString() == violationField

        where:
        subjectId | endDate  | violationField | violationMessage
        SubjectId.of(SUBJECT_ID)     | null | "endDate" | "must not be null"
        null     | LocalDateTime.now() | "subjectId" |"must not be null"
        SubjectId.of(null)     | LocalDateTime.now()  | "subjectId.id" | "must not be blank"
        SubjectId.of("  ")     | LocalDateTime.now()  | "subjectId.id" | "must not be blank"
        SubjectId.of("")     | LocalDateTime.now()  | "subjectId.id" | "must not be blank"
    }

    @Unroll
    def "End subject migration fails when the subject migration is not existing"(String subjectIdValue) {
        given: "assert no subject migration is existing for subject id #subjectIdValue"
        def subjectId = SubjectId.of(subjectIdValue)
        assertNoActiveMigrationFor(subjectId)
        and: "end date #endDateValue"
        def endDate = LocalDateTime.parse(NOW, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        when: "end subject migration"
        endSubjectMigration.end(EndSubjectMigration.Request.newBuilder().withSubjectId(subjectId).withEndDate(endDate).build())

        then: "exception is thrown"
        def exception = thrown(DomainException.class)
        exception.message == "Cannot find SubjectMigration for subject id 999-888"

        where:
        subjectIdValue | _
        SUBJECT_ID | _
    }

    @Unroll
    def "End subject migration fails when the subject migration has the status #status"(SubjectMigrationStatus status) {
        given: "a subject migration with #status for subject id #subjectIdValue "
        def subjectId = SubjectId.of(SUBJECT_ID)
        createSubjectMigrationFor(subjectId, status)
        and: "end date #endDateValue"
        def endDate = LocalDateTime.parse(NOW, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        when: "end subject migration"
        endSubjectMigration.end(EndSubjectMigration.Request.newBuilder().withSubjectId(subjectId).withEndDate(endDate).build())

        then: "exception is thrown"
        def exception = thrown(DomainException.class)
        exception.message == String.format("Subject migration for subject id '999-888' has currently the status '%s' and cannot be ended", status)

        where:
        status  << Arrays.stream(SubjectMigrationStatus.values())
                .filter{s -> s != SubjectMigrationStatus.STARTED}
                .toArray{size -> new SubjectMigrationStatus[size]};
    }


    private void createSubjectMigrationFor(final SubjectId subjectId, final SubjectMigrationStatus status) {
        subjectMigrationDocumentRepository.insert(SubjectMigrationDocument.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(LocalDateTime.now().minusDays(1L))
                .withEndDate(LocalDateTime.now())
                .withStatus(status)
                .build())
    }

    private void assertNoActiveMigrationFor(final SubjectId subjectId) {
        assert !hasActivateSubjectMigrationFor(subjectId)
    }

    private boolean hasActivateSubjectMigrationFor(final SubjectId subjectId) {
        Query query = new Query()
        query.addCriteria(createNoActiveMigrationCriteria(subjectId))
        return mongoTemplate.exists(query, SubjectMigrationDocument.class)
    }

    private CriteriaDefinition createNoActiveMigrationCriteria(final SubjectId subjectId) {
        where("subjectId").is(subjectId).and("status").is(SubjectMigrationStatus.STARTED)
    }

    private void createActiveSubjectMigrationFor(final SubjectId subjectId, final LocalDateTime startDate) {
        subjectMigrationDocumentRepository.insert(SubjectMigrationDocument.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(startDate)
                .withEndDate(null)
                .withStatus(SubjectMigrationStatus.STARTED)
                .build())
    }
}
