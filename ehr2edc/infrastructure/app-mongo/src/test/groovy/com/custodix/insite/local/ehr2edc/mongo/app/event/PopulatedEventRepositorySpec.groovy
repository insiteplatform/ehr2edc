package com.custodix.insite.local.ehr2edc.mongo.app.event

import com.custodix.insite.local.ehr2edc.RepositorySpec
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventRepository
import org.springframework.beans.factory.annotation.Autowired

import static com.custodix.insite.local.ehr2edc.populator.PopulatedEventObjectMother.*

class PopulatedEventRepositorySpec extends RepositorySpec {
    @Autowired
    PopulatedEventRepository eventRepository

    def "getEvent"() {
        given: "An event"
        PopulatedEvent givenEvent = aDefaultEvent()
        eventRepository.save(givenEvent)

        when: "I get the event"
        PopulatedEvent event = eventRepository.getEvent(givenEvent.instanceId)

        then: "The event is returned"
        event.instanceId == EVENT_ID
        event.subjectId == SUBJECT_ID
        event.studyId == STUDY_ID
        event.referenceDate == REFERENCE_DATE
        event.eventDefinitionId == EVENT_DEFINITION_ID
        event.edcSubjectReference == EDC_SUBJECT_REFERENCE
        event.populator.present
        event.populator.get() == POPULATOR
    }

    def "getEvent without a populator"() {
        given: "An event"
        PopulatedEvent givenEvent = aDefaultEventBuilder()
                .withPopulator(null)
                .build()
        eventRepository.save(givenEvent)

        when: "I get the event"
        PopulatedEvent event = eventRepository.getEvent(givenEvent.instanceId)

        then: "The event is returned"
        event.instanceId == EVENT_ID
        event.subjectId == SUBJECT_ID
        event.studyId == STUDY_ID
        event.referenceDate == REFERENCE_DATE
        event.eventDefinitionId == EVENT_DEFINITION_ID
        event.edcSubjectReference == EDC_SUBJECT_REFERENCE
        !event.populator.present
    }
}