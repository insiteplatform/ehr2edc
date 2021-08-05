package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.patient.application.api.StartSubjectMigration
import com.custodix.insite.mongodb.export.patient.domain.exceptions.SubjectMigrationAlreadyStartedException
import com.custodix.insite.mongodb.export.patient.domain.model.common.SubjectMigrationStatus
import com.custodix.insite.mongodb.export.patient.domain.repository.SubjectMigrationRepository
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.subjectmigration.SubjectMigrationDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.SubjectMigrationDocumentRepository
import com.custodix.insite.mongodb.vocabulary.PatientExporterId
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

class StartSubjectMigrationSpec extends AbstractSubjectMigrationSpec {

    @Autowired
    private SubjectMigrationDocumentRepository subjectMigrationDocumentRepository
    @Autowired
    private SubjectMigrationRepository subjectMigrationRepository
    @Autowired
    private StartSubjectMigration startSubjectMigration;

    private static final String SUBJECT_ID = "999-888"

    def "Start subject migration correctly when no subject migration is existing"(String subjectIdValue, String startDateValue, String patientExporterId) {
        given: "no subject migration is started for subject id #subjectIdValue"
        def subjectId = SubjectId.of(subjectIdValue)
        assertNoActiveMigrationFor(subjectId)
        and: "starting date #startDateValue"
        def startDate = LocalDateTime.parse(startDateValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        when: "start subject migration"
        startSubjectMigration.start(StartSubjectMigration.Request.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(startDate)
                .withPatientExporterId(PatientExporterId.of(patientExporterId))
                .build())

        then: "assert that the subject migration is correctly started"
        def subjectMigration = subjectMigrationDocumentRepository.getBySubjectId(subjectId)
        subjectMigration.subjectId.getId() == subjectIdValue
        subjectMigration.status == SubjectMigrationStatus.STARTED
        subjectMigration.startDate.isEqual(startDate)
        subjectMigration.endDate == null
        subjectMigration.failDate == null
        subjectMigration.patientExporterId == patientExporterId

        where:
         subjectIdValue | startDateValue | patientExporterId
         SUBJECT_ID     | now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) | UUID.randomUUID().toString()
    }

    def "Start subject migration should fail when the subject migration is active"(String subjectIdValue, String startDateValue, String previousStartDateValueIsNow,
                                                                                   String patientExporterId, String previousPatientExporterId) {
        given: "starting date #startDateValue"
        def startDate = LocalDateTime.parse(startDateValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        and : "previous starting date #previousStartDateValueIsNow of the subject migration"
        def previousStartDate = LocalDateTime.parse(previousStartDateValueIsNow, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        and: "an existing subject migration is started for subject id #subjectId at previousStartDateValueIsNow and patient exporter id #previousPatientExporterId"
        def subjectId = SubjectId.of(subjectIdValue)
        createActiveSubjectMigrationFor(subjectId, previousStartDate, previousPatientExporterId)

        when: "start subject migration"
        startSubjectMigration.start(StartSubjectMigration.Request.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(startDate)
                .withPatientExporterId(PatientExporterId.of(patientExporterId))
                .build())

        then: "migration should failed"
        SubjectMigrationAlreadyStartedException exception = thrown()
        exception.message == "Migration cannot be started because an existing migration is running for subject is '"+ subjectId.getId() +"' and has not exceed the maximum running period."
        and: "assert that the activate subject migration is still active"
        def subjectMigration = subjectMigrationDocumentRepository.getBySubjectId(subjectId)
        subjectMigration.subjectId == subjectId
        subjectMigration.status == SubjectMigrationStatus.STARTED
        subjectMigration.startDate.isEqual(previousStartDate)
        subjectMigration.endDate == null
        subjectMigration.failDate == null
        subjectMigration.patientExporterId == previousPatientExporterId

        where:
        subjectIdValue | startDateValue                                     | previousStartDateValueIsNow  | patientExporterId | previousPatientExporterId
        SUBJECT_ID     | now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)  | now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) | "newPatientExporterId" | "previousPatientExporterId"
    }

    def "Start subject migration successfully when the previous migration has been started and exceed the max running period of 4 hours"(
            String subjectIdValue,
            String startDateValue,
            String previousStartDateValueIs4HoursAgo,
            String patientExporterId,
            String previousPatientExporterId) {
        given: "starting date #startDateValue"
        def startDate = LocalDateTime.parse(startDateValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        and : "previous starting date #previousStartDateValueIs4HoursAgo of the subject migration"
        def previousStartDate = LocalDateTime.parse(previousStartDateValueIs4HoursAgo, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        and: "an existing subject migration is started for subject id #subjectId at previousStartDateValueIs4HoursAgo"
        def subjectId = SubjectId.of(subjectIdValue)
        createActiveSubjectMigrationFor(subjectId, previousStartDate, previousPatientExporterId)

        when: "start subject migration"
        startSubjectMigration.start(StartSubjectMigration.Request.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(startDate)
                .withPatientExporterId(PatientExporterId.of(patientExporterId))
                .build())
        then: "assert that the migration is started"
        def subjectMigration = subjectMigrationDocumentRepository.getBySubjectId(subjectId)
        subjectMigration.subjectId == subjectId
        subjectMigration.status == SubjectMigrationStatus.STARTED
        subjectMigration.startDate.isEqual(startDate)
        subjectMigration.endDate == null
        subjectMigration.failDate == null
        subjectMigration.patientExporterId == patientExporterId

        where:
        subjectIdValue | startDateValue                                       | previousStartDateValueIs4HoursAgo | patientExporterId | previousPatientExporterId
        SUBJECT_ID     | now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)  | now().minusHours(4L).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) | "newPatientExporterId" | "previousPatientExporterId"
    }

    def "Start subject migration successfully  when the previous migration has failed" (String subjectIdValue,
                                                                                        String startDateValue,
                                                                                        String patientExporterId,
                                                                                        String previousPatientExporterId) {
        given: "starting date #startDateValue"
        def startDate = LocalDateTime.parse(startDateValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        and: "an existing subject migration is failed for subject id #subjectId"
        def subjectId = SubjectId.of(subjectIdValue)
        createSubjectMigrationStartDate2MinAgoAndFailedNowFor(subjectId, previousPatientExporterId)

        when: "start subject migration"
        startSubjectMigration.start(StartSubjectMigration.Request.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(startDate)
                .withPatientExporterId(PatientExporterId.of(patientExporterId))
                .build())

        then: "assert that the migration is done"
        def subjectMigration = subjectMigrationDocumentRepository.getBySubjectId(subjectId)
        subjectMigration.subjectId == subjectId
        subjectMigration.status == SubjectMigrationStatus.STARTED
        subjectMigration.startDate == startDate
        subjectMigration.endDate == null
        subjectMigration.failDate == null
        subjectMigration.patientExporterId == patientExporterId

        where:
        subjectIdValue | startDateValue | patientExporterId | previousPatientExporterId
        SUBJECT_ID     | now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) | "newPatientExporterId" | "previousPatientExporterId"
    }

    def "Start subject migration successfully  when the previous migration has ended" (String subjectIdValue,
                                                                                       String startDateValue,
                                                                                       String patientExporterId,
                                                                                       String previousPatientExporterId) {
        given: "starting date #startDateValue"
        def startDate = LocalDateTime.parse(startDateValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        and: "an existing subject migration is ended for subject id #subjectId"
        def subjectId = SubjectId.of(subjectIdValue)
        createSubjectMigrationStartDate2MinAgoAndEndedNowFor(subjectId, previousPatientExporterId)

        when: "start subject migration"
        startSubjectMigration.start(StartSubjectMigration.Request.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(startDate)
                .withPatientExporterId(PatientExporterId.of(patientExporterId))
                .build())

        then: "assert that the migration is done"
        def subjectMigration = subjectMigrationDocumentRepository.getBySubjectId(subjectId)
        subjectMigration.subjectId == subjectId
        subjectMigration.status == SubjectMigrationStatus.STARTED
        subjectMigration.startDate == startDate
        subjectMigration.endDate == null
        subjectMigration.failDate == null
        subjectMigration.patientExporterId == patientExporterId

        where:
        subjectIdValue | startDateValue | patientExporterId | previousPatientExporterId
        SUBJECT_ID     | now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) | "newPatientExporterId" | "previousPatientExporterId"
    }

    @Unroll
    @PendingFeature(reason = "No validation configuration in mongo migrator. see (EHR2EDC-644)")
    def "Starting subject migration fails when request is invalid "(SubjectId subjectId,
                                                                    LocalDateTime startDate,
                                                                    PatientExporterId patientExporterId,
                                                                    String violationField,
                                                                    String violationMessage) {
        given: "an invalid request with subject id #subjectIdValue and startDate #startDate"
        def request = StartSubjectMigration.Request.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(startDate)
                .withPatientExporterId(patientExporterId)
                .build()

        when: "start subject migration"
        startSubjectMigration.start(request)

        then: "constraint violation exception is thrown"
        def violation = thrown(ConstraintViolationException)
        violation.constraintViolations.size() == 1
        violation.constraintViolations[0].message == violationMessage
        violation.constraintViolations[1].propertyPath.toString() == violationField

        where:
        subjectId                | startDate           | patientExporterId                         | violationField         | violationMessage
        SubjectId.of(SUBJECT_ID) | null                | PatientExporterId.of("patientExporterId") | "startDate"            | "must not be null"
        null                     | LocalDateTime.now() | PatientExporterId.of("patientExporterId") | "subjectId"            |"must not be null"
        SubjectId.of(null)       | LocalDateTime.now() | PatientExporterId.of("patientExporterId") | "subjectId.id"         | "must not be blank"
        SubjectId.of("  ")       | LocalDateTime.now() | PatientExporterId.of("patientExporterId") | "subjectId.id"         | "must not be blank"
        SubjectId.of("")         | LocalDateTime.now() | PatientExporterId.of("patientExporterId") | "subjectId.id"         | "must not be blank"
        SubjectId.of(SUBJECT_ID) | LocalDateTime.now() | null                                      | "patientExporterId"    | "must not be blank"
        SubjectId.of(SUBJECT_ID) | LocalDateTime.now() | PatientExporterId.of("")                  | "patientExporterId.id" | "must not be blank"
        SubjectId.of(SUBJECT_ID) | LocalDateTime.now() | PatientExporterId.of(" ")                 | "patientExporterId.id" | "must not be blank"
        SubjectId.of(SUBJECT_ID) | LocalDateTime.now() | PatientExporterId.of(null)                | "patientExporterId.id" | "must not be blank"

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

    private void createActiveSubjectMigrationFor(final SubjectId subjectId, final LocalDateTime startDate, String patientExporterId) {
        subjectMigrationDocumentRepository.insert(SubjectMigrationDocument.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(startDate)
                .withEndDate(null)
                .withStatus(SubjectMigrationStatus.STARTED)
                .withPatientExporterId(patientExporterId)
                .build())
    }

    private void createSubjectMigrationStartDate2MinAgoAndFailedNowFor(final SubjectId subjectId, String patientExporterId) {
        subjectMigrationDocumentRepository.insert(SubjectMigrationDocument.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(now().minusMinutes(2L))
                .withEndDate(null)
                .withFailDate(now())
                .withStatus(SubjectMigrationStatus.FAILED)
                .withPatientExporterId(patientExporterId)
                .build())
    }

    private void createSubjectMigrationStartDate2MinAgoAndEndedNowFor(final SubjectId subjectId, String patientExporterId) {
        subjectMigrationDocumentRepository.insert(SubjectMigrationDocument.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(now().minusMinutes(2L))
                .withEndDate(now())
                .withFailDate(null)
                .withStatus(SubjectMigrationStatus.ENDED)
                .withPatientExporterId(patientExporterId)
                .build())
    }
}
