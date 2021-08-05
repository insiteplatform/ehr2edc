package com.custodix.insite.mongodb.export.patient.event.controller

import com.custodix.insite.mongodb.export.patient.application.api.EndSubjectMigration
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientEnded
import com.custodix.insite.mongodb.export.patient.domain.model.EventPublisher
import org.mockito.ArgumentCaptor
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Timeout

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import static org.mockito.Mockito.verify

@Timeout(value = 2, unit = TimeUnit.MINUTES)
class EndSubjectMigrationControllerSpec extends MongoMigratorEventControllerSpec {

    private static final String SUBJECT_ID = "Subject-ID-123"
    private static final LocalDateTime NOW = LocalDateTime.now()
    private static final String NAMESPACE = "namespace"
    private static final String PATIENT_ID = "patientId-123"

    @Autowired
    private EventPublisher eventPublisher

    ArgumentCaptor<EndSubjectMigration.Request> endSubjectMigrationRequest = ArgumentCaptor.forClass(EndSubjectMigration.Request.class);

    def "Publish ExportPatientEnded triggers EndSubjectMigration use case"() {
        given: "an exportPatientEnded"
        def exportPatientEnded = ExportPatientEnded.newBuilder().withSubjectId(SUBJECT_ID).withDate(NOW).withNamespace(NAMESPACE).withPatientId(PATIENT_ID).build()

        when: "publishing event"
        eventPublisher.publishEvent(exportPatientEnded)

        then: "EndSubjectMigration use case is called synchronously"
        verify(endSubjectMigration).end(endSubjectMigrationRequest.capture())
        endSubjectMigrationRequest.value.subjectId.id == SUBJECT_ID
        endSubjectMigrationRequest.value.endDate.isEqual(NOW)
    }
}
