package com.custodix.insite.local.ehr2edc.security

import com.custodix.insite.local.ehr2edc.user.CurrentUserGateway
import com.custodix.insite.local.ehr2edc.user.User
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import spock.lang.Shared
import spock.lang.Specification

class AbstractSpecification extends Specification {

    @Shared
    CurrentUserGateway currentUserGateway

    def setup() {
        currentUserGateway = Mock()
    }

    def login(String username, boolean drm) {
        currentUserGateway.getCurrentUser() >> User.newBuilder()
                .withUserId(UserIdentifier.of(username))
                .withName(username)
                .withDrm(drm)
                .build()
    }

    def logout() {
        currentUserGateway.getCurrentUser() >> null
    }

}
