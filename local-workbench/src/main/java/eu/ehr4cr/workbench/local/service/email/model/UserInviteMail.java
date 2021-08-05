package eu.ehr4cr.workbench.local.service.email.model;

import org.thymeleaf.context.Context;

import java.util.List;

class UserInviteMail extends Mail {
	private static final String TEMPLATE_NAME = "userInviteMessage";
	private static final String SUBJECT = "Invite to the InSite platform";

	private final UserInviteMailContent content;

	UserInviteMail(String senderAddress, List<String> recipientAddresses, UserInviteMailContent content) {
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
		ctx.setVariable("invite_expireValue", content.getInviteExpireValue());
		ctx.setVariable("invite_expireUnit", content.getInviteExpireUnit().toString()
				.toLowerCase());
		ctx.setVariable("invite_acceptUrl", content.getInviteAcceptUrl());
		return ctx;
	}
}
