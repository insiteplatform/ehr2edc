package com.custodix.insite.local.user.infra.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.custodix.insite.local.user.domain.AuthenticationAttempt;
import com.custodix.insite.local.user.domain.AuthenticationAttempts;
import com.custodix.insite.local.user.domain.repository.AuthenticationAttemptRepository;
import com.custodix.insite.local.user.vocabulary.AuthenticationLockSettings;
import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.dao.jpa.AbstractJpaGenericRepository;

@Repository
class AuthenticationAttemptJpaRepository extends AbstractJpaGenericRepository
		implements AuthenticationAttemptRepository {

	private final AuthenticationLockSettings authenticationLockSettings;

	AuthenticationAttemptJpaRepository(AuthenticationLockSettings authenticationLockSettings) {
		this.authenticationLockSettings = authenticationLockSettings;
	}

	@Override
	public AuthenticationAttempt save(AuthenticationAttempt authenticationAttempt) {
		AuthenticationAttemptJpaEntity jpaEntity = super.save(map(authenticationAttempt));
		return map(jpaEntity);
	}

	@Override
	public AuthenticationAttempts getAttempts(Email email) {
		String query = "SELECT aa FROM AuthenticationAttemptJpaEntity aa WHERE timestamp > :since AND email = :email";
		Map<String, Object> params = new HashMap<>();
		params.put("since", authenticationLockSettings.getCutoffDate());
		params.put("email", email);
		List<AuthenticationAttemptJpaEntity> jpaEntities = find(query, params);
		return new AuthenticationAttempts(map(jpaEntities), authenticationLockSettings);
	}

	private AuthenticationAttemptJpaEntity map(AuthenticationAttempt authenticationAttempt) {
		return new ToAuthenticationAttemptJpaEntity().apply(authenticationAttempt);
	}

	private List<AuthenticationAttempt> map(List<AuthenticationAttemptJpaEntity> jpaEntities) {
		return jpaEntities.stream()
				.map(this::map)
				.collect(Collectors.toList());
	}

	private AuthenticationAttempt map(AuthenticationAttemptJpaEntity authenticationAttemptJpaEntity) {
		return new ToAuthenticationAttempt().apply(authenticationAttemptJpaEntity);
	}
}
