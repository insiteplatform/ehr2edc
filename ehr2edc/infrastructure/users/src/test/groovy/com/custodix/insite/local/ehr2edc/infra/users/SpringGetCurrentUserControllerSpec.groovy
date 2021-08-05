package com.custodix.insite.local.ehr2edc.infra.users

import com.custodix.insite.local.GetCurrentUserController
import com.custodix.insite.local.ehr2edc.query.security.GetUser
import com.custodix.insite.local.ehr2edc.security.oidc.SecurityContextUser
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

@Title("Get the currently logged in user")
@SpringBootTest(classes = SpringGetCurrentUserControllerSpec)
@Import(UsersConfiguration.class)
@EnableAutoConfiguration
class SpringGetCurrentUserControllerSpec extends Specification {
    @Autowired
    private GetCurrentUserController getCurrentUserController


    @Unroll
    def "Get the currently logged in user"(String username, Boolean drm, UserIdentifier userId) {
        given: "An authenticated user #username with drm #drm "
        authenticate(GetUser.User.newBuilder().withName(username).withDrm(drm).withUserId(userId).build())

        when: "you get the logged in user"
        GetCurrentUserController.Response response = getCurrentUserController.get()

        then: "the logged in user is returned"
        def user = response.user
        user != null
        user.name == username
        user.drm == drm

        where:
        username        | drm   | userId
        "Username"      | true  | UserIdentifier.of("userId")
        "usernameNoDrm" | false | UserIdentifier.of("userIdNoDrm")
    }

    def "Get accessdenied when no user is logged in"() {
        given: "No authenticated user"
        noAuthentication()

        when: "you get the logged in user"
        getCurrentUserController.get()

        then: "the an accessdenied exception is thrown"
        thrown(AccessDeniedException)
    }

    def noAuthentication() {
        SecurityContextHolder.getContext()
                .setAuthentication(null)
    }

    def authenticate(final GetUser.User user) {
        SecurityContextUser principal = new SecurityContextUser(user)
        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(principal, Collections.emptyList(), "ehr2edc-client")
        SecurityContextHolder.getContext()
                .setAuthentication(authentication)
    }
}
