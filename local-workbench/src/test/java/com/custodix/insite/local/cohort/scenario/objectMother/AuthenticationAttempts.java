package com.custodix.insite.local.cohort.scenario.objectMother;

import static eu.ehr4cr.workbench.local.service.DomainTime.now;

import com.custodix.insite.local.user.domain.AuthenticationAttempt;
import com.custodix.insite.local.user.domain.AuthenticationAttemptResult;
import com.custodix.insite.local.user.domain.repository.AuthenticationAttemptRepository;
import com.custodix.insite.local.user.vocabulary.AuthenticationLockSettings;
import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.service.TestTimeService;

public class AuthenticationAttempts {
	private final AuthenticationAttemptRepository authenticationAttemptRepository;
	private final AuthenticationLockSettings authenticationLockSettings;
	private final TestTimeService testTimeService;

	public AuthenticationAttempts(AuthenticationAttemptRepository authenticationAttemptRepository,
			AuthenticationLockSettings authenticationLockSettings, TestTimeService testTimeService) {
		this.authenticationAttemptRepository = authenticationAttemptRepository;
		this.authenticationLockSettings = authenticationLockSettings;
		this.testTimeService = testTimeService;
	}

	public void setOneAttemptLeft(Email email) {
		testTimeService.timeTravelToThePast(authenticationLockSettings.getAttemptsInterval().minusMinutes(1L));
		saveBadCredentialsAttempts(email, authenticationLockSettings.getAttemptsMax() - 1);
		testTimeService.reset();
	}

	public void setOneAttemptLeftWithSufficientDelay(Email email) {
		testTimeService.timeTravelToThePast(authenticationLockSettings.getAttemptsInterval());
		saveBadCredentialsAttempts(email, authenticationLockSettings.getAttemptsMax() - 1);
		testTimeService.reset();
	}

	public void setLocked(Email email) {
		testTimeService.timeTravelToThePast(authenticationLockSettings.getInterval().minusMinutes(1L));
		saveBadCredentialsAttempts(email, authenticationLockSettings.getAttemptsMax());
		testTimeService.reset();
	}

	public void setLockedForAsLongAsLockInterval(Email email) {
		testTimeService.timeTravelToThePast(authenticationLockSettings.getInterval());
		saveBadCredentialsAttempts(email, authenticationLockSettings.getAttemptsMax());
		testTimeService.reset();
	}

	public void unlock(Email email) {
		saveAuthenticationAttempt(email, AuthenticationAttemptResult.UNLOCKED);
	}

	private void saveBadCredentialsAttempts(Email email, int count) {
		for (int i = 0; i < count; i++) {
			saveAuthenticationAttempt(email, AuthenticationAttemptResult.BAD_CREDENTIALS);
		}
	}

	private void saveAuthenticationAttempt(Email email, AuthenticationAttemptResult result) {
		AuthenticationAttempt attempt = AuthenticationAttempt.newBuilder()
				.withEmail(email)
				.withResult(result)
				.withTimestamp(now())
				.build();
		authenticationAttemptRepository.save(attempt);
	}
}
