package com.custodix.insite.local.user.application.command;

import com.custodix.insite.local.shared.annotations.Command;
import com.custodix.insite.local.user.application.api.UpdatePasswordsStatus;
import com.custodix.insite.local.user.vocabulary.PasswordExpirySettings;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.security.User;

@Command
class UpdatePasswordsStatusCommand implements UpdatePasswordsStatus {
	private final SecurityDao securityDao;
	private final PasswordExpirySettings passwordExpirySettings;

	UpdatePasswordsStatusCommand(SecurityDao securityDao, PasswordExpirySettings passwordExpirySettings) {
		this.securityDao = securityDao;
		this.passwordExpirySettings = passwordExpirySettings;
	}

	@Override
	public void updatePasswordsStatus() {
		securityDao.findAllUsers()
				.forEach(this::updatePasswordStatus);
	}

	private void updatePasswordStatus(User user) {
		user.updatePasswordStatus(passwordExpirySettings);
		securityDao.save(user);
	}
}
