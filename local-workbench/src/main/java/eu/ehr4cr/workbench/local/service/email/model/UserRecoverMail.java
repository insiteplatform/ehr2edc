package eu.ehr4cr.workbench.local.service.email.model;

import org.thymeleaf.context.Context;

import java.util.List;

class UserRecoverMail extends Mail {
	private static final String TEMPLATE_NAME = "userRecoverMessage";
	private static final String SUBJECT = "User recovery for the InSite platform";

	private final UserRecoverMailContent content;

	UserRecoverMail(String senderAddress, List<String> recipientAddresses, UserRecoverMailContent content) {
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
		ctx.setVariable("recover_expireValue", content.getRecoverExpireValue());
		ctx.setVariable("recover_expireUnit", content.getRecoverExpireUnit().toString()
				.toLowerCase());
		ctx.setVariable("recover_acceptUrl", content.getRecoverAcceptUrl());
		return ctx;
	}
}
