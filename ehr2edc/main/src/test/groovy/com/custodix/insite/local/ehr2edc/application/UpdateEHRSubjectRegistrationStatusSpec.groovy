package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.StudyRepository
import com.custodix.insite.local.ehr2edc.command.UpdateEHRSubjectRegistrationStatus
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshotObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired

import static com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.vocabulary.EHRRegistrationStatus.NOT_REGISTERED
import static com.custodix.insite.local.ehr2edc.vocabulary.EHRRegistrationStatus.REGISTERED
import static com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReferenceObjectMother.aDefaultPatientCDWReference
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId

class UpdateEHRSubjectRegistrationStatusSpec extends AbstractSpecification {

    public static final SubjectId SUBJECT_ID_123 = SubjectId.of('subject-id-123')
    public static final StudyId STUDY_ID_123 = StudyId.of("study-id-123")
    @Autowired
    UpdateEHRSubjectRegistrationStatus updateEHRSubjectRegistrationStatus
    @Autowired
    StudyRepository studyRepository

    def "The EHR subject registration status update with 'REGISTERED'"() {
        given: "A study 'study-id-123' with a subject 'subject-id-123' not registered in EHR"
        aStudyWithASubjectNotRegisteredInEHR(SUBJECT_ID_123)

        when: "update the ehr subject registration status with REGISTERED"
        updateEHRSubjectRegistrationStatus.update(aRegisteredRequestFor(SUBJECT_ID_123))

        then: "the study with subject id 'subject-id-123' is registered in EHR"
        def study = studyRepository.getBySubjectId(SUBJECT_ID_123)
        def subject = getSubjectById(study, SUBJECT_ID_123)
        subject.EHRRegistrationStatus == REGISTERED
    }

    def "The EHR subject registration status update with 'NOT_REGISTERED'"() {
        given: "A study 'study-id-123' with a subject 'subject-id-123' not registered in EHR"
        aStudyWithASubjectNotRegisteredInEHR(SUBJECT_ID_123)

        when: "update the ehr subject registration status with NOT_REGISTERED"
        updateEHRSubjectRegistrationStatus.update(aNotRegisteredRequestFor(SUBJECT_ID_123))

        then: "the study with subject id 'subject-id-123' is not registered in EHR"
        def study = studyRepository.getBySubjectId(SUBJECT_ID_123)
        def subject = getSubjectById(study, SUBJECT_ID_123)
        subject.EHRRegistrationStatus == NOT_REGISTERED
    }

    def "The EHR subject registration status update where the subject is linked to a study anymore."() {
        given: "A subject 'subject-id-123' not registered in EHR and  not linked to a study"
        aSubjectNotRegisteredInEHRAndNotLinkedToAStudy(SUBJECT_ID_123)

        when: "update the ehr subject registration status with REGISTERED"
        updateEHRSubjectRegistrationStatus.update(aRegisteredRequestFor(SUBJECT_ID_123))

        then: 'no exceptions must be thrown'
        noExceptionThrown()

    }

    def "Update EHR subject registration status fails when the request is null."() {
        given: "A null request"
        def request = null

        when: "update the ehr subject registration status"
        updateEHRSubjectRegistrationStatus.update(request)

        then: "invalid request"
        def exception = thrown(UseCaseConstraintViolationException)
        exception.getConstraintViolations().size() == 1
        exception.getConstraintViolations()[0].field == ""
        exception.getConstraintViolations()[0].message == "must not be null"
    }

    def "Update EHR subject registration status fails when the subject id request is null."() {
        given: "A request with subject id"
        def request = aDefaultUpdateEHRSubjectRegistrationStatusRequestBuilder()
                .withSubjectId(null)
                .build()

        when: "update the ehr subject registration status"
        updateEHRSubjectRegistrationStatus.update(request)

        then: "invalid request"
        def exception = thrown(UseCaseConstraintViolationException)
        exception.getConstraintViolations().size() == 1
        exception.getConstraintViolations()[0].field == "subjectId"
        exception.getConstraintViolations()[0].message == "must not be null"
    }

    def "Update EHR subject registration status fails when the patient reference request is null."() {
        given: "A request with patient reference id"
        def request = aDefaultUpdateEHRSubjectRegistrationStatusRequestBuilder()
                .withPatientCDWReference(null)
                .build()

        when: "update the ehr subject registration status"
        updateEHRSubjectRegistrationStatus.update(request)

        then: "invalid request"
        def exception = thrown(UseCaseConstraintViolationException)
        exception.getConstraintViolations().size() == 1
        exception.getConstraintViolations()[0].field == "patientCDWReference"
        exception.getConstraintViolations()[0].message == "must not be null"
    }

    def "Update EHR subject registration status fails when the status request is null."() {
        given: "A request with patient reference id"
        def request = aDefaultUpdateEHRSubjectRegistrationStatusRequestBuilder()
                .withStatus(null)
                .build()

        when: "update the ehr subject registration status"
        updateEHRSubjectRegistrationStatus.update(request)

        then: "invalid request"
        def exception = thrown(UseCaseConstraintViolationException)
        exception.getConstraintViolations().size() == 1
        exception.getConstraintViolations()[0].field == "status"
        exception.getConstraintViolations()[0].message == "must not be null"
    }

    private getSubjectById(Study study, SubjectId subjectId) {
        study.toSnapShot().subjects.stream().filter { subject -> subject.subjectId == subjectId }
                .findFirst().get()
    }

    private aDefaultUpdateEHRSubjectRegistrationStatusRequestBuilder() {
        UpdateEHRSubjectRegistrationStatus.Request.newBuilder()
                .withSubjectId(aRandomSubjectId())
                .withPatientCDWReference(aDefaultPatientCDWReference())
                .withStatus(REGISTERED)
    }

    void aStudyWithASubjectNotRegisteredInEHR(SubjectId subjectId) {
        def aNotRegisteredEHRSubject = aDefaultSubjectSnapshotBuilder()
                .withSubjectId(subjectId)
                .withEHRRegistrationStatus(NOT_REGISTERED).build()
        def study = StudySnapshotObjectMother.aDefaultStudySnapshotBuilder()
                .withStudyId(STUDY_ID_123)
                .withSubjects(Collections.singleton(aNotRegisteredEHRSubject))
                .build()

        studyRepository.save(Study.restoreSnapshot(study));
    }

    void aSubjectNotRegisteredInEHRAndNotLinkedToAStudy(SubjectId subjectId) {
        aDefaultSubjectSnapshotBuilder()
                .withSubjectId(subjectId)
                .withEHRRegistrationStatus(NOT_REGISTERED).build()
    }

    UpdateEHRSubjectRegistrationStatus.Request aRegisteredRequestFor(SubjectId subjectId) {
        aDefaultUpdateEHRSubjectRegistrationStatusRequestBuilder()
            .withSubjectId(subjectId)
            .withStatus(REGISTERED)
            .build()
    }

    UpdateEHRSubjectRegistrationStatus.Request aNotRegisteredRequestFor(SubjectId subjectId) {
        aDefaultUpdateEHRSubjectRegistrationStatusRequestBuilder()
                .withSubjectId(subjectId)
                .withStatus(NOT_REGISTERED)
                .build()
    }
}
