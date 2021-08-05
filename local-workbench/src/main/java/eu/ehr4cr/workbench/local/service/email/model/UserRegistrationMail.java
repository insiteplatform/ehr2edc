package eu.ehr4cr.workbench.local.service.email.model;

import org.thymeleaf.context.Context;

import java.util.List;

class UserRegistrationMail extends Mail {
	private static final String TEMPLATE_NAME = "userRegistrationMessage";
	private static final String SUBJECT = "A new user registered on the InSite Clinical Workbench";

	private final UserRegistrationMailContent content;

	UserRegistrationMail(String senderAddress, List<String> recipientAddresses, UserRegistrationMailContent content) {
		super(senderAddress, recipientAddresses);
		this.content = content;
	}

	@Override
	public String getSubject() {
		return SUBJECT;
	}

	@Override
	public Context getContext() {
		Context ctx = new Context();
		ctx.setVariable("messageTemplate", TEMPLATE_NAME);
		ctx.setVariable("mailInfo_subject", SUBJECT);
		ctx.setVariable("user_name", content.getUsername());
		ctx.setVariable("accept_url", content.getAcceptUrl());
		return ctx;
	}
}
