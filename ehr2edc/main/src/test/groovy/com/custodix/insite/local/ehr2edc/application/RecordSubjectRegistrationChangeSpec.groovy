package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.RegistrationRecord
import com.custodix.insite.local.ehr2edc.command.RecordSubjectRegistrationChange
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.vocabulary.DeregisterReason
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Title("Record changes in the subject registration")
class RecordSubjectRegistrationChangeSpec extends AbstractSpecification {

    @Autowired
    RecordSubjectRegistrationChange recordSubjectRegistrationChange

    static final PatientCDWReference patientId = PatientCDWReference.newBuilder().withSource("HIS_001").withId("PID_001").build()
    static final StudyId studyId = StudyId.of("SID_001")
    static final SubjectId subjectId = SubjectId.of("SUBJ_042")
    static final LocalDate date = LocalDate.of(2050, 12, 31)
    static final DeregisterReason reason = DeregisterReason.CONSENT_RETRACTED

    def cleanup() {
        registrationRecordRepository.reset()
    }

    @Unroll
    def "Recording a registration with an invalid request"(PatientCDWReference patientId, StudyId studyId, SubjectId subjectId,
                                                           LocalDate date, String field, String message) {
        given: "The invalid registration request"
        def request = RecordSubjectRegistrationChange.RegistrationRequest.newBuilder()
                .withPatientId(patientId)
                .withStudyId(studyId)
                .withSubjectId(subjectId)
                .withDate(date)
                .build()
        when: "The change is recorded"
        recordSubjectRegistrationChange.register(request)
        then: "A constraint violation is thrown"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field.equals(field)
        ex.constraintViolations.first().message == message
        where:
        patientId      | studyId      | subjectId      | date      | field       | message
        null           | this.studyId | this.subjectId | this.date | "patientId" | "must not be null"
        this.patientId | null         | this.subjectId | this.date | "studyId"   | "must not be null"
        this.patientId | this.studyId | null           | this.date | "subjectId" | "must not be null"
        this.patientId | this.studyId | this.subjectId | null      | "date"      | "must not be null"
    }

    def "Recording a registration with a valid request"() {
        given: "The valid registration request"
        def request = RecordSubjectRegistrationChange.RegistrationRequest.newBuilder()
                .withPatientId(patientId)
                .withStudyId(studyId)
                .withSubjectId(subjectId)
                .withDate(date)
                .build()
        when: "The change is recorded"
        recordSubjectRegistrationChange.register(request)
        then: "A registration record is stored"
        def registrationRecord = registrationRecordRepository.lastSaved
        registrationRecord.patientId == request.patientId
        registrationRecord.studyId == request.studyId
        registrationRecord.subjectId == request.subjectId
        registrationRecord.date == DateTimeFormatter.ISO_LOCAL_DATE.format(request.date)
        registrationRecord.reason == null
        registrationRecord.type == RegistrationRecord.Type.REGISTRATION
    }

    @Unroll
    def "Recording a deregistration with an invalid request"(PatientCDWReference patientId, StudyId studyId, SubjectId subjectId,
                                                             LocalDate date, DeregisterReason reason, String field, String message) {
        given: "The invalid deregistration request"
        def request = RecordSubjectRegistrationChange.DeregistrationRequest.newBuilder()
                .withPatientId(patientId)
                .withStudyId(studyId)
                .withSubjectId(subjectId)
                .withDate(date)
                .withReason(reason)
                .build()
        when: "The change is recorded"
        recordSubjectRegistrationChange.deregister(request)
        then: "A constraint violation is thrown"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field.equals(field)
        ex.constraintViolations.first().message == message
        where:
        patientId      | studyId      | subjectId      | date      | reason      | field       | message
        null           | this.studyId | this.subjectId | this.date | this.reason | "patientId" | "must not be null"
        this.patientId | null         | this.subjectId | this.date | this.reason | "studyId"   | "must not be null"
        this.patientId | this.studyId | null           | this.date | this.reason | "subjectId" | "must not be null"
        this.patientId | this.studyId | this.subjectId | null      | this.reason | "date"      | "must not be null"
        this.patientId | this.studyId | this.subjectId | this.date | null        | "reason"    | "must not be null"
    }

    def "Recording a deregistration with a valid request"() {
        given: "The valid deregistration request"
        def request = RecordSubjectRegistrationChange.DeregistrationRequest.newBuilder()
                .withPatientId(patientId)
                .withStudyId(studyId)
                .withSubjectId(subjectId)
                .withDate(date)
                .withReason(reason)
                .build()
        when: "The change is recorded"
        recordSubjectRegistrationChange.deregister(request)
        then: "A registration record is stored"
        def registrationRecord = registrationRecordRepository.lastSaved
        registrationRecord.patientId == request.patientId
        registrationRecord.studyId == request.studyId
        registrationRecord.subjectId == request.subjectId
        registrationRecord.date == DateTimeFormatter.ISO_LOCAL_DATE.format(request.date)
        registrationRecord.reason == request.reason
        registrationRecord.type == RegistrationRecord.Type.DEREGISTRATION
    }

    def "Recording a registration should succeed for an unauthenticated user"() {
        given: "The valid registration request"
        def request = RecordSubjectRegistrationChange.RegistrationRequest.newBuilder()
                .withPatientId(patientId)
                .withStudyId(studyId)
                .withSubjectId(subjectId)
                .withDate(date)
                .build()
        and: "The user is not authenticated"
        withoutAuthenticatedUser()

        when: "The change is recorded"
        recordSubjectRegistrationChange.register(request)

        then: "The request should succeed"
        noExceptionThrown()
    }

    def "Recording a deregistration should succeed for an unauthenticated user"() {
        given: "The valid deregistration request"
        def request = RecordSubjectRegistrationChange.DeregistrationRequest.newBuilder()
                .withPatientId(patientId)
                .withStudyId(studyId)
                .withSubjectId(subjectId)
                .withDate(date)
                .withReason(reason)
                .build()
        and: "The user is not authenticated"
        withoutAuthenticatedUser()

        when: "The change is recorded"
        recordSubjectRegistrationChange.deregister(request)

        then: "The request should succeed"
        noExceptionThrown()
    }

}