package com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller


import com.custodix.insite.local.ehr2edc.ehr.mongo.command.SucceedSubjectRegistration
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientEnded
import com.custodix.insite.mongodb.export.patient.domain.model.EventPublisher
import org.mockito.ArgumentCaptor
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Timeout

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import static org.mockito.Mockito.verify

@Timeout(value = 2, unit = TimeUnit.MINUTES)
class SucceedSubjectRegistrationControllerSpec extends AbstractEHRMongoEventControllerSpec {

    private static final String SUBJECT_ID = "Subject-ID-123"
    private static final LocalDateTime NOW = LocalDateTime.now()
    private static final String NAMESPACE = "namespace"
    private static final String PATIENT_ID = "patientId-123"

    @Autowired
    private EventPublisher eventPublisher

    ArgumentCaptor<SucceedSubjectRegistration.Request> succeedEHRRegistrationRequest = ArgumentCaptor.forClass(SucceedSubjectRegistration.Request.class);

    def "Publish ExportPatientEnded triggers update ehr registration to succeed"() {
        given: "an exportPatientEnded"
        def exportPatientEnded = ExportPatientEnded.newBuilder().withSubjectId(SUBJECT_ID).withDate(NOW).withNamespace(NAMESPACE).withPatientId(PATIENT_ID).build()

        when: "publishing event"
        eventPublisher.publishEvent(exportPatientEnded)

        then: "Update EHR registration to succeed is called synchronously"
        verify(succeedEHRRegistration).succeed(succeedEHRRegistrationRequest.capture())
        succeedEHRRegistrationRequest.value.subjectId.id == SUBJECT_ID
        succeedEHRRegistrationRequest.value.patientCDWReference.id == PATIENT_ID
        succeedEHRRegistrationRequest.value.patientCDWReference.source == NAMESPACE
    }
}
