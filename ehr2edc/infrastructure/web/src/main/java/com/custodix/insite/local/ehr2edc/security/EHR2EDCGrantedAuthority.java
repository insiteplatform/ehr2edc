package com.custodix.insite.local.ehr2edc.security;

import org.springframework.security.core.GrantedAuthority;

public class EHR2EDCGrantedAuthority implements GrantedAuthority {
	private static final String ROLE_PREFIX = "ROLE_";
	private final AuthorityType authorityType;

	EHR2EDCGrantedAuthority(AuthorityType authorityType) {
		this.authorityType = authorityType;
	}

	@Override
	public String getAuthority() {
		return ROLE_PREFIX + authorityType.name();
	}
}