package eu.ehr4cr.workbench.local.security;

import org.springframework.security.core.GrantedAuthority;

import eu.ehr4cr.workbench.local.global.AuthorityType;

public class WorkbenchGrantedAuthority implements GrantedAuthority {
	private static final String ROLE_PREFIX = "ROLE_";
	private final AuthorityType authorityType;

	public WorkbenchGrantedAuthority(AuthorityType authorityType) {
		this.authorityType = authorityType;
	}

	@Override
	public String getAuthority() {
		return ROLE_PREFIX + authorityType.name();
	}
}