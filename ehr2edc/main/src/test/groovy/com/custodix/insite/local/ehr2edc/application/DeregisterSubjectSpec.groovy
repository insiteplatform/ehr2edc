package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.Subject
import com.custodix.insite.local.ehr2edc.command.DeregisterSubject
import com.custodix.insite.local.ehr2edc.events.SubjectDeregisteredEvent
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title
import spock.lang.Unroll

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import static com.custodix.insite.local.ehr2edc.command.DeregisterSubject.Request
import static com.custodix.insite.local.ehr2edc.vocabulary.DeregisterReason.CONSENT_RETRACTED
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId

@Title("Deregister Subject from Study")
class DeregisterSubjectSpec extends AbstractSpecification {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)

    @Autowired
    DeregisterSubject deregisterSubject

    @Unroll
    def "I can deregister a subject with an end date '#endDate' that is on or after the subject's consent date '#consentDate'"() {
        given: "A known study with a registered subject with consent date '#consentDate'"
        def studyId = aRandomStudyId()
        def subject = generateKnownSubject(studyId, aRandomSubjectId(), consentDate, USER_ID_KNOWN)
        and: "A deregister request for the subject with end date '#endDate'"
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withSubjectId(subject.subjectId)
                .withEndDate(endDate)
                .build()

        when: "I deregister the subject"
        deregisterSubject.deregister(request)

        then: "The correct subject is modified with corresponding end date '#endDate' and reason 'CONSENT RETRACTED'"
        Subject modified = studyRepository.getStudyById(studyId).subjects.items.first()
        with(modified) {
            it.deregisterReason == CONSENT_RETRACTED
            it.dateOfConsent.isEqual(dateOfConsent)
            it.dateOfConsentWithdrawn.isEqual(endDate)
            it.patientCDWReference.id == subject.patientCDWReference.id
            it.patientCDWReference.source == subject.patientCDWReference.source
            it.subjectId.id == subject.subjectId.id
        }
        and: "A SubjectDeregisteredEvent is published"
        SubjectDeregisteredEvent event = (SubjectDeregisteredEvent) testEventPublisher.poll()
        with(event) {
            it.date == endDate
            it.subjectId == subject.subjectId
            it.patientCDWReference == subject.patientCDWReference
            it.reason == CONSENT_RETRACTED
        }

        where:
        consentDate                   | endDate
        DATE_YESTERDAY                | DATE_NOW
        LocalDate.parse("2019-10-04") | LocalDate.parse("2019-10-04")
        LocalDate.parse("2019-10-04") | LocalDate.parse("2019-10-04")
    }

    @Unroll
    def "I can deregister a subject with an explicit data capture stop reason '#reason"() {
        given: "A known study with a registered subject with consent date '#consentDate'"
        def studyId = aRandomStudyId()
        def subjectId = aRandomSubjectId()
        def subject = generateKnownSubject(studyId, subjectId, consentDate, USER_ID_KNOWN)
        and: "A deregister request for the subject with end date '#endDate' and reason '#reason'"
        Request request = Request.newBuilder()
                .withSubjectId(subject.subjectId)
                .withStudyId(studyId)
                .withEndDate(endDate)
                .withDataCaptureStopReason(reason)
                .build()

        when: "I deregister the subject"
        deregisterSubject.deregister(request)

        then: "The correct subject is modified with corresponding end date '#endDate' and reason '#reason'"
        Subject modified = studyRepository.getStudyById(studyId).subjects.items.first()
        with(modified) {
            it.deregisterReason == reason
            it.dateOfConsent.isEqual(subject.dateOfConsent)
            it.dateOfConsentWithdrawn.isEqual(endDate)
            it.patientCDWReference.id == subject.patientCDWReference.id
            it.patientCDWReference.source == subject.patientCDWReference.source
            it.subjectId.id == subject.subjectId.id
        }
        and: "A SubjectDeregisteredEvent is published"
        SubjectDeregisteredEvent deregisteredSubjectEvent = (SubjectDeregisteredEvent) testEventPublisher.poll()
        with(deregisteredSubjectEvent) {
            it.date == endDate
            it.subjectId == subject.subjectId
            it.patientCDWReference == subject.patientCDWReference
            it.reason == reason
        }

        where:
        consentDate = DATE_YESTERDAY
        endDate = DATE_NOW
        reason = CONSENT_RETRACTED
    }

    def "I cannot deregister a subject with an empty request"() {
        given: "An empty Deregister request"
        Request request = Request.newBuilder().build()

        when: "I deregister the subject"
        deregisterSubject.deregister(request)

        then: "Access is denied"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
    }

    def "I cannot deregister a subject without a SubjectId"() {
        given: "A Deregister request without a SubjectId"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withEndDate(DATE_TOMORROW)
                .build()

        when: "I deregister the subject"
        deregisterSubject.deregister(request)

        then: "Indicate the request was invalid"
        thrown UseCaseConstraintViolationException
    }

    def "I cannot deregister a subject without a Date of Consent Withdrawn"() {
        given: "A Deregister request without a Date of Consent Withdrawn"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        SubjectSnapshot subject = generateKnownSubject(studyId)
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .build()

        when: "I deregister the subject"
        deregisterSubject.deregister(request)

        then: "Indicate the request was invalid"
        thrown UseCaseConstraintViolationException
    }

    def "I cannot deregister a subject without a StudyId"() {
        given: "A Deregister request without a StudyId"
        Request request = Request.newBuilder()
                .withSubjectId(aRandomSubjectId())
                .withEndDate(DATE_TOMORROW)
                .build()

        when: "I deregister the subject"
        deregisterSubject.deregister(request)

        then: "Access is denied"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
    }

    def "I cannot deregister a subject with a SubjectId for an unknown Study"() {
        given: "A Deregister request with a SubjectId for an unknown Study"
        Request request = Request.newBuilder()
                .withStudyId(aRandomStudyId())
                .withSubjectId(aRandomSubjectId())
                .withEndDate(DATE_TOMORROW)
                .build()

        when: "I deregister the subject"
        deregisterSubject.deregister(request)

        then: "Indicate the user has no permission to perform the request"
        thrown AccessDeniedException
    }

    def "I cannot deregister a subject with an unknown SubjectId for a known Study"() {
        given: "A Deregister request with an unknown SubjectId for a known Study"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        SubjectId subjectId = aRandomSubjectId()
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withSubjectId(subjectId)
                .withEndDate(DATE_TOMORROW)
                .build()

        when: "I deregister the subject"
        deregisterSubject.deregister(request)

        then: "Indicate the Subject is not known for the study"
        UserException ex = thrown()
        ex.message == "Unknown subject id '" + subjectId.id + "'."
    }

    @Unroll
    def "I cannot deregister a subject with an end date '#endDate' which is before the consent date '#consentDate'"(LocalDate consentDate, LocalDate endDate) {
        given: "A registered subject with consent date '#consentDate'"
        def studyId = generateKnownStudyId(USER_ID_KNOWN)
        def subject = generateKnownSubject(studyId, aRandomSubjectId(), consentDate, USER_ID_KNOWN)
        and: "A deregister request with end date '#endDate'"
        Request request = Request.newBuilder()
                .withSubjectId(subject.subjectId)
                .withStudyId(studyId)
                .withEndDate(endDate)
                .build()

        when: "I deregister the subject"
        deregisterSubject.deregister(request)

        then: "Indicate the request was invalid"
        UserException ex = thrown()
        ex.message == String.format("Date of deregistration must be on or after the date of consent (%s)", consentDate.format(DATE_FORMAT))

        where:
        consentDate | endDate
        DATE_NOW    | DATE_YESTERDAY
    }

    @Unroll
    def "I cannot deregister a subject with an end date '#endDate' which is in the future"(LocalDate consentDate, LocalDate endDate) {
        given: "A deregister request with end date '#endDate' which is in the future"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        def subject = generateKnownSubject(studyId, aRandomSubjectId(), consentDate, USER_ID_KNOWN)
        Request request = Request.newBuilder()
                .withSubjectId(subject.subjectId)
                .withStudyId(studyId)
                .withEndDate(endDate)
                .build()

        when: "I deregister the subject"
        deregisterSubject.deregister(request)

        then: "Indicate the request was invalid"
        UserException ex = thrown()
        ex.message == "Date of deregistration must not be in the future"

        where:
        consentDate | endDate
        DATE_NOW    | DATE_TOMORROW
    }

    def "I cannot deregister a subject as an unauthenticated user"() {
        given: "A valid request"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        SubjectSnapshot subject = generateKnownSubject(studyId)
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withEndDate(DATE_TOMORROW)
                .withDataCaptureStopReason(CONSENT_RETRACTED)
                .build()
        and: "An unknown user"
        withoutAuthenticatedUser()

        when: "I deregister the subject"
        deregisterSubject.deregister(request)

        then: "An access denied exception is thrown"
        thrown(AccessDeniedException)
    }

    def "I cannot deregister a subject as a user who is not an assigned investigator"() {
        given: "A valid request"
        StudyId studyId = generateKnownStudyId(USER_ID_OTHER)
        SubjectSnapshot subject = generateKnownSubject(studyId)
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withEndDate(DATE_TOMORROW)
                .withDataCaptureStopReason(CONSENT_RETRACTED)
                .build()

        when: "I deregister the subject"
        deregisterSubject.deregister(request)

        then: "An access denied exception is thrown"
        thrown(AccessDeniedException)
    }
}
