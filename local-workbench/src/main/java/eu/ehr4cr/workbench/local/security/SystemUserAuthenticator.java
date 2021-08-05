package eu.ehr4cr.workbench.local.security;

import eu.ehr4cr.workbench.local.model.security.SystemUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SystemUserAuthenticator {

	public void authenticate() {
		SystemUser systemUser = new SystemUser();
		SecurityContextUser principal = new SecurityContextUser(systemUser);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, "");
		SecurityContextHolder.getContext()
				.setAuthentication(authentication);
	}
}
