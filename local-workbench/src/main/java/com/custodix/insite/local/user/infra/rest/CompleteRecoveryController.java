package com.custodix.insite.local.user.infra.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.user.application.api.CompleteRecovery;
import com.custodix.insite.local.user.application.api.CompleteRecovery.Request;
import com.custodix.insite.local.user.infra.rest.model.CompleteRecoveryRequest;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.BaseController;

@RestController
class CompleteRecoveryController extends BaseController {
	private final CompleteRecovery completeRecovery;

	CompleteRecoveryController(CompleteRecovery completeRecovery) {
		this.completeRecovery = completeRecovery;
	}

	@PostMapping(WebRoutes.completeRecovery)
	protected void doPost(@RequestBody CompleteRecoveryRequest request, @RequestParam("password") String tempPassword) {
		Request recoverRequest = Request.newBuilder()
				.withUserId(request.getUserId())
				.withPassword(request.getPassword())
				.withTempPassword(tempPassword)
				.build();
		completeRecovery.completeRecovery(recoverRequest);
	}
}
