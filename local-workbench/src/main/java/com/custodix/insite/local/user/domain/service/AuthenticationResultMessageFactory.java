package com.custodix.insite.local.user.domain.service;

import org.springframework.stereotype.Service;

import com.custodix.insite.local.user.domain.repository.AuthenticationAttemptRepository;
import com.custodix.insite.local.user.vocabulary.AuthenticationLockSettings;
import com.custodix.insite.local.user.vocabulary.AuthenticateResult;
import com.custodix.insite.local.user.vocabulary.Email;
import com.custodix.insite.local.user.vocabulary.validation.Message;

import eu.ehr4cr.workbench.local.exception.SystemException;

@Service
public class AuthenticationResultMessageFactory {

	private final AuthenticationLockSettings authenticationLockSettings;
	private final AuthenticationAttemptRepository authenticationAttemptRepository;

	AuthenticationResultMessageFactory(AuthenticationLockSettings authenticationLockSettings,
			AuthenticationAttemptRepository authenticationAttemptRepository) {
		this.authenticationLockSettings = authenticationLockSettings;
		this.authenticationAttemptRepository = authenticationAttemptRepository;
	}

	public Message createMessage(Email email, AuthenticateResult authenticateResult) {
		switch (authenticateResult) {
		case SUCCESS:
			return new Message("login.success");
		case BAD_CREDENTIALS:
			return new Message("login.userInvalid", authenticationAttemptRepository.getAttempts(email)
					.getCurrentAttempt(), authenticationLockSettings.getAttemptsMax());
		case PASSWORD_EXPIRED:
			return new Message("login.passwordExpired");
		case ACCOUNT_LOCKED:
			return new Message("login.accountLocked", authenticationAttemptRepository.getAttempts(email)
					.getUnlockDate());
		default:
			throw new SystemException("Unrecognized authentication result " + authenticateResult);
		}
	}
}
