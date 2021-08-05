package eu.ehr4cr.workbench.local.service.email.model;

import java.util.List;

import org.thymeleaf.context.Context;

class ImminentlyExpiringPasswordMail extends Mail {
	private static final String TEMPLATE_NAME = "imminentlyExpiringPasswordMessage";
	private static final String SUBJECT = "Your password will expire soon";

	private final ImminentlyExpiringPasswordMailContent content;

	ImminentlyExpiringPasswordMail(String senderAddress, List<String> recipientAddresses,
			ImminentlyExpiringPasswordMailContent content) {
		super(senderAddress, recipientAddresses);
		this.content = content;
	}

	@Override
	public String getSubject() {
		return "[InSite] " + SUBJECT;
	}

	@Override
	public Context getContext() {
		Context ctx = new Context();
		ctx.setVariable("messageTemplate", TEMPLATE_NAME);
		ctx.setVariable("mailInfo_subject", SUBJECT);
		ctx.setVariable("expiration_date", content.getExpirationDate());
		ctx.setVariable("change_password_url", content.getChangePasswordUrl());
		return ctx;
	}
}
