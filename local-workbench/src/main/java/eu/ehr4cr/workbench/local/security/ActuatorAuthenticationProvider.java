package eu.ehr4cr.workbench.local.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.global.AuthorityType;

@Component
@Order(Integer.MIN_VALUE)
class ActuatorAuthenticationProvider implements AuthenticationProvider {

	private final SecurityConfigProperties securityConfigProperties;

	@Autowired
	ActuatorAuthenticationProvider(final SecurityConfigProperties securityConfigProperties) {
		this.securityConfigProperties = securityConfigProperties;
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		if (isValidActuatorLogin(authentication)) {
			return createUsernamePasswordAuthenticationToken(authentication);
		}
		return null;
	}

	private UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(
			final Authentication authentication) {
		return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(),
				Collections.singletonList(
						new WorkbenchGrantedAuthority(AuthorityType.VIEW_ACTUATOR_DETAILS)));
	}

	private boolean isValidActuatorLogin(final Authentication authentication) {
		return securityConfigProperties.getUsername()
				.equals(authentication.getPrincipal()) && securityConfigProperties.getPassword()
				.equals(authentication.getCredentials());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
