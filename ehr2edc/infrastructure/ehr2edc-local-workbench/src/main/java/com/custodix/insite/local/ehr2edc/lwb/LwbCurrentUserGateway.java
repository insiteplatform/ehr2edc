package com.custodix.insite.local.ehr2edc.lwb;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.GetCurrentUserController;
import com.custodix.insite.local.ehr2edc.user.CurrentUserGateway;
import com.custodix.insite.local.ehr2edc.user.User;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

@Component
public class LwbCurrentUserGateway implements CurrentUserGateway {

	private final GetCurrentUserController currentUserController;

	@Autowired
	public LwbCurrentUserGateway(GetCurrentUserController currentUserController) {
		this.currentUserController = currentUserController;
	}

	@Override
	public User getCurrentUser() {
		return Optional.of(currentUserController.get())
				.map(GetCurrentUserController.Response::getUser)
				.map(this::toUser)
				.orElse(null);
	}

	private User toUser(GetCurrentUserController.User user) {
		return User.newBuilder()
				.withUserId(UserIdentifier.of(user.getName()))
				.withName(user.getName())
				.withDrm(user.isDrm())
				.build();
	}
}

