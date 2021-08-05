package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.query.GetEventPopulationReadiness
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.snapshots.InvestigatorSnapshotObjectMother.aDefaultInvestigatorSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.MetadataDefinitionSnapshotObjectMother.aDefaultMetaDataDefinitionSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.StudySnapshotObjectMother.aDefaultStudySnapshotBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.vocabulary.EHRRegistrationStatus.NOT_REGISTERED
import static com.custodix.insite.local.ehr2edc.vocabulary.EHRRegistrationStatus.REGISTERED
import static com.custodix.insite.local.ehr2edc.vocabulary.PopulationNotReadyReason.SUBJECT_MIGRATION_STARTED
import static java.util.Collections.singletonList

class GetEventPopulationReadinessSpec extends AbstractSpecification {

    @Autowired
    GetEventPopulationReadiness getEventPopulationReadiness

    def "Subject is ready for events population when the subject is registered in EHR and the study has events"(StudyId studyId, SubjectId subjectId, String patientExporterId) {
        given: "Study #studyId has events and a subject registered in EHR"
        createStudyWithSubjectEHRRegisteredAndEventFor(studyId, subjectId, USER_ID_KNOWN)

        when: "requesting whether the study with id #studyId is ready for population for subject id #subjectId"
        def response = getEventPopulationReadiness.getEventPopulationReadiness(GetEventPopulationReadiness.Request.newBuilder().withStudyId(studyId).withSubjectId(subjectId).build())

        then: "it returns it is ready"
        response.isReady()
        !response.isSubjectMigrationInProgress()
        response.notReadyReason == null

        where:
        studyId                    | subjectId                      | patientExporterId
        StudyId.of("study-id-098") | SubjectId.of("subject-id-098") | "patientExporterId-098"
    }

    def "Subject is not ready for events population when the subject is NOT registered in EHR and the study has events"(StudyId studyId, SubjectId subjectId, String patientExporterId) {
        given: "Study #studyId has events and a subject NOT registered in EHR"
        createStudyWithSubjectEHRNotRegisteredAndEventFor(studyId, subjectId, USER_ID_KNOWN)

        when: "requesting whether the study with id #studyId is ready for  population for subject id #subjectId"
        def response = getEventPopulationReadiness.getEventPopulationReadiness(GetEventPopulationReadiness.Request.newBuilder().withStudyId(studyId).withSubjectId(subjectId).build())

        then: "it returns it is not ready"
        !response.isReady()
        response.isSubjectMigrationInProgress()
        response.notReadyReason == SUBJECT_MIGRATION_STARTED

        where:
        studyId                    | subjectId                      | patientExporterId
        StudyId.of("study-id-098") | SubjectId.of("subject-id-098") | "patientExporterId-098"
    }

    def "Subject is not ready for events population when the study has no events"(StudyId studyId, SubjectId subjectId) {
        given: "Study #studyId without events"
        createStudyWithSubjectEHRRegisteredAndWithoutEventFor(studyId, subjectId, USER_ID_KNOWN)

        when: "requesting whether the study with id #studyId is ready for  population for subject id #subjectId"
        def response = getEventPopulationReadiness.getEventPopulationReadiness(GetEventPopulationReadiness.Request.newBuilder().withStudyId(studyId).withSubjectId(subjectId).build())

        then: "Get user exception"
        def userException = thrown(UserException)
        userException.message == "Study 'study-id-098' has no events defined, please ensure that the study has events defined before populating."

        where:
        studyId                    | subjectId
        StudyId.of("study-id-098") | SubjectId.of("subject-id-098")
    }

    @Unroll
    def "Is study ready for event population with invalid study id"(StudyId studyId, SubjectId subjectId) {
        given: "An invalid request with study id #studyId and subject id #subjectId"
        def invalidRequest = GetEventPopulationReadiness.Request.newBuilder().withStudyId(studyId).withSubjectId(subjectId).build()

        when: "requesting whether the study with id #studyId is ready for  population for subject id #subjectId"
        getEventPopulationReadiness.getEventPopulationReadiness(invalidRequest)

        then: "an AccessDeniedException is thrown"
        thrown(AccessDeniedException)

        where:
        studyId           | subjectId
        StudyId.of(null)  | SubjectId.of("subject-id-098")
        StudyId.of("   ") | SubjectId.of("subject-id-098")
        StudyId.of("")    | SubjectId.of("subject-id-098")
    }

    def "Is study ready for event population with null request"() {
        given: "A null request"
        def nullRequest = null

        when: "requesting whether the study is ready for population with null request"
        getEventPopulationReadiness.getEventPopulationReadiness(nullRequest)

        then: "invalid request"
        def exception = thrown(UseCaseConstraintViolationException)
        exception.getConstraintViolations().size() == 1
        exception.getConstraintViolations()[0].message == "must not be null"
    }

    @Unroll
    def "Is study ready for event population with study for unauthorized user"(StudyId studyId, SubjectId subjectId) {
        given: "Study #studyId has events for not current user"
        createStudyWithEventFor(studyId, subjectId, USER_ID_OTHER)
        and: "A request with study id #studyId and subject id #subjectId"
        def invalidRequest = GetEventPopulationReadiness.Request.newBuilder().withStudyId(studyId).withSubjectId(subjectId).build()

        when: "requesting whether the study with id #studyId is ready for  population for subject id #subjectId"
        getEventPopulationReadiness.getEventPopulationReadiness(invalidRequest)

        then: "an AccessDeniedException is thrown"
        thrown(AccessDeniedException)

        where:
        studyId                    | subjectId
        StudyId.of("study-id-098") | SubjectId.of("subject-id-098")
    }

    @Unroll
    def "Is study ready for event population with study id null"(StudyId studyId, SubjectId subjectId, String field) {
        given: "An invalid request with study id #studyId and subject id #subjectId"
        def invalidRequest = GetEventPopulationReadiness.Request.newBuilder().withStudyId(studyId).withSubjectId(subjectId).build()

        when: "requesting whether the study with id #studyId is ready for  population for subject id #subjectId"
        getEventPopulationReadiness.getEventPopulationReadiness(invalidRequest)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"

        where:
        studyId | subjectId                      | field
        null    | SubjectId.of("subject-id-098") | "studyId"
    }

    @Unroll
    def "Is study ready for event population with subject id null"(StudyId studyId, SubjectId subjectId, String field, String errorMessage) {
        given: "Study #studyId has events"
        createStudyWithEventForCurrentUserAndFor(studyId, USER_ID_KNOWN)

        and: "An invalid request with study id #studyId and subject id #subjectId"
        def invalidRequest = GetEventPopulationReadiness.Request.newBuilder().withStudyId(studyId).withSubjectId(subjectId).build()

        when: "requesting whether the study with id #studyId is ready for  population for subject id #subjectId"
        getEventPopulationReadiness.getEventPopulationReadiness(invalidRequest)

        then: "invalid request"
        def exception = thrown(UseCaseConstraintViolationException)
        exception.getConstraintViolations().size() == 1
        exception.getConstraintViolations()[0].field == field
        exception.getConstraintViolations()[0].message == errorMessage

        where:
        studyId                    | subjectId | field       | errorMessage
        StudyId.of("study-id-098") | null      | "subjectId" | "must not be null"
    }

    @Unroll
    def "Is study ready for event population with invalid request"(StudyId studyId, SubjectId subjectId, String field, String errorMessage) {
        given: "Study #studyId has events"
        createStudyWithEventFor(studyId, subjectId, USER_ID_KNOWN)

        and: "An invalid request with study id #studyId and subject id #subjectId"
        def invalidRequest = GetEventPopulationReadiness.Request.newBuilder().withStudyId(studyId).withSubjectId(subjectId).build()

        when: "requesting whether the study with id #studyId is ready for  population for subject id #subjectId"
        getEventPopulationReadiness.getEventPopulationReadiness(invalidRequest)

        then: "invalid request"
        def exception = thrown(UseCaseConstraintViolationException)
        exception.getConstraintViolations().size() == 1
        exception.getConstraintViolations()[0].field == field
        exception.getConstraintViolations()[0].message == errorMessage

        where:
        studyId                    | subjectId           | field          | errorMessage
        StudyId.of("study-id-098") | SubjectId.of(null)  | "subjectId.id" | "must not be blank"
        StudyId.of("study-id-098") | SubjectId.of("   ") | "subjectId.id" | "must not be blank"
    }

    private createStudyWithEventFor(StudyId studyId, SubjectId subjectId, UserIdentifier investigator) {
        def studySnapshot = aDefaultStudySnapshotBuilder(investigator)
                .withStudyId(studyId)
                .withSubjects(singletonList(aDefaultSubjectSnapshotBuilder().withSubjectId(subjectId).build()))
                .build()

        studyRepository.save(Study.restoreSnapshot(studySnapshot))
    }

    private createStudyWithSubjectEHRRegisteredAndEventFor(StudyId studyId, SubjectId subjectId, UserIdentifier investigator) {
        def subjectEHRRegistered = aDefaultSubjectSnapshotBuilder().withSubjectId(subjectId).withEHRRegistrationStatus(REGISTERED).build()
        def studySnapshot = aDefaultStudySnapshotBuilder(investigator)
                .withStudyId(studyId)
                .withSubjects(singletonList(subjectEHRRegistered))
                .build()

        studyRepository.save(Study.restoreSnapshot(studySnapshot))
    }

    private createStudyWithSubjectEHRNotRegisteredAndEventFor(StudyId studyId, SubjectId subjectId, UserIdentifier investigator) {
        def subjectEHRRegistered = aDefaultSubjectSnapshotBuilder().withSubjectId(subjectId).withEHRRegistrationStatus(NOT_REGISTERED).build()
        def studySnapshot = aDefaultStudySnapshotBuilder(investigator)
                .withStudyId(studyId)
                .withSubjects(singletonList(subjectEHRRegistered))
                .build()

        studyRepository.save(Study.restoreSnapshot(studySnapshot))
    }

    private createStudyWithEventForCurrentUserAndFor(StudyId studyId, UserIdentifier investigator) {
        def studySnapshot = aDefaultStudySnapshotBuilder(investigator)
                .withStudyId(studyId)
                .withInvestigators(singletonList(aDefaultInvestigatorSnapshotBuilder().withUserId(UserIdentifier.of(investigator.id)).build()))
                .build();
        studyRepository.save(Study.restoreSnapshot(studySnapshot))
    }

    private createStudyWithSubjectEHRRegisteredAndWithoutEventFor(StudyId studyId, SubjectId subjectId, UserIdentifier investigator) {
        def subjectEHRRegistered = aDefaultSubjectSnapshotBuilder().withSubjectId(subjectId).withEHRRegistrationStatus(REGISTERED).build()
        def studySnapshot = aDefaultStudySnapshotBuilder(investigator)
                .withStudyId(studyId)
                .withSubjects(singletonList(subjectEHRRegistered))
                .withMetadata(aDefaultMetaDataDefinitionSnapshotBuilder().withEventDefinitions(Collections.emptyList()).build())
                .withInvestigators(singletonList(aDefaultInvestigatorSnapshotBuilder().withUserId(UserIdentifier.of(investigator.id)).build()))
                .build()

        studyRepository.save(Study.restoreSnapshot(studySnapshot))
    }
}