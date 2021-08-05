package com.custodix.insite.local.user;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.eventhandlers.user.UserInvitedEvent;
import eu.ehr4cr.workbench.local.eventpublisher.EventPublisher;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.properties.AccountSecuritySettings;
import eu.ehr4cr.workbench.local.security.annotation.HasPermission;

@Component
class ReinviteUserUsecase implements ReinviteUser {
	private final SecurityDao securityDao;
	private final EventPublisher eventPublisher;
	private final AccountSecuritySettings accountSecuritySettings;

	ReinviteUserUsecase(SecurityDao securityDao, EventPublisher eventPublisher,
			AccountSecuritySettings accountSecuritySettings) {
		this.securityDao = securityDao;
		this.eventPublisher = eventPublisher;
		this.accountSecuritySettings = accountSecuritySettings;
	}

	@HasPermission(AuthorityType.MANAGE_ACCOUNTS)
	@Transactional
	@Override
	public void reinvite(Request request) {
		User user = securityDao.findUserById(request.getUserId());
		user.reinvite(accountSecuritySettings.getInviteExpireValue(), accountSecuritySettings.getInviteExpireUnit());
		securityDao.merge(user);
		eventPublisher.publishEvent(new UserInvitedEvent(user));
	}
}
