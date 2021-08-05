package com.custodix.insite.local.user.application.command;

import org.springframework.validation.annotation.Validated;

import com.custodix.insite.local.user.application.api.UpdatePassword;

import com.custodix.insite.local.shared.annotations.Command;
import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.annotation.HasPermissionForAccount;

@Validated
@Command
class UpdatePasswordCommand implements UpdatePassword {
	private final SecurityDao securityDao;

	UpdatePasswordCommand(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	@HasPermissionForAccount
	@Override
	public void update(Request request) {
		User user = securityDao.findUserById(request.getUserIdentifier());
		user.changePassword(request.getOldPassword(), request.getNewPassword());
		securityDao.save(user);
	}
}
