package com.custodix.insite.local.ehr2edc.lwb;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.custodix.insite.local.GetUsersController;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.user.User;
import com.custodix.insite.local.ehr2edc.user.UserRepository;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

@Component
public class InProcessUserRepository implements UserRepository {

	private final GetUsersController getUsersController;

	public InProcessUserRepository(GetUsersController getUsersController) {
		this.getUsersController = getUsersController;
	}

	@Override
	public List<User> getUsers() {
		Collection<GetUsersController.User> lwbUsers = getUsersController.getUsers()
				.getUsers();
		return lwbUsers.stream()
				.map(this::mapToEHR2EDCUser)
				.collect(Collectors.toList());
	}

	@Override
	public User getUser(UserIdentifier userIdentifier) {
		List<User> users = getUsers();
		return users.stream()
				.filter(u -> userIdentifier.equals(u.getUserIdentifier()))
				.findFirst()
				.orElseThrow(() -> DomainException.of("user.unknown", userIdentifier));
	}

	@Override
	public Optional<User> findUser(UserIdentifier userIdentifier) {
		List<User> users = getUsers();
		return users.stream()
				.filter(u -> userIdentifier.equals(u.getUserIdentifier()))
				.findFirst();
	}

	private User mapToEHR2EDCUser(GetUsersController.User u) {
		return User.newBuilder()
				.withName(u.getName())
				.withUserId(UserIdentifier.of(u.getName()))
				.withDrm(u.isDrm())
				.build();
	}
}