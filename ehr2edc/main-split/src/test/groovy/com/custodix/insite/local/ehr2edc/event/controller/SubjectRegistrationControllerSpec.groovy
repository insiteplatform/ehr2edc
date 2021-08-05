package com.custodix.insite.local.ehr2edc.event.controller

import com.custodix.insite.local.ehr2edc.EventPublisher
import com.custodix.insite.local.ehr2edc.events.SubjectDeregisteredEvent
import com.custodix.insite.local.ehr2edc.events.SubjectRegisteredEvent
import com.custodix.insite.local.ehr2edc.vocabulary.DeregisterReason
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.verify

class SubjectRegistrationControllerSpec extends EHR2EDCSyncEventControllerSpec {

    public static final String PATIENT_ID = "patient"
    public static final String STUDY_ID = "study"
    public static final String NAMESPACE = "namespace"
    public static final String SUBJECT_ID = "subject"
    public static final LocalDate DATE = LocalDate.of(2050, 12, 31)
    public static final DeregisterReason REASON = DeregisterReason.CONSENT_RETRACTED

    @Autowired
    private EventPublisher eventPublisher

    def "The 'subject registered'-event triggers the storage of a registration record"() {
        given: 'A SubjectRegisteredEvent'
        SubjectRegisteredEvent event = SubjectRegisteredEvent.newBuilder()
                .withPatientId(PATIENT_ID)
                .withNamespace(NAMESPACE)
                .withStudyId(STUDY_ID)
                .withSubjectId(SUBJECT_ID)
                .withDate(DATE)
                .build()

        when: "The event is published"
        eventPublisher.publishEvent(event)

        then: "The migration of patient data is triggered"
        verify(recordSubjectRegistrationChange).register(argThat({
            request ->
                request.patientId.id == PATIENT_ID &&
                        request.patientId.source == NAMESPACE &&
                        request.studyId.id == STUDY_ID &&
                        request.subjectId.id == SUBJECT_ID &&
                        request.date == DATE
        }))
    }

    def "The 'subject deregistered'-event triggers the storage of a registration record"() {
        given: 'A SubjectRegisteredEvent'
        SubjectDeregisteredEvent event = SubjectDeregisteredEvent.newBuilder()
                .withPatientCDWReference(PatientCDWReference.newBuilder().withId(PATIENT_ID).withSource(NAMESPACE).build())
                .withStudyId(StudyId.of(STUDY_ID))
                .withSubjectId(SubjectId.of(SUBJECT_ID))
                .withDate(DATE)
                .withReason(REASON)
                .build()

        when: "The event is published"
        eventPublisher.publishEvent(event)

        then: "The migration of patient data is triggered"
        verify(recordSubjectRegistrationChange).deregister(argThat({
            request ->
                request.patientId.id == PATIENT_ID &&
                        request.patientId.source == NAMESPACE &&
                        request.studyId.id == STUDY_ID &&
                        request.subjectId.id == SUBJECT_ID &&
                        request.date == DATE &&
                        request.reason == REASON
        }))
    }
}