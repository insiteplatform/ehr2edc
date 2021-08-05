package eu.ehr4cr.workbench.local.usecases.checkauthority;

import eu.ehr4cr.workbench.local.global.AuthorityType;

public interface CheckUserHasAuthority {
	boolean check(AuthorityType authorityType);
}
