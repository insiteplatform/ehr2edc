package eu.ehr4cr.workbench.local.eventpublisher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;

class SpringEventPublisher implements com.custodix.insite.local.ehr2edc.EventPublisher,
									  com.custodix.insite.mongodb.export.patient.domain.model.EventPublisher,
									  com.custodix.insite.local.ehr2edc.ehr.main.domain.event.EventPublisher,
									  com.custodix.insite.local.ehr2edc.ehr.epic.main.domain.event.EventPublisher  {
	public static final String EHR2EDC_EVENTS_DESTINATION = "ehr2edc.events";
	public static final String EHR_EVENTS_DESTINATION = "ehr.events";
	public static final String EHR2EDC_EHR_EPIC_EVENTS_DESTINATION = "ehr2edc.ehr.epic.events";
	public static final String MONGO_MIGRATOR_EVENTS_DESTINATION = "mongo.migrator.events";
	private final ApplicationEventPublisher applicationEventPublisher;
	private final JmsTemplate jmsTemplate;

	SpringEventPublisher(ApplicationEventPublisher applicationEventPublisher, JmsTemplate jmsTemplate) {
		this.applicationEventPublisher = applicationEventPublisher;
		this.jmsTemplate = jmsTemplate;
	}

	@Transactional
	@Override
	public void publishEvent(Object event) {
		publishEventSynchronously(event);
		publishEventAsynchronously(event);
	}

	private void publishEventSynchronously(Object event) {
		applicationEventPublisher.publishEvent(event);
	}

	private void publishEventAsynchronously(Object event) {
		handleEHR2EDCJmsEventDispatching(event);
		handleMongoMigratorJmsEventDispatching(event);
		handleEHRJmsEventDispatching(event);
		handleEHR2EDCEHREpicJmsEventDispatching(event);
	}

	private void handleEHR2EDCJmsEventDispatching(Object event) {
		if (event instanceof com.custodix.insite.local.ehr2edc.events.AsyncSerializationSafeDomainEvent) {
			jmsTemplate.convertAndSend(EHR2EDC_EVENTS_DESTINATION, event);
		}
	}

	private void handleMongoMigratorJmsEventDispatching(Object event) {
		if (event instanceof com.custodix.insite.mongodb.export.patient.domain.event.AsyncSerializationSafeDomainEvent) {
			jmsTemplate.convertAndSend(MONGO_MIGRATOR_EVENTS_DESTINATION, event);
		}
	}

	private void handleEHRJmsEventDispatching(Object event) {
		if (event instanceof com.custodix.insite.local.ehr2edc.ehr.domain.event.AsyncSerializationSafeDomainEvent) {
			jmsTemplate.convertAndSend(EHR_EVENTS_DESTINATION, event);
		}
	}

	private void handleEHR2EDCEHREpicJmsEventDispatching(Object event) {
		if (event instanceof com.custodix.insite.local.ehr2edc.ehr.epic.domain.event.AsyncSerializationSafeDomainEvent) {
			jmsTemplate.convertAndSend(EHR2EDC_EHR_EPIC_EVENTS_DESTINATION, event);
		}
	}
}