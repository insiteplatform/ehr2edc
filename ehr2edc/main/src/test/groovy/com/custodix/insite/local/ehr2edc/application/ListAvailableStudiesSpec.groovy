package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.query.ListAvailableStudies
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title

import static com.custodix.insite.local.ehr2edc.query.ListAvailableStudies.Request
import static com.custodix.insite.local.ehr2edc.vocabulary.PatientIdObjectMother.aRandomPatientId

@Title("List Available Studies")
class ListAvailableStudiesSpec extends AbstractSpecification {

    @Autowired
    ListAvailableStudies listAvailableStudies

    def "List Available Studies without PatientId"() {
        given: "A request without a PatientId"
        Request request = Request.newBuilder()
                .build()

        when: "Listing available studies"
        listAvailableStudies.availableStudies(request)

        then: "Indicate a Patient Id is required"
        thrown UseCaseConstraintViolationException
    }

    def "List Available Studies with an unauthenticated user"() {
        given: "A request from an unknown User"
        withoutAuthenticatedUser()
        Request request = Request.newBuilder()
                .withPatientCDWReference(aRandomPatientId())
                .build()

        when: "Listing available studies"
        List<Study> studies = listAvailableStudies.availableStudies(request).availableStudies

        then: "Access is denied"
        thrown AccessDeniedException
    }

    def "List Available studies for a Patient Id with an unregistered StudyId"() {
        given: "A Request with Patient Id"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        PatientCDWReference patientId = aRandomPatientId()
        Request request = Request.newBuilder()
                .withPatientCDWReference(patientId)
                .build()

        when: "Listing available studies"
        List<Study> studies = listAvailableStudies.availableStudies(request).availableStudies

        then: "Return the given unregistered Study"
        ListAvailableStudies.Study availableStudy = studies.find { it.studyId == study.studyId }
        availableStudy.studyId == study.studyId
        availableStudy.name == study.name
        availableStudy.description == study.description
    }

    def "List Available Studies for a subject with a PatientCDWReference that has the same id but another source as an existing subject registered to the study"() {
        given: "A study with a subject having a certain PatientCDWReference"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        SubjectSnapshot subject = generateKnownSubject(studyId)

        when: "Requesting available studies for the PatientCDWReference with a different source but same id"
        Request request = Request.newBuilder()
                .withPatientCDWReference(PatientCDWReference.newBuilder(subject.patientCDWReference).withSource("ANOTHER_SOURCE").build())
                .build()
        List<Study> studies = listAvailableStudies.availableStudies(request).availableStudies

        then: "the study should be returned"
        ListAvailableStudies.Study availableStudy = studies.find { it.studyId == studyId }
        availableStudy.studyId == studyId
    }

    def "List Available studies for a Patient Id with a registered StudyId"() {
        given: "A Request with Patient Id"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        SubjectSnapshot subject = generateKnownSubject(studyId)
        Request request = Request.newBuilder()
                .withPatientCDWReference(subject.patientCDWReference)
                .build()

        when: "Listing available studies"
        List<Study> studies = listAvailableStudies.availableStudies(request).availableStudies

        then: "Make sure the given registered Study is not returned"
        assert (studies.stream().noneMatch { s -> s.studyId == studyId })
    }

    def "List Available studies after a subject has de-registered before"() {
        given: "A Request with Patient Id"
        def studySnapshot = generateKnownStudy(USER_ID_KNOWN)
        def subject = generateKnownSubject(studySnapshot.studyId)
        and: "The subject is de-registered from the study"
        addDeregisteredSubjectToStudy(studySnapshot, subject.patientCDWReference, subject.edcSubjectReference, subject.subjectId)

        when: "Listing available studies"
        Request request = Request.newBuilder()
                .withPatientCDWReference(subject.patientCDWReference)
                .build()
        def studies = listAvailableStudies.availableStudies(request).availableStudies

        then: "The study where the subject was de-registered from is present in the list"
        assert (studies.stream().anyMatch { s -> s.studyId == studySnapshot.studyId })
    }

    def "List Available studies as a user who is not assigned investigator"() {
        given: "A Request with Patient Id"
        StudySnapshot study = generateKnownStudy(USER_ID_OTHER)
        PatientCDWReference patientId = aRandomPatientId()
        Request request = Request.newBuilder()
                .withPatientCDWReference(patientId)
                .build()

        when: "Listing available studies"
        List<Study> studies = listAvailableStudies.availableStudies(request).availableStudies

        then: "Return the given unregistered Study"
        studies.isEmpty()
    }
}