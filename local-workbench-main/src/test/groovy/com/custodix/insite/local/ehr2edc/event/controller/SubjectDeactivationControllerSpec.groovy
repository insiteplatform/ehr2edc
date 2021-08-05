package com.custodix.insite.local.ehr2edc.event.controller

import com.custodix.insite.local.ehr2edc.EventPublisher
import com.custodix.insite.local.ehr2edc.events.SubjectDeregisteredEvent
import com.custodix.insite.local.ehr2edc.vocabulary.DeregisterReason
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.timeout
import static org.mockito.Mockito.verify

class SubjectDeactivationControllerSpec extends EHR2EDCSyncEventControllerSpec {

    private static final String PATIENT_ID = "patient"
    private static final String STUDY_ID = "study"
    public static final String NAMESPACE = "namespace"
    private static final String SUBJECT_ID = "subject"
    public static final LocalDate DATE = LocalDate.of(2050, 12, 31)

    @Autowired
    private EventPublisher eventPublisher

    def "The 'subject deregistered'-event triggers subject deactivation"() {
        given: 'A SubjectDeregisteredEvent'
        SubjectDeregisteredEvent event = SubjectDeregisteredEvent.newBuilder()
                .withPatientCDWReference(PatientCDWReference.newBuilder().withId(PATIENT_ID).withSource(NAMESPACE).build())
                .withStudyId(StudyId.of(STUDY_ID))
                .withSubjectId(SubjectId.of(SUBJECT_ID))
                .withDate(DATE)
                .withReason(DeregisterReason.CONSENT_RETRACTED)
                .build()

        when: "The event is published"
        eventPublisher.publishEvent(event)

        then: "The subject deactivation is triggered"
        verify(deactivateSubject , timeout(5000)).deactivate(argThat({
            request ->
                request.subjectId.id == SUBJECT_ID
        }))
    }
}
