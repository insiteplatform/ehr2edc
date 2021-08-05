package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.query.ListRegisteredStudies
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title

import static com.custodix.insite.local.ehr2edc.query.ListRegisteredStudies.Request
import static com.custodix.insite.local.ehr2edc.vocabulary.PatientIdObjectMother.aRandomPatientId

@Title("List Registered Studies")
class ListRegisteredStudiesSpec extends AbstractSpecification {

    @Autowired
    ListRegisteredStudies listRegistredStudies

    def "List Registered Studies with an empty Request"() {
        given: "An empty request"
        Request request = Request.newBuilder().build()

        when: "Listing registered studies"
        listRegistredStudies.registeredStudies(request)

        then: "Indicate a Patient Id is required"
        thrown UseCaseConstraintViolationException
    }

    def "List Registered Studies with an unauthenticated user"() {
        given: "A request from an unknown User"
        withoutAuthenticatedUser()
        Request request = Request.newBuilder()
                .withPatientCDWReference(aRandomPatientId())
                .build()

        when: "Listing registered studies"
        List<Study> studies = listRegistredStudies.registeredStudies(request).registeredStudies

        then: "Access is denied"
        thrown AccessDeniedException
    }

    def "List Registered Studies for a Patient Id without Registered Studies"() {
        given: "An empty request"
        Request request = Request.newBuilder()
                .withPatientCDWReference(aRandomPatientId())
                .build()

        when: "Listing registered studies"
        List<Study> registeredStudies = listRegistredStudies.registeredStudies(request).registeredStudies

        then: "Return an empty list of studies"
        registeredStudies.isEmpty()
    }

    def "List Registered Studies as a user who is no assigned investigator"() {
        given: "Request with a PatientId for a Subject known to be Registered to the Study"
        StudySnapshot study = generateKnownStudy(USER_ID_OTHER)
        SubjectSnapshot subject = generateKnownSubject(study.studyId)
        Request request = Request.newBuilder()
                .withPatientCDWReference(subject.patientCDWReference)
                .build()

        when: "Listing registered studies"
        List<Study> registeredStudies = listRegistredStudies.registeredStudies(request).registeredStudies

        then: "Return an empty list of studies"
        registeredStudies.isEmpty()
    }

    def "List Registered Studies as a non authenticated user"() {
        given: "Request with a PatientId for a Subject known to be Registered to the Study"
        StudySnapshot study = generateKnownStudy(USER_ID_OTHER)
        SubjectSnapshot subject = generateKnownSubject(study.studyId)
        Request request = Request.newBuilder()
                .withPatientCDWReference(subject.patientCDWReference)
                .build()
        and: "No user is authenticated"
        withoutAuthenticatedUser()

        when: "Listing registered studies"
        List<Study> registeredStudies = listRegistredStudies.registeredStudies(request).registeredStudies
        
        then: "Access is denied"
        thrown(AccessDeniedException)
    }

    def "List Registered Studies for a Patient Id with Registered Studies"() {
        given: "Request with a PatientId for a Subject known to be Registered to the Study"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        SubjectSnapshot subject = generateKnownSubject(study.studyId)
        Request request = Request.newBuilder()
                .withPatientCDWReference(subject.patientCDWReference)
                .build()

        when: "Listing registered studies"
        List<Study> registeredStudies = listRegistredStudies.registeredStudies(request).registeredStudies

        then: "Return the list of registered studies with the registered study included"
        registeredStudies.size() == 1
        ListRegisteredStudies.Study registeredStudy = registeredStudies.get(0)
        study.studyId == registeredStudy.studyId
        study.name == registeredStudy.name
        study.description == registeredStudy.description
        registeredStudy.subject.edcSubjectReference.id == subject.edcSubjectReference.id
        registeredStudy.subject.patientCDWReference == subject.patientCDWReference
        registeredStudy.subject.dateOfConsent.isEqual(subject.dateOfConsent)
    }

    def "List Registered Studies for a subject with a PatientCDWReference that has the same id but a different source as an existing subject registered to the study"() {
        given: "A study with a subject having a certain PatientCDWReference"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        SubjectSnapshot subject = generateKnownSubject(study.studyId)

        when: "Requesting registered studies for the PatientCDWReference with a different source but same id"
        def reference = PatientCDWReference.newBuilder(subject.patientCDWReference).withSource("ANOTHER_SOURCE").build()
        Request request = Request.newBuilder()
                .withPatientCDWReference(reference)
                .build()
        def registeredStudies = listRegistredStudies.registeredStudies(request).registeredStudies

        then: "The registered studies should be empty"
        registeredStudies.isEmpty()
    }

    def "List Registered Studies for a subject that has been de-registered from a study before"() {
        given: "A study with a subject having a certain PatientCDWReference"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        SubjectSnapshot subject = generateKnownSubject(study.studyId)
        and: "The subject is de-registered from the study"
        addDeregisteredSubjectToStudy(study, subject.patientCDWReference, subject.edcSubjectReference, subject.subjectId)

        when: "Requesting registered studies"
        def reference = PatientCDWReference.newBuilder(subject.patientCDWReference).build()
        Request request = Request.newBuilder()
                .withPatientCDWReference(reference)
                .build()
        def registeredStudies = listRegistredStudies.registeredStudies(request).registeredStudies

        then: "The registered studies should be empty"
        registeredStudies.isEmpty()
    }
}