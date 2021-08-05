package eu.ehr4cr.workbench.local.service.email.model;

import org.thymeleaf.context.Context;

import java.util.List;

class UserAcceptMail extends Mail {
	private static final String TEMPLATE_NAME = "userAcceptMessage";
	private static final String SUBJECT = "Your user account to the InSite platform has been approved";

	private final UserAcceptMailContent content;

	UserAcceptMail(String senderAddress, List<String> recipientAddresses, UserAcceptMailContent content) {
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
		ctx.setVariable("accept_expireValue", content.getAcceptExpireValue());
		ctx.setVariable("accept_expireUnit", content.getAcceptExpireUnit().toString()
				.toLowerCase());
		ctx.setVariable("accept_acceptUrl", content.getAcceptAcceptUrl());
		return ctx;
	}



}
