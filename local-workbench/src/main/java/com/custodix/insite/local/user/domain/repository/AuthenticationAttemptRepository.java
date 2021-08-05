package com.custodix.insite.local.user.domain.repository;

import com.custodix.insite.local.user.domain.AuthenticationAttempt;
import com.custodix.insite.local.user.domain.AuthenticationAttempts;
import com.custodix.insite.local.user.vocabulary.Email;

public interface AuthenticationAttemptRepository {
	AuthenticationAttempt save(AuthenticationAttempt authenticationAttempt);

	AuthenticationAttempts getAttempts(Email email);
}
