package com.custodix.insite.local.ehr2edc.user;

import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public interface GetCurrentUser {

	UserIdentifier getUserId();

	boolean isDRM();

	boolean isAuthenticated();
}
