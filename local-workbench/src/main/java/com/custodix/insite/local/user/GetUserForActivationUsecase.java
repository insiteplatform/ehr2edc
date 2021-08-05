package com.custodix.insite.local.user;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.ehr4cr.workbench.local.model.security.Question;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.service.ISecurityQuestionService;
import eu.ehr4cr.workbench.local.service.IUserMgrService;

@Component
class GetUserForActivationUsecase implements GetUserForActivation {
	private final IUserMgrService userService;
	private final ISecurityQuestionService securityQuestionService;

	GetUserForActivationUsecase(IUserMgrService userService,
			ISecurityQuestionService securityQuestionService) {
		this.userService = userService;
		this.securityQuestionService = securityQuestionService;
	}

	@Transactional(readOnly = true)
	@Override
	public Response getUser(Request request) {
		User user = userService.findUserById(request.getUserId());
		validateUserState(user, request.getTempPassword());
		List<Question> questions = securityQuestionService.getQuestionsForLocale(request.getLocale());
		return Response.newBuilder()
				.withUserId(user.getId())
				.withUserEmail(user.getEmail())
				.withUserExpired(user.isExpired())
				.withSecurityQuestions(questions)
				.build();
	}

	private void validateUserState(User user, String tempPassword) {
		if (user != null && user.getTempPassword() == null) {
			throw new InvitationAlreadyCompletedException();
		} else if (user == null || !tempPassword.equals(user.getTempPassword())) {
			throw new InvitationCompletionInvalidException();
		}
	}
}
