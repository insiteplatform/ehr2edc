package com.custodix.insite.local.ehr2edc.usecase.impl.security;

import com.custodix.insite.local.ehr2edc.query.security.GetUser;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.user.UserRepository;

@Query
class GetUserQuery implements GetUser {

	private final UserRepository userRepository;

	GetUserQuery(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Response getUser(Request request) {
		return mapUser(userRepository.getUser(request.getUserIdentifier()));
	}

	private Response mapUser(com.custodix.insite.local.ehr2edc.user.User user) {
		return Response.newBuilder()
				.withUser(User.newBuilder()
						.withUserId(user.getUserIdentifier())
						.withDrm(user.isDrm())
						.withName(user.getName())
						.build())
				.build();
	}
}
