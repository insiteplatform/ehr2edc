package com.custodix.insite.local.ehr2edc.usecase.impl.security;

import org.springframework.security.access.AccessDeniedException;

import com.custodix.insite.local.ehr2edc.query.security.IsDRM;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.GuardCheck;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;

@GuardCheck
@Query
class IsDRMGuard implements IsDRM {

	private final GetCurrentUser getCurrentUser;

	IsDRMGuard(GetCurrentUser getCurrentUser) {
		this.getCurrentUser = getCurrentUser;
	}

	@Override
	public void checkPermission() {
		if (!getCurrentUser.isDRM()) {
			throw new AccessDeniedException("User is not a DRM");
		}
	}
}
