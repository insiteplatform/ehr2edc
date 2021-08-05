package com.custodix.insite.local;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.custodix.insite.local.cohort.scenario.objectMother.AuthenticationAttempts;
import com.custodix.insite.local.cohort.scenario.objectMother.Features;
import com.custodix.insite.local.cohort.scenario.objectMother.Users;
import com.custodix.insite.local.user.domain.repository.AuthenticationAttemptRepository;
import com.custodix.insite.local.user.infra.notifications.TestUserMailNotificationService;
import com.custodix.insite.local.user.infra.notifications.UserMailNotificationService;
import com.custodix.insite.local.user.vocabulary.AuthenticationLockSettings;
import com.custodix.insite.local.user.vocabulary.PasswordExpirySettings;
import eu.ehr4cr.workbench.local.TestDataSourceSettings;
import eu.ehr4cr.workbench.local.TestEventPublisher;
import eu.ehr4cr.workbench.local.TestHibernateSettings;
import eu.ehr4cr.workbench.local.conf.db.DataSourceSettings;
import eu.ehr4cr.workbench.local.conf.db.HibernateSettings;
import eu.ehr4cr.workbench.local.dao.FeatureDao;
import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.eventpublisher.EventPublisher;
import eu.ehr4cr.workbench.local.properties.AccountSecuritySettings;
import eu.ehr4cr.workbench.local.service.TestTimeService;
import eu.ehr4cr.workbench.local.service.email.MailService;
import eu.ehr4cr.workbench.local.service.email.TestMailService;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.util.Locale;

import static org.mockito.Mockito.mock;

@Configuration
@PropertySource({"classpath:application.properties", "${application.credentials.location:classpath:credentials.properties}"})
@ComponentScan(excludeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION,
														classes = org.springframework.boot.test.context.TestConfiguration.class),
								  @ComponentScan.Filter(type = FilterType.REGEX,
														pattern = "com.custodix.insite.local.ehr2edc.*") })
public class LocalWorkBenchTestConfiguration {
	@ConditionalOnProperty("integration-tests")
	@Bean(name = { "localTransactionManager" })
	@Primary
	@DependsOn("userTransactionService")
	JtaTransactionManager jtaTransactionManagerEmbedded(UserTransactionManager userTransactionManager,
			UserTransactionImp userTransactionImp,
			@Value("${transaction.timeout.seconds:3600}") int transactionTimeout) {
		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransactionImp,
				userTransactionManager);
		jtaTransactionManager.setDefaultTimeout(transactionTimeout);
		return jtaTransactionManager;
	}

	@Primary
	@Bean
	public EventPublisher testEventPublisher(EventPublisher eventPublisher) {
		return new TestEventPublisher(eventPublisher);
	}

	@Bean
	public Users users(SecurityDao securityDao, AccountSecuritySettings accountSecuritySettings,
			PasswordExpirySettings passwordExpirySettings, TestTimeService testTimeService) {
		return new Users(securityDao, accountSecuritySettings, passwordExpirySettings, testTimeService);
	}

	@Bean
	AuthenticationAttempts authenticationAttempts(AuthenticationAttemptRepository authenticationAttemptRepository,
			AuthenticationLockSettings authenticationLockSettings, TestTimeService testTimeService) {
		return new AuthenticationAttempts(authenticationAttemptRepository, authenticationLockSettings, testTimeService);
	}

	@Bean
	public Features features(FeatureDao featureDao) {
		return new Features(featureDao);
	}

	@Bean
	Locale setSystemLocale() {
		Locale english = Locale.ENGLISH;
		Locale.setDefault(english);
		return english;
	}

	@Bean
	TemporaryFolder temporaryFolder() {
		return new TemporaryFolder();
	}

	@Bean
	@Primary
	public MailService mailService() {
		return new TestMailService();
	}

	@Bean
	@Primary
	public DataSourceSettings testDataSourceSettings() {
		return new TestDataSourceSettings();
	}

	@Bean
	@Primary
	public HibernateSettings testHibernateSettings() {
		return new TestHibernateSettings();
	}

	@Bean
	@Primary
	public UserMailNotificationService testUserMailNotificationService() {
		return new TestUserMailNotificationService();
	}

	@Bean
	@Primary
	ThreadPoolTaskScheduler taskScheduler() {
		return new ThreadPoolTaskScheduler();
	}

	@Bean
	public JmsTemplate jmsTemplate() {
		return mock(JmsTemplate.class);
	}

	@Bean
	public TestTimeService testTimeService() {
		return new TestTimeService();
	}
}

