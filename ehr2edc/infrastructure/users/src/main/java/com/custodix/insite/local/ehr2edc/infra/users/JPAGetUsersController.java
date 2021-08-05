package com.custodix.insite.local.ehr2edc.infra.users;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.custodix.insite.local.GetUsersController;
import com.custodix.insite.local.ehr2edc.infra.users.repository.UserRepository;

@Component
class JPAGetUsersController implements GetUsersController {

	private final UserRepository userRepository;

	JPAGetUsersController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Response getUsers() {
		return Response.newBuilder()
				.withUsers(findActiveUsers())
				.build();
	}

	private List<User> findActiveUsers() {
		return userRepository.findByCredentialEnabledTrueAndCredentialDeletedFalse()
				.stream()
				.map(this::mapUser)
				.collect(Collectors.toList());
	}

	private User mapUser(com.custodix.insite.local.ehr2edc.infra.users.model.User u) {
		return User.newBuilder()
				.withDrm(u.isDrm())
				.withName(u.getUsername())
				.withId(u.getId())
				.build();
	}

}
