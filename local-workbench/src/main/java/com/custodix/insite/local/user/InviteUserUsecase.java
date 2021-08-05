package com.custodix.insite.local.user;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.eventhandlers.user.UserInvitedEvent;
import eu.ehr4cr.workbench.local.eventpublisher.EventPublisher;
import eu.ehr4cr.workbench.local.exception.security.InvitedUserExistsException;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.model.security.UserRole;
import eu.ehr4cr.workbench.local.properties.AccountSecuritySettings;
import eu.ehr4cr.workbench.local.security.annotation.HasPermission;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Component
class InviteUserUsecase implements InviteUser {
	private final SecurityDao securityDao;
	private final AccountSecuritySettings accountSecuritySettings;
	private final EventPublisher eventPublisher;

	InviteUserUsecase(SecurityDao securityDao, AccountSecuritySettings accountSecuritySettings,
			EventPublisher eventPublisher) {
		this.securityDao = securityDao;
		this.accountSecuritySettings = accountSecuritySettings;
		this.eventPublisher = eventPublisher;
	}

	@HasPermission(AuthorityType.MANAGE_ACCOUNTS)
	@Transactional
	@Override
	public void invite(Request request) {
		if (securityDao.userAlreadyExists(request.getEmail(), request.getUsername())) {
			throw new InvitedUserExistsException();
		} else {
			User user = new User(request.getUsername(), request.getEmail(), getUserGroups(request));
			user.invite(accountSecuritySettings.getInviteExpireValue(), accountSecuritySettings.getInviteExpireUnit());
			securityDao.persist(user);
			eventPublisher.publishEvent(new UserInvitedEvent(user));
		}
	}

	private Set<Group> getUserGroups(Request request) {
		Set<Group> groups = request.getRoles()
				.stream()
				.map(UserRole::getGroupType)
				.map(securityDao::findGroup)
				.collect(Collectors.toSet());
		groups.add(securityDao.findGroup(GroupType.USR));
		return groups;
	}
}
