package com.custodix.workbench.local.mail;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import eu.ehr4cr.workbench.local.conf.view.ThymeleafEngineConfiguration;
import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.properties.SupportSettings;
import eu.ehr4cr.workbench.local.properties.SupportSettingsProperties;
import eu.ehr4cr.workbench.local.service.DomainTime;
import eu.ehr4cr.workbench.local.service.TestTimeService;

@TestConfiguration
@Import({ MailConfiguration.class, ThymeleafEngineConfiguration.class, ThymeleafAutoConfiguration.class })
class MailServiceImplTestConfiguration {
	static final String SUPPORT_MAIL = "support@devnull.com";
	static final String DRM_ADDRESS = "drm@devnull.com";
	static final String ADMIN_ADDRESS = "admin@devnull.com";
	private static final String BASE_URL = "http://test.domain.com";
	private static final String INSTALL_ID = "TEST";

	@Bean
	@Primary
	JavaMailSender javaMailSender() {
		return spy(new JavaMailSenderImpl());
	}

	@Bean
	SecurityDao securityDao() {
		SecurityDao securityDao = mock(SecurityDao.class);
		when(securityDao.findUsersByGroupname(eq(GroupType.DRM.getInnerName()))).thenReturn(
				Collections.singletonList(createUser(DRM_ADDRESS)));
		when(securityDao.findUsersByGroupname(eq(GroupType.ADM.getInnerName()))).thenReturn(
				Collections.singletonList(createUser(ADMIN_ADDRESS)));
		return securityDao;
	}

	@Bean
	SupportSettings supportSettings() {
		SupportSettingsProperties supportSettings = new SupportSettingsProperties();
		supportSettings.setMail(SUPPORT_MAIL);
		supportSettings.setBaseUrl(BASE_URL);
		supportSettings.setInstallId(INSTALL_ID);
		return supportSettings;
	}

	@Bean
	PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	private User createUser(String email) {
		DomainTime.setTime(new TestTimeService());
		return new User("John Doe", "password", email);
	}
}
