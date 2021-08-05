package com.custodix.insite.local.ehr2edc.jms;

import java.util.Date;

import javax.jms.Session;
import javax.jms.XAConnectionFactory;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.broker.region.policy.SharedDeadLetterStrategy;
import org.apache.activemq.spring.ActiveMQXAConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosConnectionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.custodix.insite.local.ehr2edc.transactions.TransactionConfiguration;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ConditionalOnProperty(name = "jms.local.enabled",
					   havingValue = "true",
					   matchIfMissing = true)
@EnableJms
@Import({ TransactionConfiguration.class })
@EnableConfigurationProperties(LocalJMSSettingsProperties.class)
@Configuration
public class LocalJMSConfiguration {
	private static final String BROKER_URL = "vm://localhost";
	private static final int MAXIMUM_REDELIVERIES = 8;
	private static final int TEN_SECONDS = 10_000;
	private static final int FIVE_MINUTES = 300_000;

	@Bean
	JmsTemplate jmsTemplate(AtomikosConnectionFactoryBean atomikosConnectionFactoryBean,
			MessageConverter messageConverter, LocalJMSSettings localJMSSettings) {
		JmsTemplate jmsTemplate = new JmsTemplate(atomikosConnectionFactoryBean);
		jmsTemplate.setReceiveTimeout(localJMSSettings.getReceiveTimeoutMillis());
		jmsTemplate.setSessionTransacted(true);
		jmsTemplate.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
		jmsTemplate.setMessageConverter(messageConverter);
		return jmsTemplate;
	}

	@Bean(initMethod = "init",
		  destroyMethod = "destroy")
	AtomikosConnectionFactoryBean atomikosConnectionFactoryBean(XAConnectionFactory xaConnectionFactory,
			LocalJMSSettings localJMSSettings) {
		AtomikosConnectionFactoryBean cf = new AtomikosConnectionFactoryBean();
		cf.setUniqueResourceName("lwb-jms-" + new Date().getTime());
		cf.setXaConnectionFactory(xaConnectionFactory);
		cf.setPoolSize(localJMSSettings.getPoolSize());
		return cf;
	}

	@Bean
	@DependsOn("brokerService")
	XAConnectionFactory xaConnectionFactory(RedeliveryPolicy redeliveryPolicy) {
		ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new ActiveMQXAConnectionFactory();
		activeMQXAConnectionFactory.setBrokerURL(BROKER_URL);
		activeMQXAConnectionFactory.setRedeliveryPolicy(redeliveryPolicy);
		activeMQXAConnectionFactory.setNonBlockingRedelivery(true);
		return activeMQXAConnectionFactory;
	}

	@Bean
	RedeliveryPolicy redeliveryPolicy() {
		RedeliveryPolicy policy = new RedeliveryPolicy();
		policy.setUseExponentialBackOff(true);
		policy.setBackOffMultiplier(3);
		policy.setInitialRedeliveryDelay(TEN_SECONDS);
		policy.setMaximumRedeliveryDelay(FIVE_MINUTES);
		policy.setMaximumRedeliveries(MAXIMUM_REDELIVERIES);
		return policy;
	}

	@Bean
	JmsListenerContainerFactory jmsListenerContainerFactory(AtomikosConnectionFactoryBean atomikosConnectionFactoryBean,
			JtaTransactionManager jtaTransactionManager, MessageConverter messageConverter) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(atomikosConnectionFactoryBean);
		factory.setSessionTransacted(true);
		factory.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
		factory.setMessageConverter(messageConverter);
		factory.setTransactionManager(jtaTransactionManager);
		factory.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONSUMER);
		return factory;
	}

	@Bean
	MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		converter.setObjectMapper(jmsObjectMapper());
		return converter;
	}

	private ObjectMapper jmsObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.registerModule(createEHR2EDCDomainEventModule());
		objectMapper.registerModule(createMongoMigratorDomainEventModule());
		objectMapper.registerModule(createVocabularyModule());
		objectMapper.registerModule(createEHRDomainEventModule());
		objectMapper.registerModule(createEHR2EDCEHREpicDomainEventModule());
		return objectMapper;
	}

	private Module createVocabularyModule() {
		return reflectionModuleInit("com.custodix.insite.local.ehr2edc.jackson.VocabularyModule");
	}

	private Module createEHR2EDCDomainEventModule() {
		return reflectionModuleInit("eu.ehr4cr.workbench.local.eventpublisher.EHR2EDCDomainEventMixinModule");
	}

	private Module createMongoMigratorDomainEventModule() {
		return reflectionModuleInit("eu.ehr4cr.workbench.local.eventpublisher.MongoMigratorDomainEventMixinModule");
	}

	private Module createEHRDomainEventModule() {
		return reflectionModuleInit("eu.ehr4cr.workbench.local.eventpublisher.EHRDomainEventMixinModule");
	}

	private Module createEHR2EDCEHREpicDomainEventModule() {
		return reflectionModuleInit("eu.ehr4cr.workbench.local.eventpublisher.EHR2EDCEHREpicDomainEventMixinModule");
	}

	private Module reflectionModuleInit(String className) {
		try {
			return (Module) Class.forName(className)
					.getDeclaredMethod("create")
					.invoke(null);
		} catch (Exception e) {
			throw new RuntimeException("Could not initialize domain event mixin module from EHR2EDC", e);
		}
	}

	@Bean
	LocalActiveMQHealthIndicator localActiveMQHealthIndicator(LocalActiveMQMonitor localActiveMQMonitor) {
		return new LocalActiveMQHealthIndicator(localActiveMQMonitor);
	}

	@Bean
	LocalActiveMQMonitor localActiveMQMonitor() {
		return new LocalActiveMQMonitor();
	}

	@Bean
	BrokerService brokerService(LocalJMSSettings localJMSSettings, PolicyMap policyMap) {
		BrokerService brokerService = new BrokerService();
		brokerService.setUseShutdownHook(false);
		brokerService.setUseJmx(localJMSSettings.isJmxEnabled());
		brokerService.setPersistent(localJMSSettings.isPersistent());
		brokerService.setDestinationPolicy(policyMap);
		brokerService.setDataDirectory(localJMSSettings.getDataDirectory());
		ManagementContext managementContext = new ManagementContext();
		managementContext.setConnectorPort(1100);
		brokerService.setManagementContext(managementContext);
		return brokerService;
	}

	@Bean
	PolicyEntry sharedDLQPolicy() {
		PolicyEntry dlqPolicy = new PolicyEntry();
		dlqPolicy.setDeadLetterStrategy(new SharedDeadLetterStrategy());
		return dlqPolicy;
	}

	@Bean
	PolicyMap brokerServicePolicy(PolicyEntry policyEntry) {
		PolicyMap policyMap = new PolicyMap();
		policyMap.setDefaultEntry(policyEntry);
		return policyMap;
	}
}
