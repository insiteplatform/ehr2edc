package com.custodix.insite.local.ehr2edc.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.user.CurrentUserGateway;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;
import com.custodix.insite.local.ehr2edc.user.User;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

@Component
public class GetCurrentUserSpring implements GetCurrentUser {

	private final CurrentUserGateway currentUserGateway;

	@Autowired
	public GetCurrentUserSpring(CurrentUserGateway currentUserGateway) {
		this.currentUserGateway = currentUserGateway;
	}

	@Override
	public UserIdentifier getUserId() {
		return Optional.ofNullable(currentUserGateway.getCurrentUser())
				.map(User::getUserIdentifier)
				.orElseThrow(this::noAuthenticatedUser);
	}

	@Override
	public boolean isDRM() {
		return Optional.ofNullable(currentUserGateway.getCurrentUser())
				.map(User::isDrm)
				.orElseThrow(this::noAuthenticatedUser);
	}

	@Override
	public boolean isAuthenticated() {
		return Optional.ofNullable(currentUserGateway.getCurrentUser())
				.map(User::getUserIdentifier)
				.map(UserIdentifier::getId)
				.isPresent();
	}

	private DomainException noAuthenticatedUser() {
		return new DomainException("No authenticated user found");
	}

}