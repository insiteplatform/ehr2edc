package com.custodix.insite.local.ehr2edc;

import com.custodix.insite.local.ehr2edc.user.User;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public final class UserObjectMother {

	public static User createUser(String userId) {
		return User.newBuilder()
				.withUserId(UserIdentifier.of(userId))
				.withName(userId)
				.build();
	}
}