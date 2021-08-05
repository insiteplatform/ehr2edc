package eu.ehr4cr.workbench.local.security;

import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.model.security.AnonymousUser;
import eu.ehr4cr.workbench.local.model.security.User;

@Component
public class AuthenticatedUserHolder {
	public User getAuthenticatedUser() {
		return findAuthentication().map(this::getUser)
				.orElse(new AnonymousUser());
	}

	private Optional<Authentication> findAuthentication() {
		SecurityContext context = SecurityContextHolder.getContext();
		return Optional.ofNullable(context.getAuthentication());
	}

	private User getUser(Authentication authentication) {
		if (authentication instanceof AnonymousAuthenticationToken) {
			return new AnonymousUser();
		}
		SecurityContextUser securityContextUser = (SecurityContextUser) authentication.getPrincipal();
		return securityContextUser.getWorkbenchUser();
	}
}
