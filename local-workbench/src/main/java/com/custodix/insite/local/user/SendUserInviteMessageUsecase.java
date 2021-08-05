package com.custodix.insite.local.user;

import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.properties.AccountSecuritySettings;
import eu.ehr4cr.workbench.local.service.email.MailService;
import eu.ehr4cr.workbench.local.service.email.model.ImmutableUserInviteMailContent;
import eu.ehr4cr.workbench.local.service.email.model.UserInviteMailContent;

@Component
class SendUserInviteMessageUsecase implements SendUserInviteMessage {
	private final MailService mailService;
	private final AccountSecuritySettings accountSecuritySettings;

	SendUserInviteMessageUsecase(MailService mailService, AccountSecuritySettings accountSecuritySettings) {
		this.mailService = mailService;
		this.accountSecuritySettings = accountSecuritySettings;
	}

	@Override
	public void send(Request request) {
		User user = request.getUser();
		UserInviteMailContent mailContent = ImmutableUserInviteMailContent.builder()
				.inviteExpireValue(accountSecuritySettings.getInviteExpireValue())
				.inviteExpireUnit(accountSecuritySettings.getInviteExpireUnit())
				.inviteAcceptUrl(buildInviteAcceptUrl(user))
				.build();
		mailService.sendMail(mailContent, user.getEmail());
	}

	private String buildInviteAcceptUrl(User user) {
		return accountSecuritySettings.getInvitationUri(user.getId(), user.getTempPassword());
	}
}
