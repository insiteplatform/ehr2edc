package com.custodix.insite.local.user.infra.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.user.application.api.InitiateRecovery;
import com.custodix.insite.local.user.application.api.InitiateRecovery.ByAdminRequest;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

@RestController
class InitiateRecoveryByAdminController extends BaseController {
	private final InitiateRecovery initiateRecovery;

	InitiateRecoveryByAdminController(InitiateRecovery initiateRecovery) {
		this.initiateRecovery = initiateRecovery;
	}

	@PostMapping(WebRoutes.recoverMember)
	public void doPost(@RequestParam long userId) {
		ByAdminRequest request = ByAdminRequest.newBuilder()
				.withUserIdentifier(UserIdentifier.of(userId))
				.build();
		initiateRecovery.initiateByAdmin(request);
	}
}
