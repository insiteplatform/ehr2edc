package com.custodix.insite.local.user.infra.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.user.application.api.CompleteInvitation;
import com.custodix.insite.local.user.application.api.CompleteInvitation.Request;
import com.custodix.insite.local.user.infra.rest.model.CompleteInvitationRequest;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.BaseController;

@RestController
class CompleteInvitationController extends BaseController {
	private final CompleteInvitation completeInvitation;

	CompleteInvitationController(CompleteInvitation completeInvitation) {
		this.completeInvitation = completeInvitation;
	}

	@PostMapping(WebRoutes.completeInvitation)
	protected void doPost(@RequestBody CompleteInvitationRequest request, @RequestParam("password") String tempPassword) {
		Request acceptRequest = Request.newBuilder()
				.withUserId(request.getUserId())
				.withPassword(request.getPassword())
				.withTempPassword(tempPassword)
				.withSecurityQuestionId(request.getSecurityQuestionId())
				.withSecurityQuestionAnswer(request.getSecurityQuestionAnswer())
				.build();
		completeInvitation.completeInvitation(acceptRequest);
	}
}
