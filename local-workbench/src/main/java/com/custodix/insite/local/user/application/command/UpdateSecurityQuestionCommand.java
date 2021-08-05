package com.custodix.insite.local.user.application.command;

import org.springframework.validation.annotation.Validated;

import com.custodix.insite.local.shared.annotations.Command;
import com.custodix.insite.local.user.application.api.UpdateSecurityQuestion;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.annotation.HasPermissionForAccount;

@Validated
@Command
class UpdateSecurityQuestionCommand implements UpdateSecurityQuestion {
	private final SecurityDao securityDao;

	UpdateSecurityQuestionCommand(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	@HasPermissionForAccount
	@Override
	public void updateSecurityQuestion(Request request) {
		User user = securityDao.findUserById(request.getUserIdentifier());
		user.updateSecurityQuestion(request.getQuestionId(), request.getQuestionAnswer());
		securityDao.save(user);
	}
}
