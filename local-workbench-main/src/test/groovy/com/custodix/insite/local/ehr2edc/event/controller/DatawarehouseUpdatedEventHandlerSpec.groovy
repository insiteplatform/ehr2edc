package com.custodix.insite.local.ehr2edc.event.controller

import com.custodix.insite.local.ehr2edc.event.handler.EHR2EDCAsyncEventHandler
import com.custodix.insite.local.ehr2edc.events.DatawarehouseUpdatedEvent
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatientSearchCriteriaInformation
import com.custodix.insite.mongodb.export.patient.application.api.ExportSubjects
import eu.ehr4cr.workbench.local.eventhandlers.AsyncEventsTest
import eu.ehr4cr.workbench.local.eventhandlers.AsynchronousEventHandler
import eu.ehr4cr.workbench.local.eventpublisher.EventPublisher
import org.jeasy.random.EasyRandom
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.MockBeans
import org.springframework.context.annotation.Import
import spock.lang.Timeout

import static org.mockito.Mockito.timeout
import static org.mockito.Mockito.verify

@Import([
        AsynchronousEventHandler,
        EHR2EDCAsyncEventHandler,
        ExportPatientIdsController,
        ExportSubjectsController,
        DatawarehouseUpdatedEventHandler
])
@Timeout(10)
@MockBeans([
        @MockBean(SubjectCreatedEventController),
        @MockBean(UpdateEHRSubjectRegistrationStatusController)
])
class DatawarehouseUpdatedEventHandlerSpec extends AsyncEventsTest {

    @MockBean
    private ExportPatientSearchCriteriaInformation exportPatientsIds
    @MockBean
    private ExportSubjects exportSubjects
    @Autowired
    private EventPublisher eventPublisher

    def "A datawarehouse updated event triggers the exportpatientids usecase with JMS"() {
        given: "a datawarehouseUpdatedEvent"
        EasyRandom easyRandom = new EasyRandom()
        DatawarehouseUpdatedEvent datawarehouseUpdatedEvent = easyRandom.nextObject(DatawarehouseUpdatedEvent.class)

        when: "publishing the event 'datawarehouseUpdatedEvent'"
        eventPublisher.publishEvent(datawarehouseUpdatedEvent)

        then: "the handled event results in the export starting of ExportPatientIds"
        verify(exportPatientsIds, timeout(5000)).export()
    }

    def "A datawarehouse updated event triggers the exportSubjects usecase with JMS"() {
        given: "a datawarehouseUpdatedEvent"
        EasyRandom easyRandom = new EasyRandom()
        DatawarehouseUpdatedEvent datawarehouseUpdatedEvent = easyRandom.nextObject(DatawarehouseUpdatedEvent.class)

        when: "publishing the event 'datawarehouseUpdatedEvent'"
        eventPublisher.publishEvent(datawarehouseUpdatedEvent)

        then: "the handled event results in the export starting of ExportPatientIds"
        verify(exportSubjects, timeout(5000)).export()
    }
}
