package com.custodix.insite.local.user

import eu.ehr4cr.workbench.local.dao.SecurityDao
import eu.ehr4cr.workbench.local.exception.InvalidRequestException
import eu.ehr4cr.workbench.local.global.AuthorityType
import eu.ehr4cr.workbench.local.model.security.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title

import static com.custodix.insite.local.user.UpdateProfile.Request
import static org.assertj.core.api.Java6Assertions.assertThat

@Title("Update profile")
class UpdateProfileSpec extends AbstractSpecification {
    private static final String NEW_USERNAME = "Updated Username"
    private static final String NEW_EMAIL = "updated-email@custodix.com"
    private static final String INVALID_EMAIL = "invalid-email"

    @Autowired
    private UpdateProfile updateProfile
    @Autowired
    private SecurityDao securityDao

    def "A user with 'ManageUsers_Edit' authority can change the email of his account"() {
        given: "I have the 'MANAGE_ACCOUNTS' authority"
        User user = users.aUserWithAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(user)

        when: "I change my email"
        Request request = createRequest(user, NEW_EMAIL, user.getUsername())
        updateProfile.update(request)

        then: "My email is changed"
        User updatedUser = securityDao.findUserById(user.getId())
        assertThat(updatedUser.getEmail()).as("email").isEqualTo(NEW_EMAIL)
    }

    def "A user with 'ManageUsers_Edit' authority cannot change the email of his account with an invalid address"() {
        given: "I have the 'MANAGE_ACCOUNTS' authority"
        User user = users.aUserWithAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(user)

        when: "I change my email with an invalid address"
        Request request = createRequest(user, INVALID_EMAIL, user.getUsername())
        updateProfile.update(request)

        then: "My email is not changed"
        thrown InvalidRequestException
    }

    def "A user without 'ManageUsers_Edit' authority cannot change the email of his account"() {
        given: "I don't have the 'MANAGE_ACCOUNTS' authority"
        User user = users.aUserWithoutAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(user)

        when: "I change my email"
        Request request = createRequest(user, NEW_EMAIL, user.getUsername())
        updateProfile.update(request)

        then: "Access is denied"
        thrown AccessDeniedException
    }

    def "A user can change the username of his account"() {
        given: "I am logged in"
        User user = users.anActiveUser()
        users.login(user)

        when: "I change the username of my account"
        Request request = createRequest(user, user.getEmail(), NEW_USERNAME)
        updateProfile.update(request)

        then: "My username is changed"
        User updatedUser = securityDao.findUserById(user.getId())
        assertThat(updatedUser.getUsername()).as("username").isEqualTo(NEW_USERNAME)
    }

    def "A user cannot change the username of another user's account"() {
        given: "I am logged in"
        User user = users.anActiveUser()
        users.login(user)

        and: "Another user"
        User anotherUser = users.aRegularUser()

        when: "I change the username of the other user's account"
        Request request = createRequest(anotherUser, anotherUser.getEmail(), NEW_USERNAME)
        updateProfile.update(request)

        then: "Access is denied"
        thrown AccessDeniedException
    }

    private Request createRequest(User user, String email, String username) {
        return Request.newBuilder()
                .withUserIdentifier(user.getIdentifier())
                .withEmail(email)
                .withUsername(username)
                .build()
    }
}
