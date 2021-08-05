package com.custodix.insite.local.user

import com.custodix.insite.local.cohort.scenario.objectMother.Users
import com.custodix.insite.local.user.application.api.Authenticate
import com.custodix.insite.local.user.domain.AuthenticationAttemptResult
import com.custodix.insite.local.user.domain.AuthenticationAttempts
import com.custodix.insite.local.user.domain.repository.AuthenticationAttemptRepository
import com.custodix.insite.local.user.vocabulary.Email
import com.custodix.insite.local.user.vocabulary.Password
import eu.ehr4cr.workbench.local.model.security.User
import eu.ehr4cr.workbench.local.model.security.UserStatus
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.util.stream.Collectors

import static com.custodix.insite.local.user.application.api.Authenticate.Request
import static com.custodix.insite.local.user.application.api.Authenticate.Response
import static com.custodix.insite.local.user.vocabulary.AuthenticateResult.*
import static eu.ehr4cr.workbench.local.model.security.UserStatus.*
import static eu.ehr4cr.workbench.local.service.DomainTime.now
import static java.time.temporal.ChronoUnit.MINUTES

class AuthenticateLockSpec extends AbstractSpecification {
    private static final PASSWORD = Password.of(Users.PASSWORD)

    @Autowired
    private Authenticate authenticate
    @Autowired
    private AuthenticationAttemptRepository authenticationAttemptRepository

    @Unroll
    def "A user cannot authenticate to a #status account that is locked"() {
        given: "A #status account"
        User user = users.aUser(status as UserStatus)
        and: "The account is locked"
        authenticationAttempts.setLocked(user.emailAddress)

        when: "I authenticate using the email and password"
        Response response = authenticate.authenticate(createRequest(user.emailAddress, PASSWORD))

        then: "I am not authenticated"
        response.result == ACCOUNT_LOCKED
        response.message.key == "login.accountLocked"
        response.message.parameters == [Date.from(now().toInstant().plus(1L, MINUTES))]

        where:
        status << Arrays.stream(UserStatus.values()).filter({ s -> s != UNKNOWN }).collect(Collectors.toList())
    }

    @Unroll
    def "A user can authenticate to a #status account that was locked and has been explicitly unlocked"() {
        given: "A #status account"
        User user = users.aUser(status as UserStatus)
        and: "The account was locked"
        authenticationAttempts.setLocked(user.emailAddress)
        and: "The account has been explicitly unlocked"
        authenticationAttempts.unlock(user.emailAddress)

        when: "I authenticate using the email and password"
        Response response = authenticate.authenticate(createRequest(user.emailAddress, PASSWORD))

        then: "The account is unlocked"
        response.result == SUCCESS

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    @Unroll
    def "An authentication attempt for a #status account is recorded"() {
        given: "A #status account"
        User user = users.aUser(status)

        when: "I authenticate using the email and password"
        authenticate.authenticate(createRequest(user.emailAddress, PASSWORD))

        then: "My authentication attempt is recorded"
        AuthenticationAttempts attempts = authenticationAttemptRepository.getAttempts(user.emailAddress)
        attempts.attempts.size() == 1
        with(attempts.attempts.get(0)) {
            email == user.emailAddress
            result == AuthenticationAttemptResult.SUCCESS
            timestamp == now()
        }

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    @Unroll
    def "An authentication attempt for a #status account with an incorrect password is recorded"() {
        given: "A #status account"
        User user = users.aUser(status as UserStatus)

        when: "I authenticate using the email and an incorrect password"
        authenticate.authenticate(createRequest(user.emailAddress, (Password.of("test"))))

        then: "My authentication attempt is recorded"
        AuthenticationAttempts attempts = authenticationAttemptRepository.getAttempts(user.emailAddress)
        attempts.attempts.size() == 1
        with(attempts.attempts.get(0)) {
            email == user.emailAddress
            result == AuthenticationAttemptResult.BAD_CREDENTIALS
            timestamp == now()
        }

        where:
        status << Arrays.stream(UserStatus.values()).filter({ s -> s != UNKNOWN }).collect(Collectors.toList())
    }

    @Unroll
    def "An authentication attempt for a #status account with an expired password is recorded"() {
        given: "A #status account"
        User user = users.aUser(status)
        and: "The account's password has expired"
        Password expiredPassword = users.setExpiredPassword(user)

        when: "I authenticate using the email and the expired password"
        authenticate.authenticate(createRequest(user.emailAddress, expiredPassword))

        then: "My authentication attempt is recorded"
        AuthenticationAttempts attempts = authenticationAttemptRepository.getAttempts(user.emailAddress)
        attempts.attempts.size() == 1
        with(attempts.attempts.get(0)) {
            email == user.emailAddress
            result == AuthenticationAttemptResult.PASSWORD_EXPIRED
            timestamp == now()
        }

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    @Unroll
    def "An authentication attempt for a #status account that is locked is recorded"() {
        given: "A #status account"
        User user = users.aUser(status as UserStatus)
        and: "The account is locked"
        authenticationAttempts.setLocked(user.emailAddress)

        when: "I authenticate using the email and password"
        authenticate.authenticate(createRequest(user.emailAddress, PASSWORD))

        then: "My authentication attempt is recorded"
        AuthenticationAttempts attempts = authenticationAttemptRepository.getAttempts(user.emailAddress)
        with(attempts.attempts.get(attempts.attempts.size() - 1)) {
            email == user.emailAddress
            result == AuthenticationAttemptResult.ACCOUNT_LOCKED
            timestamp == now()
        }

        where:
        status << Arrays.stream(UserStatus.values()).filter({ s -> s != UNKNOWN }).collect(Collectors.toList())
    }

    def "An authentication attempt for an account that does not exist is recorded"() {
        given: "An account that does not exist"
        Email email = Email.of("test@custodix.com")

        when: "I authenticate using the email that does not exist"
        authenticate.authenticate(createRequest(email, PASSWORD))

        then: "My authentication attempt is recorded"
        AuthenticationAttempts attempts = authenticationAttemptRepository.getAttempts(email)
        attempts.attempts.size() == 1
        with(attempts.attempts.get(0)) {
            email == email
            result == AuthenticationAttemptResult.BAD_CREDENTIALS
            timestamp == now()
        }
    }

    @Unroll
    def "An authentication for a #status account with an incorrect password on the last attempt locks the account"() {
        given: "A #status account"
        User user = users.aUser(status as UserStatus)
        and: "The account has only one authentication attempt left"
        authenticationAttempts.setOneAttemptLeft(user.emailAddress)

        when: "I authenticate using the email and an incorrect password"
        authenticate.authenticate(createRequest(user.emailAddress, (Password.of("test"))))

        then: "The account is locked"
        Response response = authenticate.authenticate(createRequest(user.emailAddress, PASSWORD))
        response.result == ACCOUNT_LOCKED

        where:
        status << Arrays.stream(UserStatus.values()).filter({ s -> s != UNKNOWN }).collect(Collectors.toList())
    }

    @Unroll
    def "An authentication for a #status account with an expired password on the last attempt does not lock the account"() {
        given: "A #status account"
        User user = users.aUser(status as UserStatus)
        and: "The account has only one authentication attempt left"
        authenticationAttempts.setOneAttemptLeft(user.emailAddress)
        and: "The account's password has expired"
        Password expiredPassword = users.setExpiredPassword(user)

        when: "I authenticate using the email and the expired password"
        authenticate.authenticate(createRequest(user.emailAddress, expiredPassword))

        then: "The account is not locked"
        Response response = authenticate.authenticate(createRequest(user.emailAddress, expiredPassword))
        response.result != ACCOUNT_LOCKED

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    def "An authentication for an account that does not exist on the last attempt locks the account"() {
        given: "An account that does not exist"
        Email email = Email.of("test@custodix.com")
        and: "The account has only one authentication attempt left"
        authenticationAttempts.setOneAttemptLeft(email)

        when: "I authenticate using the email that does not exist"
        authenticate.authenticate(createRequest(email, PASSWORD))

        then: "The account is locked"
        Response response = authenticate.authenticate(createRequest(email, PASSWORD))
        response.result == ACCOUNT_LOCKED
    }

    @Unroll
    def "An authentication for a #status account with an incorrect password on the last attempt with a sufficient delay does not lock the account"() {
        given: "A #status account"
        User user = users.aUser(status as UserStatus)
        and: "The account has only one authentication attempt left with a sufficient delay"
        authenticationAttempts.setOneAttemptLeftWithSufficientDelay(user.emailAddress)

        when: "I authenticate using the email and an incorrect password"
        authenticate.authenticate(createRequest(user.emailAddress, (Password.of("test"))))

        then: "The account is not locked"
        Response response = authenticate.authenticate(createRequest(user.emailAddress, PASSWORD))
        response.result != ACCOUNT_LOCKED

        where:
        status << Arrays.stream(UserStatus.values()).filter({ s -> s != UNKNOWN }).collect(Collectors.toList())
    }

    def "An authentication for an account that does not exist on the last attempt with a sufficient delay does not lock the account"() {
        given: "An account that does not exist"
        Email email = Email.of("test@custodix.com")
        and: "The account has only one authentication attempt left with a sufficient delay"
        authenticationAttempts.setOneAttemptLeftWithSufficientDelay(email)

        when: "I authenticate using the email that does not exist"
        authenticate.authenticate(createRequest(email, PASSWORD))

        then: "The account is not locked"
        Response response = authenticate.authenticate(createRequest(email, PASSWORD))
        response.result != ACCOUNT_LOCKED
    }

    @Unroll
    def "An authentication for a #status account on the last attempt does not lock the account"() {
        given: "A #status account"
        User user = users.aUser(status as UserStatus)
        and: "The account has only one authentication attempt left"
        authenticationAttempts.setOneAttemptLeft(user.emailAddress)

        when: "I authenticate using the email and password"
        authenticate.authenticate(createRequest(user.emailAddress, PASSWORD))

        then: "The account is not locked"
        Response response = authenticate.authenticate(createRequest(user.emailAddress, PASSWORD))
        response.result == SUCCESS

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    @Unroll
    def "An authentication for a #status account resets the remaining authentication attempts"() {
        given: "A #status account"
        User user = users.aUser(status as UserStatus)
        and: "The account has only one authentication attempt left"
        authenticationAttempts.setOneAttemptLeft(user.emailAddress)

        when: "I authenticate using the email and password"
        authenticate.authenticate(createRequest(user.emailAddress, PASSWORD))
        and: "I authenticate once more with an incorrect password"
        authenticate.authenticate(createRequest(user.emailAddress, Password.of("test")))

        then: "The account is not locked"
        Response response = authenticate.authenticate(createRequest(user.emailAddress, PASSWORD))
        response.result == SUCCESS

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    @Unroll
    def "An authentication for a #status account that has been locked for as long as the lock interval unlocks the account"() {
        given: "A #status account"
        User user = users.aUser(status as UserStatus)
        and: "The account has been locked for as long as the lock interval"
        authenticationAttempts.setLockedForAsLongAsLockInterval(user.emailAddress)

        when: "I authenticate using the email and password"
        Response response = authenticate.authenticate(createRequest(user.emailAddress, PASSWORD))

        then: "The account is unlocked"
        response.result == SUCCESS

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    private Request createRequest(Email email, Password password) {
        return Request.newBuilder()
                .withEmail(email)
                .withPassword(password)
                .build()
    }
}
