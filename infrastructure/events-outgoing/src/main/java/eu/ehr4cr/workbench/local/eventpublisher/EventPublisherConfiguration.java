package eu.ehr4cr.workbench.local.eventpublisher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jms.core.JmsTemplate;

@ConditionalOnMissingBean(EventPublisher.class)
@Configuration
public class EventPublisherConfiguration {
	@Bean
	EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher, JmsTemplate jmsTemplate) {
		return new SpringEventPublisher(applicationEventPublisher, jmsTemplate);
	}

	@EventListener(ContextRefreshedEvent.class)
	void initDomain(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		provideLwbEventPublisher(applicationContext);
		provideEHR2EDCEventPublisher(applicationContext);
		provideMongoMigratorEventPublisher(applicationContext);
		provideEHREventPublisher(applicationContext);
	}

	private void provideLwbEventPublisher(ApplicationContext applicationContext) {
		EventPublisher eventPublisher = applicationContext
				.getBean(EventPublisher.class);
		DomainEventPublisher.setPublisher(eventPublisher);
	}

	private void provideEHR2EDCEventPublisher(ApplicationContext applicationContext) {
		com.custodix.insite.local.ehr2edc.EventPublisher eventPublisher = applicationContext
				.getBean(com.custodix.insite.local.ehr2edc.EventPublisher.class);
		com.custodix.insite.local.ehr2edc.DomainEventPublisher.setPublisher(eventPublisher);
	}

	private void provideMongoMigratorEventPublisher(ApplicationContext applicationContext) {
		com.custodix.insite.mongodb.export.patient.domain.model.EventPublisher eventPublisher = applicationContext
				.getBean(com.custodix.insite.mongodb.export.patient.domain.model.EventPublisher.class);
		com.custodix.insite.mongodb.export.patient.domain.model.DomainEventPublisher.setPublisher(eventPublisher);
	}

	private void provideEHREventPublisher(ApplicationContext applicationContext) {
		com.custodix.insite.local.ehr2edc.ehr.main.domain.event.EventPublisher eventPublisher = applicationContext.getBean(
				com.custodix.insite.local.ehr2edc.ehr.main.domain.event.EventPublisher.class);
		com.custodix.insite.local.ehr2edc.ehr.main.domain.event.DomainEventPublisher.setPublisher(eventPublisher);
	}
}