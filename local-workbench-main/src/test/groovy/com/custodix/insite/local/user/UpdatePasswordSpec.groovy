package com.custodix.insite.local.user

import com.custodix.insite.local.cohort.scenario.objectMother.Users
import com.custodix.insite.local.user.application.api.Authenticate
import com.custodix.insite.local.user.application.api.UpdatePassword
import com.custodix.insite.local.user.vocabulary.Email
import com.custodix.insite.local.user.vocabulary.Password
import eu.ehr4cr.workbench.local.exception.feasibility.DomainException
import eu.ehr4cr.workbench.local.model.security.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title

import javax.validation.ConstraintViolationException

import static com.custodix.insite.local.user.application.api.UpdatePassword.Request
import static eu.ehr4cr.workbench.local.service.DomainTime.now
import static eu.ehr4cr.workbench.local.testingutils.ConstraintViolationAssert.assertThat

@Title("Update password")
class UpdatePasswordSpec extends AbstractSpecification {
    private static final Password OLD_PASSWORD = Password.of(Users.PASSWORD)
    private static final Password OLD_PASSWORD_INCORRECT = Password.of("incorrect-password")
    private static final Password NEW_PASSWORD = Password.of("P4ssword_new")
    private static final Password PASSWORD_INSUFFICIENT_STRENGTH = Password.of("Password")

    @Autowired
    private UpdatePassword updatePassword
    @Autowired
    private Authenticate authenticate

    def "A user can change the password of his account"() {
        given: "I am authenticated"
        User user = users.anActiveUser()
        users.login(user)

        when: "I change the password of my account"
        Request request = createRequest(user, OLD_PASSWORD, NEW_PASSWORD)
        updatePassword.update(request)

        then: "I can authenticate using my new password"
        User userValidate = authenticateUser(user, NEW_PASSWORD)
        and: "The password last modified date is updated"
        userValidate.passwordLastModified == now()
    }

    def "A user can change the password of his account that is of insufficient strength"() {
        given: "I am authenticated"
        User user = users.anActiveUser()
        users.login(user)
        and: "The current password of my account is of insufficient strength"
        user.changePassword(OLD_PASSWORD, PASSWORD_INSUFFICIENT_STRENGTH)

        when: "I change the password of my account"
        Request request = createRequest(user, PASSWORD_INSUFFICIENT_STRENGTH, NEW_PASSWORD)
        updatePassword.update(request)

        then: "I can authenticate using my new password"
        User userValidate = authenticateUser(user, NEW_PASSWORD)
        and: "The password last modified date is updated"
        userValidate.passwordLastModified == now()
    }

    def "A user cannot change the password of another user's account"() {
        given: "I am authenticated"
        User user = users.anActiveUser()
        users.login(user)

        and: "Another user"
        User anotherUser = users.aRegularUser()

        when: "I change the password of the other user's account"
        Request request = createRequest(anotherUser, OLD_PASSWORD, NEW_PASSWORD)
        updatePassword.update(request)

        then: "The other user's password is not changed"
        User userValidate = authenticateUser(anotherUser, OLD_PASSWORD)
        userValidate.passwordLastModified == anotherUser.passwordLastModified

        and: "Access is denied"
        thrown AccessDeniedException
    }

    def "A user cannot change the password of his account if the old password doesn't match"() {
        given: "I am authenticated"
        User user = users.anActiveUser()
        users.login(user)

        when: "I change the password of my account using an incorrect old password"
        Request request = createRequest(user, OLD_PASSWORD_INCORRECT, NEW_PASSWORD)
        updatePassword.update(request)

        then: "My password is not changed"
        User userValidate = authenticateUser(user, OLD_PASSWORD)
        userValidate.passwordLastModified == user.passwordLastModified

        and: "A clear error message is returned"
        def exception = thrown DomainException
        exception.message == "Your current password is incorrect"
    }

    def "A user cannot change the password of his account to his current password"() {
        given: "I am authenticated"
        User user = users.anActiveUser()
        users.login(user)

        when: "I change the password of my account to my current password"
        Request request = createRequest(user, OLD_PASSWORD, OLD_PASSWORD)
        updatePassword.update(request)

        then: "My password is not changed"
        User userValidate = authenticateUser(user, OLD_PASSWORD)
        userValidate.passwordLastModified == user.passwordLastModified

        and: "A clear error message is returned"
        def exception = thrown DomainException
        exception.message == "Your current and new password must be different"
    }

    def "A user cannot change the password of his account to a password of insufficient strength"() {
        given: "I am authenticated"
        User user = users.anActiveUser()
        users.login(user)

        when: "I change the password of my account to a password of insufficient strength"
        Request request = createRequest(user, OLD_PASSWORD, PASSWORD_INSUFFICIENT_STRENGTH)
        updatePassword.update(request)

        then: "My password is not changed"
        User userValidate = authenticateUser(user, OLD_PASSWORD)
        userValidate.passwordLastModified == user.passwordLastModified

        and: "A clear error message is returned"
        def exception = thrown ConstraintViolationException
        assertThat(exception).hasSize(2)
        assertThat(exception).contains("Password must contain 1 or more digit characters.")
        assertThat(exception).contains("Password must contain 1 or more special characters.")
    }

    private Request createRequest(User user, Password oldPassword, Password newPassword) {
        return Request.newBuilder()
                .withUserIdentifier(user.getIdentifier())
                .withOldPassword(oldPassword)
                .withNewPassword(newPassword)
                .build()
    }

    private User authenticateUser(User user, Password password) {
        def request = Authenticate.Request.newBuilder()
                .withEmail(Email.of(user.email))
                .withPassword(password)
                .build()
        return authenticate.authenticate(request).getUser()
    }
}
