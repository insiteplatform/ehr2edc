package com.custodix.insite.local.ehr2edc.security.oidc;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.custodix.insite.local.ehr2edc.query.security.GetUser;

public class SecurityContextUser implements OAuth2User {
	private final GetUser.User user;

	public SecurityContextUser(GetUser.User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return Collections.emptyMap();
	}

	@Override
	public String getName() {
		return user.getName();
	}

	public GetUser.User getUser() {
		return user;
	}
}
