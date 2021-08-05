package com.custodix.insite.local.user;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.service.IUserMgrService;

@Component
class GetUserForRecoveryUsecase implements GetUserForRecovery {
	private final IUserMgrService userService;

	GetUserForRecoveryUsecase(IUserMgrService userService) {
		this.userService = userService;
	}

	@Transactional(readOnly = true)
	@Override
	public Response getUser(Request request) {
		User user = userService.findUserById(request.getUserId());
		validateUserState(user, request.getTempPassword());
		return Response.newBuilder()
				.withUserId(user.getId())
				.withUserEmail(user.getEmail())
				.withUserExpired(user.isExpired())
				.build();
	}

	private void validateUserState(User user, String tempPassword) {
		if (user != null && user.getTempPassword() == null) {
			throw new RecoveryAlreadyCompletedException();
		} else if (user == null || !tempPassword.equals(user.getTempPassword())) {
			throw new RecoveryCompletionInvalidException();
		}
	}
}
