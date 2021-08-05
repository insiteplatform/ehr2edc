package com.custodix.workbench.local.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import com.custodix.insite.local.user.infra.notifications.UserMailNotificationService;

import eu.ehr4cr.workbench.local.conf.view.ThymeleafEngineConfiguration;
import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.properties.SupportSettings;
import eu.ehr4cr.workbench.local.service.email.MailService;

@Import(ThymeleafEngineConfiguration.class)
@Configuration
public class MailConfiguration {

	@Bean
	MailService mailService(MailSender lwbMailSender,
			@Value("${support.mail:support@custodix.com}") String supportMail, RecipientRetriever recipientRetriever) {
		return new MailServiceImpl(lwbMailSender, supportMail, recipientRetriever);
	}

	@Bean
	RecipientRetriever recipientRetriever(SecurityDao securityDao) {
		return new RecipientRetriever(securityDao);
	}

	@Bean
	MailSender lwbMailSender(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
		return new MailSenderImpl(javaMailSender, templateEngine);
	}

	@Bean
	UserMailNotificationService userMailNotificationService(MailService mailService,
			SupportSettings supportSettings) {
		return createUserMailNotificationService(mailService, supportSettings);
	}

	private MailNotificationServiceImpl createUserMailNotificationService(MailService mailService,
			SupportSettings supportSettings) {
		return new MailNotificationServiceImpl(mailService, supportSettings);
	}

}
