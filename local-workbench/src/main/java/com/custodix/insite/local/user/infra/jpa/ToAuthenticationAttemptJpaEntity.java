package com.custodix.insite.local.user.infra.jpa;

import java.util.function.Function;

import com.custodix.insite.local.user.domain.AuthenticationAttempt;
import com.custodix.insite.local.user.vocabulary.AuthenticationAttemptIdentifier;

class ToAuthenticationAttemptJpaEntity implements Function<AuthenticationAttempt, AuthenticationAttemptJpaEntity> {
	@Override
	public AuthenticationAttemptJpaEntity apply(final AuthenticationAttempt authenticationAttempt) {
		return AuthenticationAttemptJpaEntity.newBuilder()
				.withId(mapIdentifier(authenticationAttempt))
				.withEmail(authenticationAttempt.getEmail())
				.withResult(authenticationAttempt.getResult())
				.withTimestamp(authenticationAttempt.getTimestamp())
				.build();
	}

	private Long mapIdentifier(AuthenticationAttempt authenticationAttempt) {
		return authenticationAttempt.getIdentifier()
				.map(AuthenticationAttemptIdentifier::getId)
				.orElse(null);
	}
}
