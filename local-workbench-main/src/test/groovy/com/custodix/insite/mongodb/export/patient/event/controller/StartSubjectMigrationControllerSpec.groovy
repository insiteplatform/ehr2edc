package com.custodix.insite.mongodb.export.patient.event.controller

import com.custodix.insite.mongodb.export.patient.application.api.StartSubjectMigration
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientStarting
import com.custodix.insite.mongodb.export.patient.domain.model.EventPublisher
import org.mockito.ArgumentCaptor
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Timeout

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import static org.mockito.Mockito.verify

@Timeout(value = 2, unit = TimeUnit.MINUTES)
class StartSubjectMigrationControllerSpec extends MongoMigratorEventControllerSpec {

    private static final String SUBJECT_ID = "Subject-ID-123"
    private static final LocalDateTime NOW = LocalDateTime.now()
    private static final String NAMESPACE = "namespace"
    private static final String PATIENT_ID = "patientId-123"

    @Autowired
    private EventPublisher eventPublisher

    ArgumentCaptor<StartSubjectMigration.Request> startSubjectMigrationRequest = ArgumentCaptor.forClass(StartSubjectMigration.Request.class);

    def "Publish ExportPatientStarting triggers StartSubjectMigration use case"() {
        given: "an exportPatientStarting"
        def exportPatientStarting = ExportPatientStarting.newBuilder().withSubjectId(SUBJECT_ID).withDate(NOW).withNamespace(NAMESPACE).withPatientId(PATIENT_ID).build()

        when: "publishing event"
        eventPublisher.publishEvent(exportPatientStarting)

        then: "StartSubjectMigration use case is called synchronously"
        verify(startSubjectMigration).start(startSubjectMigrationRequest.capture())
        startSubjectMigrationRequest.value.subjectId.id == SUBJECT_ID
        startSubjectMigrationRequest.value.startDate.isEqual(NOW)
    }
}
