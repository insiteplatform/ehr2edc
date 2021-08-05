package com.custodix.insite.local.user;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.exception.InvalidRequestException;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.usecases.checkauthority.CheckUserHasAuthority;
import eu.ehr4cr.workbench.local.usecases.checkuserpermissions.CheckUserHasPermissionForAccount;

@Component
class UpdateProfileUsecase implements UpdateProfile {
	private final SecurityDao securityDao;
	private final CheckUserHasPermissionForAccount checkUserHasPermissionForAccount;
	private final CheckUserHasAuthority checkUserHasAuthority;

	UpdateProfileUsecase(SecurityDao securityDao, CheckUserHasPermissionForAccount checkUserHasPermissionForAccount,
			CheckUserHasAuthority checkUserHasAuthority) {
		this.securityDao = securityDao;
		this.checkUserHasPermissionForAccount = checkUserHasPermissionForAccount;
		this.checkUserHasAuthority = checkUserHasAuthority;
	}

	@Transactional
	@Override
	public void update(Request request) {
		validateRequest(request);
		User user = securityDao.findUserById(request.getUserIdentifier());
		if (!request.getEmail().equals(user.getEmail())) {
			updateEmail(user, request.getEmail());
		}
		if (!request.getUsername().equals(user.getUsername())) {
			updateUsername(user, request.getUsername());
		}
		securityDao.merge(user);
	}

	private void updateEmail(User user, String email) {
		if (checkUserHasAuthority.check(AuthorityType.MANAGE_ACCOUNTS)) {
			user.setEmail(email);
		} else {
			throw new AccessDeniedException("User does not have access to update email");
		}
	}

	private void updateUsername(User user, String username) {
		if (checkUserHasPermissionForAccount.check(user.getIdentifier())) {
			user.setUsername(username);
		} else {
			throw new AccessDeniedException("User does not have access to update username");
		}
	}

	private void validateRequest(Request request) {
		if (!EmailValidator.getInstance()
				.isValid(request.getEmail())) {
			throw new InvalidRequestException("Email address invalid");
		}
		if (StringUtils.isBlank(request.getUsername())) {
			throw new InvalidRequestException("Username invalid");
		}
	}
}
