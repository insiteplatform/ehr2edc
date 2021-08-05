package com.custodix.insite.local.user.application.command;

import org.springframework.validation.annotation.Validated;

import com.custodix.insite.local.shared.annotations.Command;
import com.custodix.insite.local.user.application.api.CompleteRecovery;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.security.User;

@Validated
@Command
class CompleteRecoveryCommand implements CompleteRecovery {
	private final SecurityDao securityDao;

	CompleteRecoveryCommand(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	@Override
	public void completeRecovery(Request request) {
		User user = securityDao.findUserById(request.getUserId());
		user.recover(request.getPassword(), request.getTempPassword());
		securityDao.save(user);
	}
}
