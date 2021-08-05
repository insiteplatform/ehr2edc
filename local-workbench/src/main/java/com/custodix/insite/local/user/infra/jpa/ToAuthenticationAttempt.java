package com.custodix.insite.local.user.infra.jpa;

import java.util.function.Function;

import com.custodix.insite.local.user.domain.AuthenticationAttempt;
import com.custodix.insite.local.user.vocabulary.AuthenticationAttemptIdentifier;

class ToAuthenticationAttempt implements Function<AuthenticationAttemptJpaEntity, AuthenticationAttempt> {
	@Override
	public AuthenticationAttempt apply(final AuthenticationAttemptJpaEntity authenticationAttemptJpaEntity) {
		return AuthenticationAttempt.newBuilder()
				.withIdentifier(AuthenticationAttemptIdentifier.of(authenticationAttemptJpaEntity.getId()))
				.withEmail(authenticationAttemptJpaEntity.getEmail())
				.withResult(authenticationAttemptJpaEntity.getResult())
				.withTimestamp(authenticationAttemptJpaEntity.getTimestamp())
				.build();
	}
}
