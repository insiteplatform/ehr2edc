package com.custodix.insite.local.ehr2edc.application


import com.custodix.insite.local.ehr2edc.query.GetSubject
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.*
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title
import spock.lang.Unroll

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.populator.PopulatedEventObjectMother.STUDY_ID
import static com.custodix.insite.local.ehr2edc.query.GetSubject.Request
import static com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshotObjectMother.aDefaultSubjectSnapshot
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId

@Title("Get subject in a study")
class GetSubjectSpec extends AbstractSpecification {

    @Autowired
    GetSubject getSubject

    def "Get a subject from study with an empty Request"() {
        given: "An empty request"
        Request request = Request.newBuilder().build()

        when: "Getting subject in study"
        getSubject.getSubject(request)

        then: "Access is denied"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
    }

    def "Get a subject from study with no subject specified"() {
        given: "A request with studyId but no edcSubjectReference"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .build()

        when: "Getting subject in study"
        getSubject.getSubject(request)

        then: "An error should be shown that the edcSubjectReference should not be null"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field.equals("subjectId")
        ex.constraintViolations.first().message == "must not be null"
    }

    def "Get a subject from study with no study specified"() {
        given: "A request with edcSubjectReference but no studyId"
        Request request = Request.newBuilder()
                .withSubjectId(aRandomSubjectId())
                .build()

        when: "Getting subject in study"
        getSubject.getSubject(request)

        then: "An error should be shown that the studyId should not be null"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
    }

    def "Get a subject from study with an unknown study"() {
        given: "A request with edcSubjectReference and an unknown studyId"
        StudyId studyId = aRandomStudyId()
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withSubjectId(aRandomSubjectId())
                .build()

        when: "Getting subject in study"
        getSubject.getSubject(request)

        then: "An error should be shown that user has no permission to perform the request"
        def ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
    }

    def "Get a subject from study with an unknown subject"() {
        given: "A known study in the repository with id `#studyId`, name `#name`, description `#description`"
        def knownStudy = generateKnownStudy(USER_ID_KNOWN)
        def subjectId = aRandomSubjectId()

        when: "The subject with id `#edcSubjectReference` is retrieved from the study with id `#studyId`"
        getSubject.getSubject(Request.newBuilder().withStudyId(knownStudy.studyId).withSubjectId(subjectId).build())

        then: "An error should be shown that the study does not exist"
        UserException ex = thrown()
        ex.message == "Unknown subject id '" + subjectId.id + "'."
    }

    @Unroll
    def "Get a subject from study"(StudyId studyId, PatientCDWReference patientId, SubjectId subjectId, LocalDate dateOfConsent, LocalDate dateOfConsentWithdrawn, DeregisterReason deregisterReason, EDCSubjectReference edcSubjectReference) {
        given: "A known study in the repository with id `#studyId.id` and registered subject with id `#edcSubjectReference.id"
        SubjectSnapshot subjectSnapshot = SubjectSnapshot.newBuilder()
                .withPatientCDWReference(patientId)
                .withSubjectId(subjectId)
                .withEdcSubjectReference(edcSubjectReference)
                .withDateOfConsent(dateOfConsent)
                .withDateOfConsentWithdrawn(dateOfConsentWithdrawn)
                .withDeregisterReason(deregisterReason)
                .build()
        generateKnownStudy(studyId, "Name of study " + studyId.id, "Description of study " + studyId.id, subjectSnapshot, USER_ID_KNOWN)
        def request = Request.newBuilder()
                .withStudyId(studyId)
                .withSubjectId(subjectId)
                .build()

        when: "The subject with id `#edcSubjectReference.id` is retrieved from the study with id `#studyId.id`"
        def result = getSubject.getSubject(request)

        then: "The known subject is retrieved with patient id `#patientId.id`, subject id `#edcSubjectReference.id`, date of consent id `#dateOfConsent`, date of consent id `#dateOfConsentWithdrawn` and stop reason `#stopReason`"
        result.subject.patientId == patientId
        result.subject.subjectId == subjectId
        result.subject.edcSubjectReference == edcSubjectReference
        result.subject.dateOfConsent.isEqual(dateOfConsent)
        (dateOfConsentWithdrawn == null && result.subject.dateOfConsentWithdrawn == null) || result.subject.dateOfConsentWithdrawn.isEqual(dateOfConsentWithdrawn)
        result.subject.dataCaptureStopReason == deregisterReason

        where:
        studyId               | patientId                                                                            | subjectId                    | dateOfConsent | dateOfConsentWithdrawn | deregisterReason                   | edcSubjectReference
        StudyId.of("SID_001") | PatientCDWReference.newBuilder().withSource("HIS_DEFAULT").withId("PID_031").build() | SubjectId.of("SUBJ_031_001") | DATE_NOW      | null                   | null                               | EDCSubjectReference.of("EDC-ref-1")
        StudyId.of("SID_002") | PatientCDWReference.newBuilder().withSource("HIS_DEFAULT").withId("PID_095").build() | SubjectId.of("SUBJ_095_002") | DATE_NOW      | DATE_NOW.plusYears(1)  | DeregisterReason.CONSENT_RETRACTED | EDCSubjectReference.of("EDC-ref-2")
    }

    @Unroll
    def "Get subject in a study with an invalid studyId"(def studyId) {
        given: "A request with an #typeOfError"
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withSubjectId(aRandomSubjectId())
                .build()
        when: "Retrieving a subject from the study"
        getSubject.getSubject(request)

        then: "Access is denied"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"

        where:
        studyId | _
        null | _
        StudyId.of(null) | _
    }

    @Unroll
    def "Get subject in a study with an invalid subjectId"(SubjectId subjectId, String typeOfError, String errorMessage) {
        given: "A request with an #typeOfError"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withSubjectId(subjectId)
                .build()
        when: "Retrieving a subject from the study"
        getSubject.getSubject(request)
        then: "An error for edcSubjectReference containing  #errorMessage should appear"
        UseCaseConstraintViolationException ex = thrown()
        ex.constraintViolations.stream().anyMatch {
            v -> v.field.startsWith("subjectId") && v.message == errorMessage
        }
        where:
        subjectId                                               | typeOfError        | errorMessage
        null                                                    | "null subject id"  | "must not be null"
        SubjectId.of(null)                                      | "blank subject id" | "must not be blank"
        SubjectId.of("  ")                                      | "blank subject id" | "must not be blank"
        SubjectId.of("")                                        | "size subject id"  | "size must be between 1 and 200"
        SubjectId.of(RandomStringUtils.randomAlphanumeric(201)) | "size subject id"  | "size must be between 1 and 200"
    }

    def "Get a subject from study for user not assigned as investigator should fail"() {
        given: "A known study in the repository to which the authenticated user is not assigned investigator"
        def subject = aDefaultSubjectSnapshot()
        generateKnownStudy(STUDY_ID, "Name of study SID_001", "Description of study SID_001", subject, USER_ID_OTHER)
        def request = Request.newBuilder()
                .withStudyId(STUDY_ID)
                .build()

        when: "The subject with id `#edcSubjectReference.id` is retrieved from the study with id `#studyId.id`"
        getSubject.getSubject(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"
    }

    def "Get a subject from study for an unauthenticated user should fail"() {
        given: "A known study in the repository with id `#studyId.id` and registered subject with id `#edcSubjectReference.id"
        def subject = aDefaultSubjectSnapshot()
        generateKnownStudy(STUDY_ID, "Name of study", "Description of study mystudy", subject, USER_ID_KNOWN)
        def request = Request.newBuilder()
                .withStudyId(STUDY_ID)
                .build()
        and: "No user is authenticated"
        withoutAuthenticatedUser()

        when: "The subject with id `#edcSubjectReference.id` is retrieved from the study with id `#studyId.id`"
        getSubject.getSubject(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"
    }
}
