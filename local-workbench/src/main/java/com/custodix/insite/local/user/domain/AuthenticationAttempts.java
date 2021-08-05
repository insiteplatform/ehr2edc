package com.custodix.insite.local.user.domain;

import static com.custodix.insite.local.user.domain.AuthenticationAttemptResult.BAD_CREDENTIALS;
import static eu.ehr4cr.workbench.local.service.DomainTime.now;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.custodix.insite.local.user.vocabulary.AuthenticationLockSettings;

import eu.ehr4cr.workbench.local.exception.SystemException;

public final class AuthenticationAttempts {
	private final List<AuthenticationAttempt> attempts;
	private final AuthenticationLockSettings authenticationLockSettings;

	public AuthenticationAttempts(List<AuthenticationAttempt> authenticationAttempts,
			AuthenticationLockSettings authenticationLockSettings) {
		this.attempts = authenticationAttempts;
		this.authenticationLockSettings = authenticationLockSettings;
	}

	public boolean isAccountLocked() {
		List<AuthenticationAttempt> badCredentialAttempts = getBadCredentialAttemptsSinceLastSuccess();
		return !badCredentialAttempts.isEmpty() && isMostRecentAttemptWithinLockInterval(badCredentialAttempts)
				&& hasMaxAttemptsBeenReachedWithinAttemptInterval(badCredentialAttempts);
	}

	public int getCurrentAttempt() {
		List<AuthenticationAttempt> badCredentialAttempts = getBadCredentialAttemptsSinceLastSuccess();
		return getAttemptsWithinAttemptInterval(badCredentialAttempts).size();
	}

	public Date getUnlockDate() {
		List<AuthenticationAttempt> badCredentialAttempts = getBadCredentialAttemptsSinceLastSuccess();
		return Date.from(getMostRecentAttempt(badCredentialAttempts).getTimestamp()
				.toInstant()
				.plus(authenticationLockSettings.getInterval()));
	}

	private List<AuthenticationAttempt> getBadCredentialAttemptsSinceLastSuccess() {
		List<AuthenticationAttempt> attemptsSinceLastSuccess = getAttemptsSinceLastSuccess();
		return attemptsSinceLastSuccess.stream()
				.filter(a -> BAD_CREDENTIALS == a.getResult())
				.collect(Collectors.toList());
	}

	private List<AuthenticationAttempt> getAttemptsSinceLastSuccess() {
		Optional<AuthenticationAttempt> lastSuccessAttemptOptional = getLastSuccess();
		return lastSuccessAttemptOptional.map(authenticationAttempt -> attempts.stream()
				.filter(a -> a.isAfter(authenticationAttempt))
				.collect(Collectors.toList()))
				.orElse(attempts);
	}

	private Optional<AuthenticationAttempt> getLastSuccess() {
		return attempts.stream()
				.filter(AuthenticationAttempt::isSuccessful)
				.max(Comparator.comparing(AuthenticationAttempt::getTimestamp));
	}

	private boolean isMostRecentAttemptWithinLockInterval(List<AuthenticationAttempt> badCredentialAttempts) {
		AuthenticationAttempt mostRecentAttempt = getMostRecentAttempt(badCredentialAttempts);
		return mostRecentAttempt.isAfter(getLockIntervalStart());
	}

	private boolean hasMaxAttemptsBeenReachedWithinAttemptInterval(List<AuthenticationAttempt> badCredentialAttempts) {
		return getAttemptsWithinAttemptInterval(badCredentialAttempts).size()
				>= authenticationLockSettings.getAttemptsMax();
	}

	private List<AuthenticationAttempt> getAttemptsWithinAttemptInterval(
			List<AuthenticationAttempt> badCredentialAttempts) {
		return badCredentialAttempts.stream()
				.filter(a -> a.getDurationBetween(getMostRecentAttempt(badCredentialAttempts))
						.compareTo(authenticationLockSettings.getAttemptsInterval()) < 0)
				.collect(Collectors.toList());
	}

	private AuthenticationAttempt getMostRecentAttempt(List<AuthenticationAttempt> badCredentialAttempts) {
		return badCredentialAttempts.stream()
				.max(Comparator.comparing(AuthenticationAttempt::getTimestamp))
				.orElseThrow(() -> new SystemException("No bad credentials attempt found"));
	}

	private Date getLockIntervalStart() {
		return Date.from(now().toInstant()
				.minus(authenticationLockSettings.getInterval()));
	}
}
