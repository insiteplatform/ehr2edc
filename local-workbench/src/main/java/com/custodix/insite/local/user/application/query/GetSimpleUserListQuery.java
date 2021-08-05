package com.custodix.insite.local.user.application.query;

import java.util.List;
import java.util.stream.Collectors;

import com.custodix.insite.local.shared.annotations.Query;
import com.custodix.insite.local.user.application.api.GetSimpleUserList;

import eu.ehr4cr.workbench.local.dao.SecurityDao;

@Query
class GetSimpleUserListQuery implements GetSimpleUserList {

	private final SecurityDao securityDao;

	GetSimpleUserListQuery(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	@Override
	public Response getActiveUsers() {
		List<eu.ehr4cr.workbench.local.model.security.User> activeUsers = securityDao.findAllValidUsers();
		return Response.newBuilder()
				.withUsers(mapUsers(activeUsers))
				.build();
	}

	private List<User> mapUsers(List<eu.ehr4cr.workbench.local.model.security.User> activeUsers) {
		return activeUsers.stream()
				.map(this::mapUser)
				.collect(Collectors.toList());
	}

	private User mapUser(eu.ehr4cr.workbench.local.model.security.User u) {
		return User.newBuilder()
				.withId(u.getIdentifier())
				.withName(u.getUsername())
				.build();
	}
}
