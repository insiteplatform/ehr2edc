package com.custodix.insite.local.ehr2edc.security;

import java.util.Collections;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class EHR2EDCAuthenticationProvider implements AuthenticationProvider {

	private final EHR2EDCSecurityConfigProperties config;

	public EHR2EDCAuthenticationProvider(EHR2EDCSecurityConfigProperties config) {
		this.config = config;
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		if (isValidEHR2EDCAdminLogin(authentication)) {
			return createUsernamePasswordAuthenticationToken(authentication);
		}
		return null;
	}

	private UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(
			final Authentication authentication) {
		return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(),
				Collections.singletonList(new EHR2EDCGrantedAuthority(AuthorityType.EHR2EDC_ADMINISTRATION)));
	}

	private boolean isValidEHR2EDCAdminLogin(final Authentication authentication) {
		return config.getUsername() != null && config.getPassword() != null && config.getUsername()
				.equals(authentication.getPrincipal()) && config.getPassword()
				.equals(authentication.getCredentials());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
