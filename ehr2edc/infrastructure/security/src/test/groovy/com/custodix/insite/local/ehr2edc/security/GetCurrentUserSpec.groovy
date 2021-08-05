package com.custodix.insite.local.ehr2edc.security

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser

class GetCurrentUserSpec extends AbstractSpecification {

    GetCurrentUser getCurrentUser

    def setup() {
        getCurrentUser = new GetCurrentUserSpring(currentUserGateway)
    }

    def "Getting the current user when you're signed in"(String username) {
        given: "I am signed in as #username"
        login(username, false)

        when: "I ask for the current user"
        def currentUserId = getCurrentUser.getUserId()

        then: "I receive a user with id #username"
        currentUserId != null
        currentUserId.id == username

        where:
        username = "test-user"
    }

    def "Getting the current user when you're not signed in"() {
        given: "I am not signed in"
        logout()

        when: "I ask for the current user"
        getCurrentUser.getUserId()

        then: "An error gets thrown"
        DomainException domainException = thrown(DomainException.class)
        domainException.message == "No authenticated user found"
    }

    def "The user is not a DRM if the gateway returns a user without DRM"() {
        given: "A non-DRM user is signed in"
        login("username", false)

        when: "checking whether the user is DRM "
        def isDRM = getCurrentUser.isDRM()

        then: "the user is not a DRM "
        !isDRM
    }

    def "Cannot retrieve isDRM when no user is signed in"() {
        given: "No user is signed in"
        logout()

        when: "checking whether the user is DRM "
        getCurrentUser.isDRM()

        then: "An error gets thrown"
        DomainException domainException = thrown(DomainException.class)
        domainException.message == "No authenticated user found"
    }

}