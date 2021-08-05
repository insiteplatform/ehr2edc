package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.DomainEventPublisher
import com.custodix.insite.local.ehr2edc.EventPublisher
import com.custodix.insite.local.ehr2edc.command.DatawarehouseUpdated
import com.custodix.insite.local.ehr2edc.events.DatawarehouseUpdatedEvent
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

@Title("Datawarehouse updated")
class DatawarehouseUpdatedSpec extends AbstractSpecification {

    @Autowired
    DatawarehouseUpdated datawarehouseUpdated

    EventPublisher eventPublisher

    def setup() {
        eventPublisher = Mock()
        DomainEventPublisher.setPublisher(eventPublisher)
    }

    def teardown() {
        0 * _
    }

    def "If the datawarehouse is updated an event is sent in the domain"() {
        when: "The datawarehouse is updated"
        datawarehouseUpdated.update()

        then: "a DatawarehouseUpdated event is send out"
        1 * eventPublisher.publishEvent(_ as DatawarehouseUpdatedEvent)
    }

    def "A datawarehouse update should succeed for an unauthenticated user"() {
        given: "The user is not authenticated"
        withoutAuthenticatedUser()

        when: "The datawarehouse is updated"
        datawarehouseUpdated.update()

        then: "The request should succeed"
        noExceptionThrown()
    }
}