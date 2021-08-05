package com.custodix.insite.local.user.infra.eventcontrollers;

import org.springframework.stereotype.Component;

import com.custodix.insite.local.user.application.api.UnlockAccount;
import com.custodix.insite.local.user.application.api.UnlockAccount.Request;
import com.custodix.insite.local.user.domain.events.PasswordRecoveredEvent;

import eu.ehr4cr.workbench.local.security.annotation.RunAsSystemUser;

@Component
class PasswordRecoveredEventController {
	private final UnlockAccount unlockAccount;

	PasswordRecoveredEventController(UnlockAccount unlockAccount) {
		this.unlockAccount = unlockAccount;
	}

	@RunAsSystemUser
	void handle(PasswordRecoveredEvent event) {
		Request request = Request.newBuilder()
				.withEmail(event.getEmail())
				.build();
		unlockAccount.unlockAccount(request);
	}
}
