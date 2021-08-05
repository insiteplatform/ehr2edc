package com.custodix.insite.local.ehr2edc.infra.users;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.GetCurrentUserController;
import com.custodix.insite.local.ehr2edc.query.security.GetUser;
import com.custodix.insite.local.ehr2edc.security.oidc.SecurityContextUser;

@Component
class SpringGetCurrentUserController implements GetCurrentUserController {

	@Override
	public Response get() {
		Optional<SecurityContextUser> currentUserIdentifier = findCurrentUser();
		return currentUserIdentifier.map(SecurityContextUser::getUser)
				.map(this::mapToResponse)
				.orElseThrow(notAuthenticated());
	}

	private Response mapToResponse(GetUser.User user) {
		return Response.newBuilder()
				.withUser(User.newBuilder()
						.withDrm(user.isDrm())
						.withName(user.getName())
						.build())
				.build();
	}

	private Supplier<AccessDeniedException> notAuthenticated() {
		return () -> new AccessDeniedException("User is not authenticated");
	}

	public Optional<SecurityContextUser> findCurrentUser() {
		return getAuthentication().map(this::getPrincipal);
	}

	private SecurityContextUser getPrincipal(Authentication auth) {
		return (SecurityContextUser) auth.getPrincipal();
	}

	private Optional<Authentication> getAuthentication() {
		return Optional.ofNullable(getContext().getAuthentication());
	}
}
