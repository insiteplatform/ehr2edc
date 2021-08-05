package com.custodix.insite.local.user

import com.custodix.insite.local.user.vocabulary.Email
import eu.ehr4cr.workbench.local.WebRoutes
import eu.ehr4cr.workbench.local.dao.SecurityDao
import eu.ehr4cr.workbench.local.exception.security.UserExistsException
import eu.ehr4cr.workbench.local.global.GroupType
import eu.ehr4cr.workbench.local.model.security.User
import eu.ehr4cr.workbench.local.model.security.UserStatus
import eu.ehr4cr.workbench.local.service.email.model.ImmutableUserRegistrationMailContent
import eu.ehr4cr.workbench.local.service.email.model.MailContent
import eu.ehr4cr.workbench.local.usecases.user.register.RegisterUser
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

import static eu.ehr4cr.workbench.local.global.GroupType.ADM
import static eu.ehr4cr.workbench.local.service.email.TestMailService.GroupMail
import static eu.ehr4cr.workbench.local.usecases.user.register.RegisterUser.Request
import static org.assertj.core.api.Java6Assertions.assertThat

@Title("Register user")
class RegisterUserSpec extends AbstractSpecification {
    private static final USER_EMAIL = "register-user@custodix.com"
    private static final USER_NAME = "Register User"
    private static final USER_EXISTING_EMAIL = "regularUser@custodix.com"
    private static final USER_EXISTING_NAME = "Regular User"

    @Autowired
    private RegisterUser registerUser
    @Autowired
    private SecurityDao securityDao

    def "I can register a new user"() {
        given: "I am an anonymous user"
        users.loginAnonymously()

        when: "I register a new user"
        Request request = createRequest(USER_EMAIL, USER_NAME)
        registerUser.register(request)

        then: "The user is created and pending acceptation"
        User user = securityDao.findUserByEmail(Email.of(USER_EMAIL)).get()
        validateUser(user)

        and: "The user has role 'regular user'"
        validateUserGroups(user)

        and: "A registration mail is sent to the administrators"
        GroupMail mail = testMailService.poll()
        mail.group == ADM
        mail.content == createExpectedMailContent(user)
    }

    @Unroll
    def "I cannot register a new user with a duplicate #attribute"() {
        given: "I am an anonymous user"
        users.loginAnonymously()

        and: "An existing user"
        users.aRegularUser()

        when: "I register a new user with a duplicate #attribute"
        Request request = createRequest(email, username)
        registerUser.register(request)

        then: "The user is not registered"
        thrown UserExistsException

        where:
        attribute  | email               | username           | _
        "email"    | USER_EXISTING_EMAIL | USER_NAME          | _
        "username" | USER_EMAIL          | USER_EXISTING_NAME | _
    }

    private void validateUser(User user) {
        assertThat(user.getUsername()).as("username").isEqualTo(USER_NAME)
        assertThat(user.getEmail()).as("email").isEqualTo(USER_EMAIL)
        assertThat(user.getStatus()).as("status").isEqualTo(UserStatus.PENDING)
    }

    private void validateUserGroups(User user) {
        assertThat(user.getGroups()).as("groups").hasSize(1)
        assertThat(user.getGroups().iterator().next().getType()).as("group name").isEqualTo(GroupType.USR)
    }

    private MailContent createExpectedMailContent(User user) {
        return ImmutableUserRegistrationMailContent.builder()
                .username(user.getUsername())
                .acceptUrl(propertyProvider.getBaseUrl() + WebRoutes.manageMembers)
                .build()
    }

    private Request createRequest(String email, String username) {
        return Request.newBuilder()
                .withUserEmail(email)
                .withUserName(username)
                .build()
    }
}