package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.export.patient.application.command.ExportPatientCommand
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientEnded
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientFailed
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientStarting
import com.custodix.insite.mongodb.export.patient.domain.exceptions.SubjectMigrationAlreadyStartedException
import com.custodix.insite.mongodb.export.patient.domain.model.common.SubjectMigrationStatus
import com.custodix.insite.mongodb.export.patient.domain.repository.SubjectMigrationRepository
import com.custodix.insite.mongodb.export.patient.domain.service.ExportPatientRunner
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.subjectmigration.SubjectMigrationDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.SubjectMigrationDocumentRepository
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.CriteriaDefinition
import org.springframework.data.mongodb.core.query.Query
import spock.lang.Title

import static org.springframework.data.mongodb.core.query.Criteria.where

@Title("Export patient handle subject migration")
class ExportPatientSubjectMigrationSpec extends AbstractExportPatientSpec {

    private static final String PATIENT_EXPORTER_ID = "patient-exporter-id-123-123"
    private static final String PATIENT_ID = "7"
    private static final String NAMESPACE = "MASTER_PATIENT_INDEX"
    private static final String SUBJECT_ID = "999-888"

    @Autowired
    private SubjectMigrationDocumentRepository subjectMigrationDocumentRepository
    @Autowired
    private SubjectMigrationRepository subjectMigrationRepository
    @Autowired
    private ExportPatient exportPatient

    def "Export of the patient should publish start and end event"(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "no subject migration is started for subject id #subjectId"
        assertNoActiveMigrationFor(subjectId)

        when: "Exporting patient"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "assert start event"
        def exportPatientStarted = eventPublisher.poll() as ExportPatientStarting
        exportPatientStarted.subjectId == SUBJECT_ID
        exportPatientStarted.namespace == NAMESPACE
        exportPatientStarted.patientId == PATIENT_ID
        exportPatientStarted.date == testTimeService.now()
        exportPatientStarted.patientExporterId != null

        and: "assert end event"
        def exportPatientEnded = eventPublisher.poll() as ExportPatientEnded
        exportPatientEnded.subjectId == SUBJECT_ID
        exportPatientEnded.namespace == NAMESPACE
        exportPatientEnded.patientId == PATIENT_ID
        exportPatientEnded.date == testTimeService.now()

        and: "no more event"
        eventPublisher.poll() == null

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

    def "Export of the patient should fail when the subject migration is active"(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "an existing subject migration is started for subject id #subjectId"
        createActiveSubjectMigrationFor(subjectId)

        when: "Exporting patient"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "migration should failed"
        SubjectMigrationAlreadyStartedException exception = thrown()
        exception.message == "Migration cannot be started because an existing migration is running for subject is '"+ subjectId.getId() +"' and has not exceed the maximum running period."
        and: "assert that the activate subject migration is still active"
        def subjectMigration = subjectMigrationDocumentRepository.getBySubjectId(subjectId)
        subjectMigration.subjectId == subjectId
        subjectMigration.status == SubjectMigrationStatus.STARTED
        subjectMigration.startDate == testTimeService.now()
        subjectMigration.endDate == null
        subjectMigration.patientExporterId == PATIENT_EXPORTER_ID
        and: "no more event"
        eventPublisher.poll() == null

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

    def "Export of the patient should be successful when the previous migration has been started and exceed the max running period of 4 hours"(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "an existing subject migration is started 4 hours ago for subject id #subjectId"
        createActiveSubjectMigrationFor(subjectId, 4L)

        when: "Exporting patient"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "assert start event"
        def exportPatientStarted = eventPublisher.poll() as ExportPatientStarting
        exportPatientStarted.subjectId == SUBJECT_ID
        exportPatientStarted.namespace == NAMESPACE
        exportPatientStarted.patientId == PATIENT_ID
        exportPatientStarted.date == testTimeService.now()
        and: "the patient exporter id is a new id"
        exportPatientStarted.patientExporterId != null
        exportPatientStarted.patientExporterId != PATIENT_EXPORTER_ID
        and: "assert end event"
        def exportPatientEnded = eventPublisher.poll() as ExportPatientEnded
        exportPatientEnded.subjectId == SUBJECT_ID
        exportPatientEnded.namespace == NAMESPACE
        exportPatientEnded.patientId == PATIENT_ID
        exportPatientEnded.date == testTimeService.now()
        and: "no more event"
        eventPublisher.poll() == null

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

    def "Export of the patient should be successful when the previous migration has failed" (PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "an existing subject migration is failed for subject id #subjectId"
        createFailedSubjectMigrationFor(subjectId)

        when: "Exporting patient"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "assert start event"
        def exportPatientStarted = eventPublisher.poll() as ExportPatientStarting
        exportPatientStarted.subjectId == SUBJECT_ID
        exportPatientStarted.namespace == NAMESPACE
        exportPatientStarted.patientId == PATIENT_ID
        exportPatientStarted.date == testTimeService.now()
        and: "the patient exporter id is a new id"
        exportPatientStarted.patientExporterId != null
        exportPatientStarted.patientExporterId != PATIENT_EXPORTER_ID
        and: "assert end event"
        def exportPatientEnded = eventPublisher.poll() as ExportPatientEnded
        exportPatientEnded.subjectId == SUBJECT_ID
        exportPatientEnded.namespace == NAMESPACE
        exportPatientEnded.patientId == PATIENT_ID
        exportPatientEnded.date == testTimeService.now()
        and: "no more event"
        eventPublisher.poll() == null

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

    def "Subject migration should stated failed, when export patient has failed."(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "Export patient fails"
        ExportPatientRunner exportPatientRunner = Mock(ExportPatientRunner)
        exportPatientRunner.run(_) >> { throw new RuntimeException("Export has failed")}
        ExportPatient exportPatient = new ExportPatientCommand(subjectMigrationRepository, exportPatientRunner, 1000L)

        when: "Exporting patient"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "assert start event"
        def exportPatientStarted = eventPublisher.poll() as ExportPatientStarting
        exportPatientStarted.subjectId == SUBJECT_ID
        exportPatientStarted.namespace == NAMESPACE
        exportPatientStarted.patientId == PATIENT_ID
        exportPatientStarted.date == testTimeService.now()
        exportPatientStarted.patientExporterId != null
        and: "assert end event"
        def exportPatientFailed = eventPublisher.poll() as ExportPatientFailed
        exportPatientFailed.subjectId == SUBJECT_ID
        exportPatientFailed.namespace == NAMESPACE
        exportPatientFailed.patientId == PATIENT_ID
        exportPatientFailed.date == testTimeService.now()
        and: "no more event"
        eventPublisher.poll() == null
        and: "exception is thrown"
        RuntimeException exception = thrown(RuntimeException)
        exception.message == String.format("Patient export id %s has failed", exportPatientStarted.patientExporterId)
        exception.cause.message == "Export has failed"

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
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

    private void createActiveSubjectMigrationFor(final SubjectId subjectId, final long numberOfDaysAgo) {
        subjectMigrationDocumentRepository.insert(SubjectMigrationDocument.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(testTimeService.now().minusHours(numberOfDaysAgo))
                .withStatus(SubjectMigrationStatus.STARTED)
                .withPatientExporterId(PATIENT_EXPORTER_ID)
                .build())
    }

    private void createActiveSubjectMigrationFor(final SubjectId subjectId) {
        subjectMigrationDocumentRepository.insert(SubjectMigrationDocument.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(testTimeService.now())
                .withStatus(SubjectMigrationStatus.STARTED)
                .withPatientExporterId(PATIENT_EXPORTER_ID)
                .build())
    }

    private void createFailedSubjectMigrationFor(final SubjectId subjectId) {
        subjectMigrationDocumentRepository.insert(SubjectMigrationDocument.newBuilder()
                .withSubjectId(subjectId)
                .withStartDate(testTimeService.now())
                .withEndDate(testTimeService.now())
                .withPatientExporterId(PATIENT_EXPORTER_ID)
                .withStatus(SubjectMigrationStatus.FAILED)
                .build())
    }
}
