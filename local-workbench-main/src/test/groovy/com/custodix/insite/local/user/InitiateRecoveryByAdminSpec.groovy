package com.custodix.insite.local.user

import com.custodix.insite.local.shared.exceptions.UserException
import com.custodix.insite.local.user.application.api.InitiateRecovery
import eu.ehr4cr.workbench.local.dao.SecurityDao
import eu.ehr4cr.workbench.local.exception.security.InvalidPermissionsException
import eu.ehr4cr.workbench.local.global.AuthorityType
import eu.ehr4cr.workbench.local.model.security.User
import eu.ehr4cr.workbench.local.properties.AccountSecuritySettings
import eu.ehr4cr.workbench.local.service.email.model.ImmutableUserRecoverMailContent
import eu.ehr4cr.workbench.local.service.email.model.MailContent
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

import javax.validation.ConstraintViolationException

import static com.custodix.insite.local.user.application.api.InitiateRecovery.ByAdminRequest
import static eu.ehr4cr.workbench.local.model.security.UserStatus.*
import static eu.ehr4cr.workbench.local.service.DomainTime.now
import static eu.ehr4cr.workbench.local.service.email.TestMailService.RecipientMail
import static eu.ehr4cr.workbench.local.testingutils.ConstraintViolationAssert.assertThat

@Title("User password recovery can be initiated by an administrator")
class InitiateRecoveryByAdminSpec extends AbstractSpecification {
    @Autowired
    InitiateRecovery initiateRecovery
    @Autowired
    SecurityDao securityDao
    @Autowired
    AccountSecuritySettings accountSecuritySettings

    @Unroll
    def "A user with 'MANAGE_ACCOUNTS' authority can initiate password recovery for a #status user"() {
        given: "I am a user with 'MANAGE_ACCOUNTS' authority"
        User authenticatedUser = users.aUserWithAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(authenticatedUser)
        and: "A #status user"
        User user = users.aUser(status)

        when: "I initiate password recovery for the user"
        initiateRecovery.initiateByAdmin(createRequest(user.identifier))

        then: "The user's account is recovering"
        User validateUser = securityDao.findUserById(user.identifier)
        validateUser.status == RECOVERING
        !validateUser.tempPassword.empty
        validateUser.tempPasswordExpirationDate == new Date(now().getTime() + accountSecuritySettings.inviteExpireUnit.toMillis(accountSecuritySettings.inviteExpireValue))

        and: "The user receives a recovery e-mail"
        RecipientMail mail = testMailService.poll() as RecipientMail
        mail.recipient == user.email
        mail.content == createExpectedMailContent(user)

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    @Unroll
    def "A user with 'MANAGE_ACCOUNTS' authority cannot initiate password recovery for a #status user"() {
        given: "I am a user with 'MANAGE_ACCOUNTS' authority"
        User authenticatedUser = users.aUserWithAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(authenticatedUser)
        and: "A #status user"
        User user = users.aUser(status)

        when: "I initiate password recovery for the user"
        initiateRecovery.initiateByAdmin(createRequest(user.identifier))

        then: "The user's account is not recovering"
        User validateUser = securityDao.findUserById(user.identifier)
        validateUser.status == status

        and: "I receive a clear error message"
        def exception = thrown UserException
        exception.message == "Recovery unavailable for this account"

        and: "The user does not receive a recovery e-mail"
        testMailService.empty

        where:
        status             | _
        PENDING            | _
        INVITED            | _
        EXPIRED_INVITATION | _
        DELETED            | _
    }

    def "A user with 'MANAGE_ACCOUNTS' authority cannot initiate password recovery for a user that does not exist"() {
        given: "I am a user with 'MANAGE_ACCOUNTS' authority"
        User authenticatedUser = users.aUserWithAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(authenticatedUser)

        when: "I initiate password recovery for the user"
        def userIdentifier = UserIdentifier.of(999L)
        initiateRecovery.initiateByAdmin(createRequest(userIdentifier))

        then: "I receive a clear error message"
        def exception = thrown UserException
        exception.message == "Recovery unavailable for this account"
    }

    @Unroll
    def "A user without 'MANAGE_ACCOUNTS' authority cannot initiate password recovery for a #status user"() {
        given: "I am a user without 'MANAGE_ACCOUNTS' authority"
        User authenticatedUser = users.aUserWithoutAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(authenticatedUser)
        and: "A #status user"
        User user = users.aUser(status)

        when: "I initiate password recovery for the user"
        initiateRecovery.initiateByAdmin(createRequest(user.identifier))

        then: "The user's account is not recovering"
        User validateUser = securityDao.findUserById(user.identifier)
        validateUser.status == status

        and: "Access is denied"
        thrown InvalidPermissionsException

        and: "The user does not receive a recovery e-mail"
        testMailService.empty

        where:
        status             | _
        ACTIVE             | _
        RECOVERING         | _
        EXPIRED_RECOVERY   | _
        PENDING            | _
        INVITED            | _
        EXPIRED_INVITATION | _
        DELETED            | _
    }

    def "Password recovery cannot be initiated without a user identifier"() {
        given: "I am a user with 'MANAGE_ACCOUNTS' authority"
        User authenticatedUser = users.aUserWithAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(authenticatedUser)

        when: "I initiate password recovery without a user identifier"
        initiateRecovery.initiateByAdmin(createRequest(null))

        then: "I receive a clear error message"
        def exception = thrown ConstraintViolationException
        assertThat(exception).containsExactly("arg0.userIdentifier", "must not be null")
    }

    private MailContent createExpectedMailContent(User user) {
        return ImmutableUserRecoverMailContent.builder()
                .recoverExpireValue(accountSecuritySettings.getInviteExpireValue())
                .recoverExpireUnit(accountSecuritySettings.getInviteExpireUnit())
                .recoverAcceptUrl(accountSecuritySettings.getRecoveryUri(user.getId(), user.getTempPassword()))
                .build()
    }

    ByAdminRequest createRequest(UserIdentifier userIdentifier) {
        return ByAdminRequest.newBuilder()
                .withUserIdentifier(userIdentifier)
                .build()
    }
}
