package com.custodix.insite.local.user.infra.ehr2edc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.GetCurrentUserController;

import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.CurrentUser;
import eu.ehr4cr.workbench.local.model.security.Group;

@Component
public class InProcessGetCurrentUserController implements GetCurrentUserController {

	private final CurrentUser currentUser;

	@Autowired
	public InProcessGetCurrentUserController(CurrentUser currentUser) {
		this.currentUser = currentUser;
	}

	@Override
	public Response get() {
		return Response.newBuilder()
				.withUser(this.getCurrentUser())
				.build();
	}

	private User getCurrentUser() {
		return currentUser.findCurrentUser()
				.map(this::toUser)
				.orElse(null);
	}

	private User toUser(eu.ehr4cr.workbench.local.model.security.User user) {
		return User.newBuilder()
				.withId(user.getId())
				.withName(user.getUsername())
				.withDrm(isDRM(user))
				.build();
	}

	private boolean isDRM(eu.ehr4cr.workbench.local.model.security.User user) {
		return user.getGroups()
				.stream()
				.map(Group::getName)
				.map(GroupType::fromInnerName)
				.anyMatch(GroupType.DRM::equals);
	}
}
