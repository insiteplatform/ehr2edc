package com.custodix.insite.local.ehr2edc.ehr.fhir.event.controller

import com.custodix.insite.local.ehr2edc.EventPublisher
import com.custodix.insite.local.ehr2edc.ehr.fhir.command.FhirSubjectRegistration
import com.custodix.insite.local.ehr2edc.ehr.fhir.event.config.EHRFhirEventConfiguration
import com.custodix.insite.local.ehr2edc.events.SubjectRegisteredEvent
import eu.ehr4cr.workbench.local.eventhandlers.AsyncEventsTest
import eu.ehr4cr.workbench.local.eventhandlers.AsynchronousEventHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import

import java.time.LocalDate

import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.timeout
import static org.mockito.Mockito.verify

@Import([ AsynchronousEventHandler, EHRFhirEventConfiguration ])
class FhirSubjectRegistrationControllerSpec extends AsyncEventsTest {

    private static final String PATIENT_ID = "patient"
    private static final String STUDY_ID = "study"
    private static final String NAMESPACE = "namespace"
    private static final String SUBJECT_ID = "subject"
    private static final LocalDate DATE = LocalDate.of(2050, 12, 31)

    @MockBean
    protected FhirSubjectRegistration fhirSubjectRegistration

    @Autowired
    private EventPublisher eventPublisher

    def "The 'subject registered'-event triggers fhir registration handling"() {
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
        verify(fhirSubjectRegistration, timeout(5000)).register(argThat({
            FhirSubjectRegistration.Request request ->
                request.patientId.id == PATIENT_ID &&
                        request.patientId.source == NAMESPACE &&
                        request.studyId.id == STUDY_ID &&
                        request.subjectId.id == SUBJECT_ID
        }) as FhirSubjectRegistration.Request)
    }
}
