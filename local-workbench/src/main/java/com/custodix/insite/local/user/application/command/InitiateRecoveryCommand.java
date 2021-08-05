package com.custodix.insite.local.user.application.command;

import static eu.ehr4cr.workbench.local.global.AuthorityType.MANAGE_ACCOUNTS;

import org.springframework.validation.annotation.Validated;

import com.custodix.insite.local.shared.annotations.Command;
import com.custodix.insite.local.shared.exceptions.UserException;
import com.custodix.insite.local.user.application.api.InitiateRecovery;
import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.properties.AccountSecuritySettings;
import eu.ehr4cr.workbench.local.security.annotation.HasPermission;
import eu.ehr4cr.workbench.local.service.email.MailService;
import eu.ehr4cr.workbench.local.service.email.model.ImmutableUserRecoverMailContent;
import eu.ehr4cr.workbench.local.service.email.model.UserRecoverMailContent;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

@Validated
@Command
class InitiateRecoveryCommand implements InitiateRecovery {
	private static final String RECOVERY_UNAVAILABLE = "Recovery unavailable for this account";
	private final SecurityDao securityDao;
	private final MailService mailService;
	private final AccountSecuritySettings accountSecuritySettings;

	InitiateRecoveryCommand(SecurityDao securityDao, MailService mailService,
			AccountSecuritySettings accountSecuritySettings) {
		this.securityDao = securityDao;
		this.mailService = mailService;
		this.accountSecuritySettings = accountSecuritySettings;
	}

	@Override
	public void initiateBySecurityQuestion(BySecurityQuestionRequest request) {
		User user = getUser(request.getEmail());
		user.performSecurityQuestionRecovery(accountSecuritySettings.getInviteExpireValue(),
				accountSecuritySettings.getInviteExpireUnit(), request.getQuestionAnswer(), () -> sendMail(user));
		securityDao.save(user);
	}

	@HasPermission(MANAGE_ACCOUNTS)
	@Override
	public void initiateByAdmin(ByAdminRequest request) {
		User user = getUser(request.getUserIdentifier());
		user.performRecovery(accountSecuritySettings.getInviteExpireValue(),
				accountSecuritySettings.getInviteExpireUnit(), () -> sendMail(user));
		securityDao.save(user);
	}

	private void sendMail(User user) {
		UserRecoverMailContent mailContent = ImmutableUserRecoverMailContent.builder()
				.recoverExpireValue(accountSecuritySettings.getInviteExpireValue())
				.recoverExpireUnit(accountSecuritySettings.getInviteExpireUnit())
				.recoverAcceptUrl(accountSecuritySettings.getRecoveryUri(user.getId(), user.getTempPassword()))
				.build();
		mailService.sendMail(mailContent, user.getEmail());
	}

	private User getUser(Email email) {
		return securityDao.findUserByEmail(email)
				.orElseThrow(() -> new UserException(RECOVERY_UNAVAILABLE));
	}

	private User getUser(UserIdentifier userIdentifier) {
		return securityDao.findUserByIdentifier(userIdentifier)
				.orElseThrow(() -> new UserException(RECOVERY_UNAVAILABLE));
	}

}