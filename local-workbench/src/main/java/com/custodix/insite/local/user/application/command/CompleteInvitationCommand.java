package com.custodix.insite.local.user.application.command;

import org.springframework.validation.annotation.Validated;

import com.custodix.insite.local.shared.annotations.Command;
import com.custodix.insite.local.user.application.api.CompleteInvitation;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.security.User;

@Validated
@Command
class CompleteInvitationCommand implements CompleteInvitation {
	private final SecurityDao securityDao;

	CompleteInvitationCommand(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	@Override
	public void completeInvitation(Request request) {
		User user = securityDao.findUserById(request.getUserId());
		user.activate(request.getPassword(), request.getTempPassword());
		user.updateSecurityQuestion(request.getSecurityQuestionId(), request.getSecurityQuestionAnswer());
		securityDao.save(user);
	}
}
