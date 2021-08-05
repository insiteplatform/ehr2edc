package eu.ehr4cr.workbench.local.eventhandlers

import eu.ehr4cr.workbench.local.conf.jms.LocalJMSConfiguration
import eu.ehr4cr.workbench.local.eventpublisher.EventPublisherConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@ContextConfiguration(classes = [LocalJMSConfiguration, EventPublisherConfiguration])
@TestPropertySource(properties = ["jms.local.jmxEnabled=false", "jms.local.persistent=false"])
@DirtiesContext
class AsyncEventsTest extends Specification {

    @Autowired
    protected eu.ehr4cr.workbench.local.eventpublisher.EventPublisher eventPublisher
    @Autowired
    protected com.custodix.insite.local.ehr2edc.EventPublisher ehr2edcEventPublisher
    @Autowired
    protected com.custodix.insite.mongodb.export.patient.domain.model.EventPublisher mongoMigratorEventPublisher
    @Autowired
    protected com.custodix.insite.local.ehr2edc.ehr.main.domain.event.EventPublisher ehrEventPublisher
    @Autowired
    protected com.custodix.insite.local.ehr2edc.ehr.epic.main.domain.event.EventPublisher ehrEpicEventPublisher

}
