package com.custodix.insite.local.ehr2edc.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.custodix.insite.local.GetUsersController;

public class TestGetUsersController implements GetUsersController {
	private List<GetUsersController.User> users = new ArrayList<>();

	@Override
	public Response getUsers() {
		return Response.newBuilder()
				.withUsers(users)
				.build();
	}

	public void addUsers(Collection<GetUsersController.User> users) {
		this.users.addAll(users);
	}

	public void clear() {
		users.clear();
	}
}