package com.custodix.insite.local.user

import com.custodix.insite.local.user.application.api.Authenticate
import com.custodix.insite.local.user.vocabulary.AuthenticationLockSettings
import com.custodix.insite.local.user.vocabulary.Email
import com.custodix.insite.local.user.vocabulary.Password
import eu.ehr4cr.workbench.local.model.security.User
import eu.ehr4cr.workbench.local.model.security.UserStatus
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import javax.validation.ConstraintViolationException
import java.util.stream.Collectors

import static com.custodix.insite.local.cohort.scenario.objectMother.Users.PASSWORD
import static com.custodix.insite.local.user.application.api.Authenticate.Request
import static com.custodix.insite.local.user.application.api.Authenticate.Response
import static com.custodix.insite.local.user.vocabulary.AuthenticateResult.*
import static com.custodix.insite.local.user.vocabulary.Password.of
import static eu.ehr4cr.workbench.local.model.security.UserStatus.*
import static eu.ehr4cr.workbench.local.testingutils.ConstraintViolationAssert.assertThat

class AuthenticateSpec extends AbstractSpecification {
    @Autowired
    private Authenticate authenticate
    @Autowired
    private AuthenticationLockSettings authenticationLockSettings

    @Unroll
    def "A #status user can authenticate"() {
        given: "I am a #status user"
        User user = users.aUser(status)

        when: "I authenticate using my email and password"
        Request request = Request.newBuilder().withEmail(user.emailAddress).withPassword(of(PASSWORD)).build()
        Response response = authenticate.authenticate(request)

        then: "I am authenticated"
        response.result == SUCCESS
        response.user.emailAddress == user.emailAddress

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    @Unroll
    def "A #status user cannot authenticate using an incorrect password"() {
        given: "I am a #status user"
        User user = users.aUser(status as UserStatus)

        when: "I authenticate using an incorrect password"
        Request request = Request.newBuilder().withEmail(user.emailAddress).withPassword(of("test")).build()
        Response response = authenticate.authenticate(request)

        then: "I am not authenticated"
        response.result == BAD_CREDENTIALS
        response.message.key == "login.userInvalid"
        response.message.parameters == [1, authenticationLockSettings.attemptsMax]

        where:
        status << Arrays.stream(UserStatus.values()).filter({ s -> s != UNKNOWN }).collect(Collectors.toList())
    }

    @Unroll
    def "A #status user cannot authenticate using an expired password"() {
        given: "I am a #status user"
        User user = users.aUser(status)
        and: "My password has expired"
        Password expiredPassword = users.setExpiredPassword(user)

        when: "I authenticate using my email and password"
        Request request = Request.newBuilder().withEmail(user.emailAddress).withPassword(expiredPassword).build()
        Response response = authenticate.authenticate(request)

        then: "I am not authenticated"
        response.result == PASSWORD_EXPIRED
        response.message.key == "login.passwordExpired"

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    @Unroll
    def "A #status user can authenticate using an imminently expiring password"() {
        given: "I am a #status user"
        User user = users.aUser(status)
        and: "My password is imminently expiring"
        Password expiredPassword = users.setImminentlyExpiringPassword(user)

        when: "I authenticate using my email and password"
        Request request = Request.newBuilder().withEmail(user.emailAddress).withPassword(expiredPassword).build()
        Response response = authenticate.authenticate(request)

        then: "I am authenticated"
        response.result == SUCCESS
        response.user.emailAddress == user.emailAddress

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    def "A user cannot authenticate with an unknown email"() {
        when: "I authenticate using an email that does not exist"
        Request request = Request.newBuilder().withEmail(Email.of("test@custodix.com")).withPassword(of(PASSWORD)).build()
        Response response = authenticate.authenticate(request)

        then: "I am not authenticated"
        response.result == BAD_CREDENTIALS
        response.message.key == "login.userInvalid"
    }

    def "A user cannot authenticate with an invalid email"() {
        when: "I authenticate using an email that is invalid"
        Request request = Request.newBuilder().withEmail(Email.of("test")).withPassword(of(PASSWORD)).build()
        authenticate.authenticate(request)

        then: "I am not authenticated"
        def exception = thrown ConstraintViolationException
        assertThat(exception).containsExactly("must be a well-formed email address")
    }

    def "A user cannot authenticate without an email"() {
        when: "I authenticate without an email"
        Request request = Request.newBuilder().withPassword(of(PASSWORD)).build()
        authenticate.authenticate(request)

        then: "I am not authenticated"
        def exception = thrown ConstraintViolationException
        assertThat(exception).containsExactly("arg0.email", "must not be null")
    }

    def "A user cannot authenticate without a password"() {
        when: "I authenticate without a password"
        Request request = Request.newBuilder().withEmail(Email.of("test@custodix.com")).build()
        authenticate.authenticate(request)

        then: "I am not authenticated"
        def exception = thrown ConstraintViolationException
        assertThat(exception).containsExactly("arg0.password", "must not be null")
    }
}
