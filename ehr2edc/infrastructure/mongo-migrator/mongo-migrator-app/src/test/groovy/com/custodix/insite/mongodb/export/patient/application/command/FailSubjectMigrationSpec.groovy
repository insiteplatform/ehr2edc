package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.patient.application.api.FailSubjectMigration
import com.custodix.insite.mongodb.export.patient.domain.model.common.SubjectMigrationStatus
import com.custodix.insite.mongodb.export.patient.domain.repository.SubjectMigrationRepository
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.subjectmigration.SubjectMigrationDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.SubjectMigrationDocumentRepository
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.CriteriaDefinition
import spock.lang.PendingFeature
import spock.lang.Unroll

import javax.validation.ConstraintViolationException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static com.custodix.insite.mongodb.export.patient.domain.model.DomainTime.now
import static org.springframework.data.mongodb.core.query.Criteria.where

class FailSubjectMigrationSpec extends AbstractSubjectMigrationSpec {

    public static final String NOW = now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    public static final String YESTERDAY = now().minusDays(1L).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    @Autowired
    private SubjectMigrationDocumentRepository subjectMigrationDocumentRepository
    @Autowired
    private SubjectMigrationRepository subjectMigrationRepository
    @Autowired
    private FailSubjectMigration failSubjectMigration;

    private static final String SUBJECT_ID = "999-888"

    @Unroll
    def "Fail subject migration correctly when the subject migration in status #status"(SubjectMigrationStatus status) {
        given: "a subject migration with #status"
        def subjectId = SubjectId.of(SUBJECT_ID)
        def originalSubjectMigration = createSubjectMigrationFor(subjectId, status)
        and: "failed date == now"
        def failedDate = LocalDateTime.now()

        when: "fail subject migration"
        failSubjectMigration.fail(FailSubjectMigration.Request.newBuilder().withSubjectId(subjectId).withFailDate(failedDate).build())

        then: "assert that the subject migration is correctly failed"
        def subjectMigration = subjectMigrationDocumentRepository.getBySubjectId(subjectId)
        subjectMigration.subjectId.getId() == SUBJECT_ID
        subjectMigration.status == SubjectMigrationStatus.FAILED
        subjectMigration.failDate.isEqual(failedDate)
        subjectMigration.startDate.isEqual(originalSubjectMigration.getStartDate())
        subjectMigration.endDate.isEqual(originalSubjectMigration.getEndDate())

        where:
        status << SubjectMigrationStatus.values()
    }

    @Unroll
    @PendingFeature(reason = "No validation configuration in mongo migrator see (EHR2EDC-644)")
    def "Fail subject migration fails when request is invalid "(SubjectId subjectId, LocalDateTime failedDate, String violationField, String violationMessage) {
        given: "an invalid request with subject id #subjectIdValue and endDate #failedDate"
        def request = FailSubjectMigration.Request.newBuilder().withSubjectId(subjectId).withFailDate(failedDate).build()

        when: "fail subject migration"
        failSubjectMigration.fail(request)

        then: "constraint violation exception is thrown"
        def violation = thrown(ConstraintViolationException)
        violation.constraintViolations.size() == 1
        violation.constraintViolations[0].message == violationMessage
        violation.constraintViolations[1].propertyPath.toString() == violationField

        where:
        subjectId | failedDate  | violationField | violationMessage
        SubjectId.of(SUBJECT_ID)     | null | "date" | "must not be null"
        null     | LocalDateTime.now() | "subjectId" |"must not be null"
        SubjectId.of(null)     | LocalDateTime.now()  | "subjectId.id" | "must not be blank"
        SubjectId.of("  ")     | LocalDateTime.now()  | "subjectId.id" | "must not be blank"
        SubjectId.of("")     | LocalDateTime.now()  | "subjectId.id" | "must not be blank"
    }

    private SubjectMigrationDocument createSubjectMigrationFor(final SubjectId subjectId, final SubjectMigrationStatus status) {
        return subjectMigrationDocumentRepository.insert(SubjectMigrationDocument.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(LocalDateTime.now().minusDays(3L))
                .withEndDate(LocalDateTime.now().minusDays(1L))
                .withStatus(status)
                .build())
    }

    private CriteriaDefinition createNoActiveMigrationCriteria(final SubjectId subjectId) {
        where("subjectId").is(subjectId).and("status").is(SubjectMigrationStatus.STARTED)
    }
}
