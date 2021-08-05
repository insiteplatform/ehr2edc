package com.custodix.insite.local.ehr2edc.usecase.impl.security;

import com.custodix.insite.local.ehr2edc.query.security.IsAny;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.GuardCheck;

@GuardCheck
@Query
class IsAnyGuard implements IsAny {

	@Override
	public void checkPermission() {
		// anything goes
	}

}
