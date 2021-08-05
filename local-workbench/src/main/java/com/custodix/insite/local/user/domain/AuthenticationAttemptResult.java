package com.custodix.insite.local.user.domain;

import java.util.Arrays;

import com.custodix.insite.local.user.vocabulary.AuthenticateResult;

import eu.ehr4cr.workbench.local.exception.SystemException;

public enum AuthenticationAttemptResult {
	SUCCESS(AuthenticateResult.SUCCESS, true),
	UNLOCKED(null, true),
	BAD_CREDENTIALS(AuthenticateResult.BAD_CREDENTIALS, false),
	PASSWORD_EXPIRED(AuthenticateResult.PASSWORD_EXPIRED, false),
	ACCOUNT_LOCKED(AuthenticateResult.ACCOUNT_LOCKED, false);

	private final AuthenticateResult authenticateResult;
	private final boolean successState;

	AuthenticationAttemptResult(AuthenticateResult authenticateResult, boolean successState) {
		this.authenticateResult = authenticateResult;
		this.successState = successState;
	}

	public static AuthenticationAttemptResult of(AuthenticateResult authenticateResult) {
		return Arrays.stream(values())
				.filter(e -> e.matches(authenticateResult))
				.findFirst()
				.orElseThrow(() -> new SystemException("Unrecognized authenticate result " + authenticateResult));
	}

	public boolean isSuccessState() {
		return successState;
	}

	private boolean matches(AuthenticateResult authenticateResult) {
		return this.authenticateResult == authenticateResult;
	}
}
