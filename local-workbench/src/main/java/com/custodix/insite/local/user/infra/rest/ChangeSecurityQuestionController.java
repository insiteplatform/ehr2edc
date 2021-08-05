package com.custodix.insite.local.user.infra.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.user.application.api.UpdateSecurityQuestion;
import com.custodix.insite.local.user.application.api.UpdateSecurityQuestion.Request;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.controllers.api.security.dto.ChangeSecurityQuestionRequest;

@RestController
class ChangeSecurityQuestionController extends BaseController {
	private final UpdateSecurityQuestion updateSecurityQuestion;

	ChangeSecurityQuestionController(UpdateSecurityQuestion updateSecurityQuestion) {
		this.updateSecurityQuestion = updateSecurityQuestion;
	}

	@PostMapping(WebRoutes.accountQuestion)
	protected void doPost(@RequestBody ChangeSecurityQuestionRequest webRequest) {
		Request request = Request.newBuilder()
				.withUserIdentifier(getUser().getIdentifier())
				.withQuestionId(webRequest.getSecurityQuestionId())
				.withQuestionAnswer(webRequest.getSecurityAnswer())
				.build();
		updateSecurityQuestion.updateSecurityQuestion(request);
	}
}
