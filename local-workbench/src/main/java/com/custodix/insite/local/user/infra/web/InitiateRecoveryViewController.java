package com.custodix.insite.local.user.infra.web;

import org.springframework.web.bind.annotation.GetMapping;

import com.custodix.insite.local.shared.annotations.ViewController;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.WebViews;
import eu.ehr4cr.workbench.local.controllers.BaseController;

@ViewController
class InitiateRecoveryViewController extends BaseController {
	@GetMapping(WebRoutes.recoverAccount)
	protected String doGet() {
		return WebViews.recoverAccount;
	}
}
