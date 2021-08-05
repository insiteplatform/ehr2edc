package com.custodix.insite.local.ehr2edc.user;

import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public interface UserRepository {
	List<User> getUsers();

	User getUser(UserIdentifier userIdentifier);

	Optional<User> findUser(UserIdentifier userIdentifier);
}