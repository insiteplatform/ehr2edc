package com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller

import com.custodix.insite.local.ehr2edc.ehr.mongo.command.StartSubjectRegistration
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientStarting
import com.custodix.insite.mongodb.export.patient.domain.model.EventPublisher
import org.mockito.ArgumentCaptor
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Timeout

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import static org.mockito.Mockito.verify

@Timeout(value = 2, unit = TimeUnit.MINUTES)
class StartSubjectRegistrationControllerSpec extends AbstractEHRMongoEventControllerSpec {

    private static final String SUBJECT_ID = "Subject-ID-123"
    private static final LocalDateTime NOW = LocalDateTime.now()
    private static final String NAMESPACE = "namespace"
    private static final String PATIENT_ID = "patientId-123"

    @Autowired
    private EventPublisher eventPublisher

    ArgumentCaptor<StartSubjectRegistration.Request> startSubjectRegistrationRequest = ArgumentCaptor.forClass(StartSubjectRegistration.Request.class);

    def "Publish ExportPatientStarting triggers ehr subject registration to start"() {
        given: "an exportPatientStarting"
        def exportPatientStarting = ExportPatientStarting.newBuilder().withSubjectId(SUBJECT_ID).withDate(NOW).withNamespace(NAMESPACE).withPatientId(PATIENT_ID).build()

        when: "publishing event"
        eventPublisher.publishEvent(exportPatientStarting)

        then: "EHR subject registration to start is called synchronously"
        verify(startSubjectRegistration).start(startSubjectRegistrationRequest.capture())
        startSubjectRegistrationRequest.value.subjectId.id == SUBJECT_ID
        startSubjectRegistrationRequest.value.patientCDWReference.id == PATIENT_ID
        startSubjectRegistrationRequest.value.patientCDWReference.source == NAMESPACE
    }
}
