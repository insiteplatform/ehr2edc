package com.custodix.insite.local.user.infra.ehr2edc;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.custodix.insite.local.GetUsersController;
import com.custodix.insite.local.user.application.api.GetSimpleUserList;

@Component
class InProcessGetUsersController implements GetUsersController {

	private final GetSimpleUserList getSimpleUserList;

	public InProcessGetUsersController(GetSimpleUserList getSimpleUserList) {
		this.getSimpleUserList = getSimpleUserList;
	}

	@Override
	public Response getUsers() {
		return Response.newBuilder()
				.withUsers(mapUsers(getSimpleUserList.getActiveUsers()
						.getUsers()))
				.build();
	}

	private List<User> mapUsers(List<GetSimpleUserList.User> users) {
		return users.stream()
				.map(this::mapUsers)
				.collect(Collectors.toList());
	}

	private User mapUsers(GetSimpleUserList.User u) {
		return User.newBuilder()
				.withId(u.getId()
						.getId())
				.withName(u.getName())
				.build();
	}

}
