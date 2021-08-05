package com.custodix.insite.local.ehr2edc.event.controller

import com.custodix.insite.AsyncEventsTest
import com.custodix.insite.local.ehr2edc.command.CreateSubjectInEDC
import com.custodix.insite.local.ehr2edc.event.AsynchronousEventHandler
import com.custodix.insite.local.ehr2edc.event.handler.EHR2EDCAsyncEventHandler
import com.custodix.insite.local.ehr2edc.events.SubjectCreated
import com.custodix.insite.mongodb.export.patient.domain.model.EventPublisher
import org.jeasy.random.EasyRandom
import org.mockito.ArgumentCaptor
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
        SubjectCreatedEventController
])
@Timeout(10)
@MockBeans([
        @MockBean(DatawarehouseUpdatedEventHandler),
        @MockBean(UpdateEHRSubjectRegistrationStatusController),
])
class SubjectCreatedEventControllerSpec extends AsyncEventsTest {
    @Autowired
    SubjectCreatedEventController controller

    @MockBean
    CreateSubjectInEDC createSubjectInEDC

    ArgumentCaptor<CreateSubjectInEDC.Request> requestArgumentCaptor = ArgumentCaptor.forClass(CreateSubjectInEDC.Request)

    @Autowired
    private EventPublisher eventPublisher

    def "A SubjectCreatedEvent triggers the CreateSubjectInEDC usecase"() {
        given: "a SubjectCreatedEvent"
        EasyRandom easyRandom = new EasyRandom()
        SubjectCreated subjectCreatedEvent = easyRandom.nextObject(SubjectCreated.class)

        when: "publishing the event 'SubjectCreatedEvent'"
        eventPublisher.publishEvent(subjectCreatedEvent)

        then: "the CreateSubjectInEDC gets called with the values of the event"
        verify(createSubjectInEDC, timeout(5000)).create(requestArgumentCaptor.capture())
        def value = requestArgumentCaptor.value
        value.edcSubjectReference == subjectCreatedEvent.edcSubjectReference
        value.studyId == subjectCreatedEvent.studyId
    }


}
