package com.custodix.insite.local.user.application.command;

import static com.custodix.insite.local.user.domain.AuthenticationAttemptResult.UNLOCKED;
import static eu.ehr4cr.workbench.local.global.AuthorityType.MANAGE_ACCOUNTS;
import static eu.ehr4cr.workbench.local.service.DomainTime.now;

import org.springframework.validation.annotation.Validated;

import com.custodix.insite.local.shared.annotations.Command;
import com.custodix.insite.local.user.application.api.UnlockAccount;
import com.custodix.insite.local.user.domain.AuthenticationAttempt;
import com.custodix.insite.local.user.domain.repository.AuthenticationAttemptRepository;

import eu.ehr4cr.workbench.local.security.annotation.HasPermission;

@Validated
@Command
class UnlockAccountCommand implements UnlockAccount {
	private final AuthenticationAttemptRepository authenticationAttemptRepository;

	UnlockAccountCommand(AuthenticationAttemptRepository authenticationAttemptRepository) {
		this.authenticationAttemptRepository = authenticationAttemptRepository;
	}

	@HasPermission(MANAGE_ACCOUNTS)
	@Override
	public void unlockAccount(Request request) {
		AuthenticationAttempt authenticationAttempt = AuthenticationAttempt.newBuilder()
				.withEmail(request.getEmail())
				.withResult(UNLOCKED)
				.withTimestamp(now())
				.build();
		authenticationAttemptRepository.save(authenticationAttempt);
	}
}
