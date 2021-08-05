package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;

public interface DatawarehouseUpdated {

	@Allow(ANYONE)
	void update();
}
