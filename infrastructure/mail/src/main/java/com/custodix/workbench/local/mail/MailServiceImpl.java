package com.custodix.workbench.local.mail;

import java.util.Collections;
import java.util.List;

import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.service.email.MailService;
import eu.ehr4cr.workbench.local.service.email.model.Mail;
import eu.ehr4cr.workbench.local.service.email.model.MailContent;

class MailServiceImpl implements MailService {
	private final MailSender mailSender;
	private final String supportMail;
	private final RecipientRetriever recipientRetriever;

	MailServiceImpl(MailSender mailSender, String supportMail, RecipientRetriever recipientRetriever) {
		this.mailSender = mailSender;
		this.supportMail = supportMail;
		this.recipientRetriever = recipientRetriever;
	}

	@Override
	public void sendMail(MailContent content, String recipientAddress) {
		Mail mail = content.createMail(supportMail, Collections.singletonList(recipientAddress));
		mailSender.sendMail(mail);
	}

	@Override
	public void sendMail(MailContent content, GroupType recipientType) {
		List<String> recipientAddresses = recipientRetriever.findRecipientEmailAddresses(recipientType);
		Mail mail = content.createMail(supportMail, recipientAddresses);
		mailSender.sendMail(mail);
	}

}
