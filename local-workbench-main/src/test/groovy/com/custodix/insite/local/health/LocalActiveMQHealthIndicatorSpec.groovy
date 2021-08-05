package com.custodix.insite.local.health

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import eu.ehr4cr.workbench.local.AsyncSerializationSafeDomainEvent
import eu.ehr4cr.workbench.local.annotation.LwbEventListener
import eu.ehr4cr.workbench.local.eventhandlers.AsyncEventsTest
import eu.ehr4cr.workbench.local.eventpublisher.EventPublisher
import eu.ehr4cr.workbench.local.health.LocalActiveMQHealthIndicator
import eu.ehr4cr.workbench.local.service.impl.LocalActiveMQMonitor
import org.apache.activemq.RedeliveryPolicy
import org.awaitility.Awaitility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.health.Health
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

import static org.springframework.boot.actuate.health.Status.DOWN
import static org.springframework.boot.actuate.health.Status.UP

@ContextConfiguration(classes = [LocalActiveMQHealthIndicatorSpecConfiguration])
@TestPropertySource(properties = ["jms.local.jmxEnabled=true"])
class LocalActiveMQHealthIndicatorSpec extends AsyncEventsTest {
    @Autowired
    private LocalActiveMQHealthIndicator activeMQHealthIndicator
    @Autowired
    private LocalActiveMQMonitor localActiveMQService
    @Autowired
    private EventPublisher eventPublisher

    def "The indicator is healthy when no messages are on the dead letter queue"() {
        given: "There are no messages on the dead letter queue"
        assert localActiveMQService.getDLQMessageCount() == 0

        when: "I query the health indicator"
        Health health = activeMQHealthIndicator.health()

        then: "The indicator is healthy"
        with(health) {
            status == UP
            details.get("dlqMessageCount") == 0
        }
    }

    // If this test fails make sure you LWB is not running
    def "The indicator is unhealthy when there are messages on the dead letter queue"() {
        given: "There is a message on the dead letter queue"
        eventPublisher.publishEvent(new TestEvent())
        Awaitility.await().until { localActiveMQService.getDLQMessageCount() == 1 }

        when: "I query the health indicator"
        Health health = activeMQHealthIndicator.health()

        then: "The indicator is unhealthy"
        with(health) {
            status == DOWN
            details.get("dlqMessageCount") == 1
        }
    }


    static class LocalActiveMQHealthIndicatorSpecConfiguration {
        @LwbEventListener
        void handleTestEvent(TestEvent testEvent) {
            throw new RuntimeException("This is a test exception")
        }

        @Primary
        @Bean
        RedeliveryPolicy noRedelivery() {
            RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy()
            redeliveryPolicy.setMaximumRedeliveries(0)
            return redeliveryPolicy
        }
    }

    static final class TestEvent implements AsyncSerializationSafeDomainEvent {
        private String property

        @JsonCreator
        TestEvent(@JsonProperty("property") String property) {
            this.property = property
        }

        String getProperty() {
            return property
        }
    }
}
