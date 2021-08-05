package com.custodix.insite.local.user

import eu.ehr4cr.workbench.local.exception.security.InvalidPermissionsException
import eu.ehr4cr.workbench.local.global.AuthorityType
import eu.ehr4cr.workbench.local.model.security.User
import eu.ehr4cr.workbench.local.usecases.user.GetDetailedUsers
import org.assertj.core.api.SoftAssertions
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

import static eu.ehr4cr.workbench.local.usecases.user.GetDetailedUsers.Response
import static eu.ehr4cr.workbench.local.usecases.user.GetDetailedUsers.Response.UserInfo
import static org.assertj.core.api.Java6Assertions.assertThat

@Title("Get users")
class GetDetailedUsersSpec extends AbstractSpecification {
    @Autowired
    private GetDetailedUsers getUsers

    def "A user with 'ManageUsers_View' authority can view the users"() {
        given: "A user with 'VIEW_ACCOUNTS' authority"
        User user = users.aUserWithAuthority(AuthorityType.VIEW_ACCOUNTS)
        users.login(user)

        when: "The user views the users"
        Response response = getUsers.getUsers()

        then: "The users are returned"
        validateUsers(response.getUsers())
    }

    def "A user without 'ManageUsers_View' authority cannot view users"() {
        given: "A user without 'VIEW_ACCOUNTS' authority"
        User user = users.aUserWithoutAuthority(AuthorityType.VIEW_ACCOUNTS)
        users.login(user)

        when: "The user views the users"
        getUsers.getUsers()

        then: "Access is denied"
        thrown(InvalidPermissionsException)
    }

    private void validateUsers(List<UserInfo> users) {
        assertThat(users).as("Users count").hasSize(2)
        for (UserInfo user : users) {
            validateUser(user);
        }
    }

    private void validateUser(UserInfo user) {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(user.getId()).as("User ID").isGreaterThan(0L)
        softly.assertThat(user.getUsername()).as("User name").isNotNull()
        softly.assertThat(user.getEmail()).as("User email").isNotNull()
        softly.assertThat(user.getStatus()).as("User status").isNotNull()
        softly.assertThat(user.getRoles()).as("User roles").isNotNull()
        softly.assertAll()
    }
}
