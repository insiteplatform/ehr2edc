package com.custodix.insite.mongodb.export.patient.event.controller

import com.custodix.insite.mongodb.export.patient.application.api.FailSubjectMigration
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientFailed
import com.custodix.insite.mongodb.export.patient.domain.model.EventPublisher
import org.mockito.ArgumentCaptor
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Timeout

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import static org.mockito.Mockito.verify

@Timeout(value = 2, unit = TimeUnit.MINUTES)
class FailSubjectMigrationControllerSpec extends MongoMigratorEventControllerSpec {

    private static final String SUBJECT_ID = "Subject-ID-123"
    private static final LocalDateTime NOW = LocalDateTime.now()
    private static final String NAMESPACE = "namespace"
    private static final String PATIENT_ID = "patientId-123"

    @Autowired
    private EventPublisher eventPublisher

    ArgumentCaptor<FailSubjectMigration.Request> failSubjectMigrationRequest = ArgumentCaptor.forClass(FailSubjectMigration.Request.class);

    def "Publish ExportPatientFailed triggers FailSubjectMigration use case"() {
        given: "an exportPatientFailed"
        def exportPatientFailed = ExportPatientFailed.newBuilder().withSubjectId(SUBJECT_ID).withDate(NOW).withNamespace(NAMESPACE).withPatientId(PATIENT_ID).build()

        when: "publishing event"
        eventPublisher.publishEvent(exportPatientFailed)

        then: "FailSubjectMigration use case is called asynchronously"
        verify(failSubjectMigration).fail(failSubjectMigrationRequest.capture())
        failSubjectMigrationRequest.value.subjectId.id == SUBJECT_ID
        failSubjectMigrationRequest.value.failDate.isEqual(NOW)
    }
}
