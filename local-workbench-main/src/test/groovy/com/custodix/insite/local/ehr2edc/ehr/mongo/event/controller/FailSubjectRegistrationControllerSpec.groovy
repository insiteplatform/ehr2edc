package com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller

import com.custodix.insite.local.ehr2edc.ehr.mongo.command.FailSubjectRegistration
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientFailed
import com.custodix.insite.mongodb.export.patient.domain.model.EventPublisher
import org.mockito.ArgumentCaptor
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Timeout

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import static org.mockito.Mockito.verify

@Timeout(value = 2, unit = TimeUnit.MINUTES)
class FailSubjectRegistrationControllerSpec extends AbstractEHRMongoEventControllerSpec {

    private static final String SUBJECT_ID = "Subject-ID-123"
    private static final LocalDateTime NOW = LocalDateTime.now()
    private static final String NAMESPACE = "namespace"
    private static final String PATIENT_ID = "patientId-123"

    @Autowired
    private EventPublisher eventPublisher

    ArgumentCaptor<FailSubjectRegistration.Request> failSubjectRegistrationRequest = ArgumentCaptor.forClass(FailSubjectRegistration.Request.class);

    def "Publish ExportPatientFailed triggers ehr subject registration to fail"() {
        given: "an exportPatientFailed"
        def exportPatientFailed = ExportPatientFailed.newBuilder().withSubjectId(SUBJECT_ID).withDate(NOW).withNamespace(NAMESPACE).withPatientId(PATIENT_ID).build()

        when: "publishing event"
        eventPublisher.publishEvent(exportPatientFailed)

        then: "EHR subject registration to fail is called synchronously"
        verify(failSubjectRegistration).fail(failSubjectRegistrationRequest.capture())
        failSubjectRegistrationRequest.value.subjectId.id == SUBJECT_ID
        failSubjectRegistrationRequest.value.patientCDWReference.id == PATIENT_ID
        failSubjectRegistrationRequest.value.patientCDWReference.source == NAMESPACE
    }
}
