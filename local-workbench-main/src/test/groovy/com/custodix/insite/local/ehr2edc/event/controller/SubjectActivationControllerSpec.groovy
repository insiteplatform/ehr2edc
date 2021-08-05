package com.custodix.insite.local.ehr2edc.event.controller

import com.custodix.insite.local.ehr2edc.EventPublisher
import com.custodix.insite.local.ehr2edc.events.SubjectRegisteredEvent
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.timeout
import static org.mockito.Mockito.verify

class SubjectActivationControllerSpec extends EHR2EDCSyncEventControllerSpec {

    private static final String PATIENT_ID = "patient"
    private static final String STUDY_ID = "study"
    public static final String NAMESPACE = "namespace"
    private static final String SUBJECT_ID = "subject"
    public static final LocalDate DATE = LocalDate.of(2050, 12, 31)

    @Autowired
    private EventPublisher eventPublisher

    def "The 'subject registered'-event triggers subject activation"() {
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

        then: "The subject activation is triggered"
        verify(activateSubject, timeout(5000)).activate(argThat({
            request ->
                request.patientIdentifier.patientId.id == PATIENT_ID &&
                        request.patientIdentifier.namespace.name == NAMESPACE &&
                        request.patientIdentifier.subjectId.id == SUBJECT_ID
        }))
    }
}
