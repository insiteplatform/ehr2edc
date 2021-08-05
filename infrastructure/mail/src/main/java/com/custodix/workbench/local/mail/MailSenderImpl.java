package com.custodix.workbench.local.mail;

import eu.ehr4cr.workbench.local.service.email.model.Mail;
import eu.ehr4cr.workbench.local.service.email.model.MailException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

class MailSenderImpl implements MailSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailSenderImpl.class);
	private static final String BASE_EMAIL_RESOURCE_PATH = "messages/email";
	private static final String EMAIL_LOGO = "classpath:/assets/global/img/insite-trinetx-logo-small.png";
	private final JavaMailSender javaMailSender;
	private final TemplateEngine thymeleaf;

	MailSenderImpl(JavaMailSender javaMailSender, TemplateEngine thymeleaf) {
		this.javaMailSender = javaMailSender;
		this.thymeleaf = thymeleaf;
	}

	@Override
	public void sendMail(Mail mail) {
		MimeMessage msg = javaMailSender.createMimeMessage();
		try {
			configureMessage(msg, mail);
			javaMailSender.send(msg);
		} catch (MessagingException e) {
			LOGGER.error(String.format("Sending e-mail %s from %s to %s failed.", mail.getClass()
					.getName(), mail.getSenderAddress(), mail.getRecipientAddresses()));
			throw new MailException(e);
		}
	}

	private void configureMessage(MimeMessage msg, Mail mail) throws MessagingException {
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		helper.setFrom(mail.getSenderAddress());
		helper.setBcc(getRecipients(mail));
		helper.setSubject(mail.getSubject());
		helper.setText(getEmailContent(mail), true);
		helper.addInline("<heading-img>", getLogo(), "image/png");
	}

	private String[] getRecipients(Mail mail) {
		return mail.getRecipientAddresses()
				.toArray(new String[] {});
	}

	private String getEmailContent(Mail mail) {
		return StringEscapeUtils.unescapeHtml4(thymeleaf.process(BASE_EMAIL_RESOURCE_PATH, mail.getContext()));
	}

	private Resource getLogo() {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		return resourceLoader.getResource(EMAIL_LOGO);
	}
}
