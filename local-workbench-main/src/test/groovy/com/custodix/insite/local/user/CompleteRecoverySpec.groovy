package com.custodix.insite.local.user

import com.custodix.insite.local.user.application.api.Authenticate
import com.custodix.insite.local.user.application.api.CompleteRecovery
import com.custodix.insite.local.user.domain.events.PasswordRecoveredEvent
import com.custodix.insite.local.user.vocabulary.AuthenticateResult
import com.custodix.insite.local.user.vocabulary.Email
import com.custodix.insite.local.user.vocabulary.Password
import eu.ehr4cr.workbench.local.exception.security.TempPasswordMismatchException
import eu.ehr4cr.workbench.local.model.security.User
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

import javax.validation.ConstraintViolationException

import static com.custodix.insite.local.user.application.api.CompleteRecovery.Request
import static com.custodix.insite.local.user.vocabulary.AuthenticateResult.BAD_CREDENTIALS
import static eu.ehr4cr.workbench.local.service.DomainTime.now
import static eu.ehr4cr.workbench.local.testingutils.ConstraintViolationAssert.assertThat

@Title("Complete recovery")
class CompleteRecoverySpec extends AbstractSpecification {
    private static final Password USER_PASSWORD = Password.of("P4ssword_new")
    private static final String INCORRECT_TEMPORARY_PASSWORD = "Incorrect temporary password"
    private static final Password USER_PASSWORD_INSUFFICIENT_STRENGTH = Password.of("Password")

    @Autowired
    private CompleteRecovery completeRecovery
    @Autowired
    private Authenticate authenticate

    def "A recovering user can complete his account recovery"() {
        given: "I am a recovering user"
        User user = users.aRecoveringUser()
        users.loginAnonymously()

        when: "I complete my account recovery"
        Request request = createRequest(user.getId(), USER_PASSWORD, user.getTempPassword())
        completeRecovery.completeRecovery(request)

        then: "I can authenticate using my new password"
        User userValidate = doAuthenticate(user.emailAddress, USER_PASSWORD).getUser()
        and: "My password last modified date is updated"
        userValidate.passwordLastModified == now()
        and: "A password recovered event is published"
        PasswordRecoveredEvent event = eventPublisher.poll() as PasswordRecoveredEvent
        event.email == userValidate.emailAddress
    }

    def "A recovering user cannot complete his account recovery with an incorrect temporary password"() {
        given: "I am a recovering user"
        User user = users.aRecoveringUser()
        users.loginAnonymously()

        when: "I complete my account recovery with an incorrect temporary password"
        Request request = createRequest(user.getId(), USER_PASSWORD, INCORRECT_TEMPORARY_PASSWORD)
        completeRecovery.completeRecovery(request)

        then: "I cannot authenticate using my new password"
        AuthenticateResult authenticateResult = doAuthenticate(user.emailAddress, USER_PASSWORD).getResult()
        authenticateResult == BAD_CREDENTIALS
        and: "No event is published"
        eventPublisher.empty
        and: "I see a clear error message"
        thrown TempPasswordMismatchException
    }

    def "A recovering user cannot complete his account recovery with a password of insufficient strength"() {
        given: "I am a recovering user"
        User user = users.aRecoveringUser()
        users.loginAnonymously()

        when: "I complete my account recovery with a password of insufficient strength"
        Request request = createRequest(user.getId(), USER_PASSWORD_INSUFFICIENT_STRENGTH, user.getTempPassword())
        completeRecovery.completeRecovery(request)

        then: "I cannot authenticate using my new password"
        AuthenticateResult authenticateResult = doAuthenticate(user.emailAddress, USER_PASSWORD_INSUFFICIENT_STRENGTH).getResult()
        authenticateResult == BAD_CREDENTIALS
        and: "No event is published"
        eventPublisher.empty
        and: "I see a clear error message"
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
                .build()
    }

    private Authenticate.Response doAuthenticate(Email email, Password password) {
        Authenticate.Request request = Authenticate.Request.newBuilder()
                .withEmail(email)
                .withPassword(password)
                .build()
        return authenticate.authenticate(request)
    }
}
