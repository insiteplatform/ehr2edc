package com.custodix.insite.local.ehr2edc.query.security;

import java.util.List;

public interface IsAssignedInvestigator {

	void checkPermission(List<Object> correlatorAncestors);

}
