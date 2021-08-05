package com.custodix.insite.local.ehr2edc.usecase.impl.security;

import org.springframework.security.access.AccessDeniedException;

import com.custodix.insite.local.ehr2edc.query.security.IsAuthenticated;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.GuardCheck;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;

@GuardCheck
@Query
class IsAuthenticatedGuard implements IsAuthenticated {

	private final GetCurrentUser getCurrentUser;

	IsAuthenticatedGuard(GetCurrentUser getCurrentUser) {
		this.getCurrentUser = getCurrentUser;
	}

	@Override
	public void checkPermission() {
		if (!getCurrentUser.isAuthenticated()) {
			throw new AccessDeniedException("Authentication required.");
		}
	}
}
