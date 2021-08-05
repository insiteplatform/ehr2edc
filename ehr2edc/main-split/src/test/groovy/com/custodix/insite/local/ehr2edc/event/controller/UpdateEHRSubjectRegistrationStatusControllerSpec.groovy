package com.custodix.insite.local.ehr2edc.event.controller

import com.custodix.insite.AsyncEventsTest
import com.custodix.insite.local.ehr2edc.EventPublisher
import com.custodix.insite.local.ehr2edc.command.UpdateEHRSubjectRegistrationStatus
import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatus
import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatusUpdated
import com.custodix.insite.local.ehr2edc.event.AsynchronousEventHandler
import com.custodix.insite.local.ehr2edc.event.handler.EHR2EDCAsyncEventHandler
import com.custodix.insite.local.ehr2edc.vocabulary.EHRRegistrationStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.MockBeans
import org.springframework.context.annotation.Import
import spock.lang.Timeout

import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.timeout
import static org.mockito.Mockito.verify

@Import([
        AsynchronousEventHandler,
        EHR2EDCAsyncEventHandler,
        UpdateEHRSubjectRegistrationStatusController,
])
@Timeout(10)
@MockBeans([
        @MockBean(SubjectCreatedEventController),
        @MockBean(DatawarehouseUpdatedEventHandler)
])
class UpdateEHRSubjectRegistrationStatusControllerSpec extends AsyncEventsTest {

    private static final String PATIENT_ID = "patient"
    private static final String NAMESPACE = "namespace"
    private static final String SUBJECT_ID = "subject"

    @Autowired
    private EventPublisher eventPublisher
    @MockBean
    protected UpdateEHRSubjectRegistrationStatus updateEHRSubjectRegistrationStatus

    def "The REGISTERED ehr subject registration event triggers an ehr subject registration to be registered"() {
        given: 'A EHRSubjectRegistrationStatusUpdated event with status REGISTERED'
        EHRSubjectRegistrationStatusUpdated event = EHRSubjectRegistrationStatusUpdated.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withNamespace(NAMESPACE)
                .withPatientId(PATIENT_ID)
                .withStatus(EHRSubjectRegistrationStatus.REGISTERED)
                .build()

        when: "The event is published"
        eventPublisher.publishEvent(event)

        then: "the ehr subject registration is triggered to be registered "
        verify(updateEHRSubjectRegistrationStatus , timeout(5000)).update(argThat({
            request ->
                request.subjectId.id == SUBJECT_ID
                request.patientCDWReference.id == PATIENT_ID
                request.status == EHRRegistrationStatus.REGISTERED
        }))
    }

    def "The FAILED ehr subject registration event triggers an ehr subject registration to be NOT REGISTERED"() {
        given: 'A EHRSubjectRegistrationStatusUpdated event with status FAILED'
        EHRSubjectRegistrationStatusUpdated event = EHRSubjectRegistrationStatusUpdated.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withNamespace(NAMESPACE)
                .withPatientId(PATIENT_ID)
                .withStatus(EHRSubjectRegistrationStatus.FAILED)
                .build()

        when: "The event is published"
        eventPublisher.publishEvent(event)

        then: "the ehr subject registration is triggered to be failed "
        verify(updateEHRSubjectRegistrationStatus , timeout(5000)).update(argThat({
            request ->
                request.subjectId.id == SUBJECT_ID
                request.patientCDWReference.id == PATIENT_ID
                request.status == EHRRegistrationStatus.NOT_REGISTERED
        }))
    }

    def "The STARTED ehr subject registration event triggers an NOT REGISTERED ehr subject registration "() {
        given: 'A EHRSubjectRegistrationStatusUpdated event with status STARTED'
        EHRSubjectRegistrationStatusUpdated event = EHRSubjectRegistrationStatusUpdated.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withNamespace(NAMESPACE)
                .withPatientId(PATIENT_ID)
                .withStatus(EHRSubjectRegistrationStatus.STARTED)
                .build()

        when: "The event is published"
        eventPublisher.publishEvent(event)

        then: "the ehr subject registration is triggered to be not registered "
        verify(updateEHRSubjectRegistrationStatus , timeout(5000)).update(argThat({
            request ->
                request.subjectId.id == SUBJECT_ID
                request.patientCDWReference.id == PATIENT_ID
                request.status == EHRRegistrationStatus.NOT_REGISTERED
        }))
    }
}
