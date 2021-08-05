package com.custodix.insite.local.user

import com.custodix.insite.local.user.application.api.Authenticate
import com.custodix.insite.local.user.application.api.CompleteInvitation
import com.custodix.insite.local.user.vocabulary.Email
import com.custodix.insite.local.user.vocabulary.Password
import eu.ehr4cr.workbench.local.exception.security.TempPasswordMismatchException
import eu.ehr4cr.workbench.local.model.security.User
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

import javax.validation.ConstraintViolationException

import static com.custodix.insite.local.user.application.api.CompleteInvitation.Request
import static eu.ehr4cr.workbench.local.service.DomainTime.now
import static eu.ehr4cr.workbench.local.testingutils.ConstraintViolationAssert.assertThat

@Title("Complete invitation")
class CompleteInvitationSpec extends AbstractSpecification {
    private static final Password USER_PASSWORD = Password.of("P4ssword_new")
    private static final Password USER_PASSWORD_INSUFFICIENT_STRENGTH = Password.of("Password")
    private static final String USER_SECURITY_QUESTION_ID = "1"
    private static final String USER_SECURITY_QUESTION_ANSWER = "Answer"
    private static final String INCORRECT_TEMPORARY_PASSWORD = "Incorrect temporary password"

    @Autowired
    private CompleteInvitation completeInvitation
    @Autowired
    private Authenticate authenticate

    def "An invited user can complete his account invitation"() {
        given: "An invited user"
        User user = users.anInvitedUser()
        users.loginAnonymously()

        when: "The user completes his account invitation"
        Request request = createRequest(user.getId(), USER_PASSWORD, user.getTempPassword())
        completeInvitation.completeInvitation(request)

        then: "The user can authenticate"
        User userValidate = authenticate.authenticate(Authenticate.Request.newBuilder().withEmail(Email.of(user.email)).withPassword(USER_PASSWORD).build()).getUser()
        then: "The user's password last modified date is updated"
        userValidate.passwordLastModified == now()
        and: "The user's security question is updated"
        userValidate.getSecurityQuestionId() == USER_SECURITY_QUESTION_ID
        userValidate.getSecurityAnswer() == USER_SECURITY_QUESTION_ANSWER
    }

    def "An invited user cannot complete his account invitation with an incorrect temporary password"() {
        given: "An invited user"
        User user = users.anInvitedUser()
        users.loginAnonymously()

        when: "The user completes his account invitation with an incorrect temporary password"
        Request request = createRequest(user.getId(), USER_PASSWORD, INCORRECT_TEMPORARY_PASSWORD)
        completeInvitation.completeInvitation(request)

        then: "The user's account is not activated"
        thrown TempPasswordMismatchException
    }

    def "An invited user cannot complete his account invitation with a password of insufficient strength"() {
        given: "A invited user"
        User user = users.anInvitedUser()
        users.loginAnonymously()

        when: "The user completes his account invitation with a password of insufficient strength"
        Request request = createRequest(user.getId(), USER_PASSWORD_INSUFFICIENT_STRENGTH, user.getTempPassword())
        completeInvitation.completeInvitation(request)

        then: "The user's account is not activated"
        def exception = thrown ConstraintViolationException
        assertThat(exception).hasSize(2)
        assertThat(exception).contains("Password must contain 1 or more digit characters.")
        assertThat(exception).contains("Password must contain 1 or more special characters.")
    }

    private Request createRequest(long userId, Password password, String tempPassword) {
        return Request.newBuilder()
                .withUserId(userId)
                .withPassword(password)
                .withTempPassword(tempPassword)
                .withSecurityQuestionId(USER_SECURITY_QUESTION_ID)
                .withSecurityQuestionAnswer(USER_SECURITY_QUESTION_ANSWER)
                .build()
    }
}
