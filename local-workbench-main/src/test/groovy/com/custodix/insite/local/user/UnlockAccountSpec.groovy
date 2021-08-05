package com.custodix.insite.local.user

import com.custodix.insite.local.cohort.scenario.objectMother.AuthenticationAttempts
import com.custodix.insite.local.user.application.api.UnlockAccount
import com.custodix.insite.local.user.domain.repository.AuthenticationAttemptRepository
import com.custodix.insite.local.user.vocabulary.Email
import eu.ehr4cr.workbench.local.exception.security.InvalidPermissionsException
import eu.ehr4cr.workbench.local.model.security.User
import eu.ehr4cr.workbench.local.model.security.UserStatus
import eu.ehr4cr.workbench.local.testingutils.ConstraintViolationAssert
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import javax.validation.ConstraintViolationException
import java.util.stream.Collectors

import static com.custodix.insite.local.user.application.api.UnlockAccount.Request
import static eu.ehr4cr.workbench.local.global.AuthorityType.MANAGE_ACCOUNTS
import static eu.ehr4cr.workbench.local.model.security.UserStatus.UNKNOWN

class UnlockAccountSpec extends AbstractSpecification {
    @Autowired
    UnlockAccount unlockAccount
    @Autowired
    AuthenticationAttempts authenticationAttempts
    @Autowired
    AuthenticationAttemptRepository authenticationAttemptRepository

    @Unroll
    def "A user with 'MANAGE_ACCOUNTS' authority can unlock a #status account"() {
        given: "I am a user with 'MANAGE_ACCOUNTS' authority"
        User authenticatedUser = users.aUserWithAuthority(MANAGE_ACCOUNTS)
        users.login(authenticatedUser)
        and: "A #status account"
        User user = users.aUser(status as UserStatus)
        and: "The account is locked"
        authenticationAttempts.setLocked(user.emailAddress)

        when: "I unlock the account"
        unlockAccount.unlockAccount(createRequest(user.emailAddress))

        then: "The account is unlocked"
        !authenticationAttemptRepository.getAttempts(user.emailAddress).isAccountLocked()

        where:
        status << Arrays.stream(UserStatus.values()).filter({ s -> s != UNKNOWN }).collect(Collectors.toList())
    }

    @Unroll
    def "A user without 'MANAGE_ACCOUNTS' authority cannot unlock a #status account"() {
        given: "I am a user without 'MANAGE_ACCOUNTS' authority"
        User authenticatedUser = users.aUserWithoutAuthority(MANAGE_ACCOUNTS)
        users.login(authenticatedUser)
        and: "A #status account"
        User user = users.aUser(status as UserStatus)
        and: "The account is locked"
        authenticationAttempts.setLocked(user.emailAddress)

        when: "I unlock the account"
        unlockAccount.unlockAccount(createRequest(user.emailAddress))

        then: "The account is still locked"
        authenticationAttemptRepository.getAttempts(user.emailAddress).isAccountLocked()
        and: "Access is denied"
        thrown InvalidPermissionsException

        where:
        status << Arrays.stream(UserStatus.values()).filter({ s -> s != UNKNOWN }).collect(Collectors.toList())
    }

    def "A user with 'MANAGE_ACCOUNTS' authority can unlock an account that does not exist"() {
        given: "I am a user with 'MANAGE_ACCOUNTS' authority"
        User authenticatedUser = users.aUserWithAuthority(MANAGE_ACCOUNTS)
        users.login(authenticatedUser)
        and: "An account that does not exist"
        def email = Email.of("non-existing-user@custodix.com")
        and: "The account is locked"
        authenticationAttempts.setLocked(email)

        when: "I unlock the account"
        unlockAccount.unlockAccount(createRequest(email))

        then: "The account is unlocked"
        !authenticationAttemptRepository.getAttempts(email).isAccountLocked()
    }

    def "An account cannot be unlocked without using an email"() {
        given: "I am a user with 'MANAGE_PLACEMENT' authority"
        User authenticatedUser = users.aUserWithAuthority(MANAGE_ACCOUNTS)
        users.login(authenticatedUser)

        when: "I unlock an account without using an email"
        unlockAccount.unlockAccount(createRequest(null))

        then: "I see a clear error message"
        def exception = thrown ConstraintViolationException
        ConstraintViolationAssert.assertThat(exception).containsExactly("arg0.email", "must not be null")
    }

    def "An account cannot be unlocked using an invalid email"() {
        given: "I am a user with 'MANAGE_PLACEMENT' authority"
        User authenticatedUser = users.aUserWithAuthority(MANAGE_ACCOUNTS)
        users.login(authenticatedUser)

        when: "I unlock an account without using an email"
        unlockAccount.unlockAccount(createRequest(Email.of("test")))

        then: "I see a clear error message"
        def exception = thrown ConstraintViolationException
        ConstraintViolationAssert.assertThat(exception).containsExactly("must be a well-formed email address")
    }

    private Request createRequest(Email email) {
        return Request.newBuilder()
                .withEmail(email)
                .build()
    }
}
