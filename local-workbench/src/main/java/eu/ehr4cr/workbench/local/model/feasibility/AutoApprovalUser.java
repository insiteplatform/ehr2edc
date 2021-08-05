package eu.ehr4cr.workbench.local.model.feasibility;

import eu.ehr4cr.workbench.local.model.security.SystemUser;

public class AutoApprovalUser extends SystemUser {
	@Override
	public String getUsername() {
		return "automatic approval";
	}
}
