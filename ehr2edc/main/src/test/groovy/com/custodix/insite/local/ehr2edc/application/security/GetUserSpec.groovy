package com.custodix.insite.local.ehr2edc.application.security

import com.custodix.insite.local.GetUsersController
import com.custodix.insite.local.ehr2edc.application.AbstractSpecification
import com.custodix.insite.local.ehr2edc.query.security.GetUser
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

@Title("Get a user")
class GetUserSpec extends AbstractSpecification {
    @Autowired
    GetUser getUser

    def "Get a user as an unauthenticated user"() {
        given: "A user"
        userRepository.addUsers(Collections.singletonList(GetUsersController.User.newBuilder().withDrm(true).withName("Gert").withId(1L).build()))
        and: "unauthenticated"
        withoutAuthenticatedUser()

        when: "Getting a user"
        def response = getUser.getUser(GetUser.Request.newBuilder().withUserIdentifier(UserIdentifier.of("Gert")).build())

        then: "The user is retrieved"
        def user = response.user
        user.name == "Gert"
        user.userIdentifier == UserIdentifier.of("Gert")
        user.drm
    }

    def "Get a user as an authenticated user"() {
        given: "A user"
        userRepository.addUsers(Collections.singletonList(GetUsersController.User.newBuilder().withDrm(false).withName("Gert").withId(1L).build()))
        and: "authenticated"
        withCurrentUserNoDRM()

        when: "Getting a user"
        def response = getUser.getUser(GetUser.Request.newBuilder().withUserIdentifier(UserIdentifier.of("Gert")).build())

        then: "The user is retrieved"
        def user = response.user
        user.name == "Gert"
        user.userIdentifier == UserIdentifier.of("Gert")
        !user.drm
    }


    @Unroll
    def "Get a user with an invalid UserIdentifier"(UserIdentifier user, String field, String message) {
        given: "A user"
        userRepository.addUsers(Collections.singletonList(GetUsersController.User.newBuilder().withDrm(false).withName("Gert").withId(1L).build()))
        and: "a request without a user identifier"
        GetUser.Request request = GetUser.Request.newBuilder().withUserIdentifier(user).build()

        when: "getting the user"
        getUser.getUser(request)

        then: "An error should be shown that the #field #message"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field == field
        ex.constraintViolations.first().message == message
        where:
        user                    | field               | message
        null                    | "userIdentifier"    | "must not be null"
        UserIdentifier.of(null) | "userIdentifier.id" | "must not be null"
    }
}
