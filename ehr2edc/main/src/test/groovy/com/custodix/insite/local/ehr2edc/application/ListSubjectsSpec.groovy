package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.query.ListSubjects
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title

import static com.custodix.insite.local.ehr2edc.query.ListSubjects.Request
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId

@Title("List Subjects in Study")
class ListSubjectsSpec extends AbstractSpecification {

    @Autowired
    ListSubjects listSubjects

    def "List Subjects in Study with an empty Request"() {
        given: "An empty request"
        Request request = Request.newBuilder().build()

        when: "Listing the subjects in the Study"
        listSubjects.list(request)

        then: "Access is denied"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
    }

    def "List Subjects in Study without a StudyId"() {
        given: "A request without a StudyId"
        Request request = Request.newBuilder()
                .build()

        when: "Listing the subjects in the Study"
        listSubjects.list(request)

        then: "Access is denied"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
    }

    def "List Subjects in Study without a known StudyId"() {
        given: "A request without a StudyId"
        StudyId studyId = aRandomStudyId()
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .build()

        when: "Listing the subjects in the Study"
        listSubjects.list(request)

        then: "Indicate the user has no permission to perform the request"
        AccessDeniedException ex = thrown()
        ex.message == "User is not an assigned Investigator"
    }

    def "List Subjects in Study with a known StudyId without registered Subjects"() {
        given: "A known study without subjects"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        and: "A request for the given study"
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .build()

        when: "Listing the subjects in the Study"
        List<SubjectId> subjects = listSubjects.list(request).subjects

        then: "An empty list of items is returned"
        subjects.isEmpty()

    }

    def "List Subjects in Study with a known StudyId with a registered Subject"() {
        given: "A known study with one registered subject with id '#edcSubjectReference'"
        SubjectSnapshot subject = SubjectSnapshot.newBuilder()
                .withSubjectId(subjectId)
                .withDateOfConsent(DATE_NOW)
                .withPatientCDWReference(PatientCDWReference.newBuilder().withSource("source").withId("id").build())
                .build()
        def knownStudy = generateKnownStudy(StudyId.of("id"), "Name", "Description", subject, USER_ID_KNOWN)
        and: "A request for subjects in the study"
        Request request = Request.newBuilder()
                .withStudyId(knownStudy.studyId)
                .build()

        when: "Listing the subjects in the Study"
        List<SubjectId> subjects = listSubjects.list(request).subjects

        then: "A list containing only the registered subject with id '#edcSubjectReference' is returned"
        subjects.size() == 1
        subjects.first() == subjectId

        where:
        subjectId = SubjectId.of("SUBJECT")
    }

    def "List Subjects in Study should fail as user who is not assigned as investigator"() {
        given: "A known study"
        StudyId studyId = generateKnownStudyId(USER_ID_OTHER)
        and: "A request for the given study"
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .build()

        when: "Listing the subjects in the Study"
        listSubjects.list(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"
    }

    def "List Subjects in Study should fail as unauthenticated user"() {
        given: "A known study"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        and: "A request for the given study"
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .build()
        and: "No user is authenticated"
        withoutAuthenticatedUser()

        when: "Listing the subjects in the Study"
        listSubjects.list(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"
    }
}
