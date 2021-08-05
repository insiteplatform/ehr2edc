package com.custodix.insite.local.user

import com.custodix.insite.local.shared.exceptions.UserException
import com.custodix.insite.local.user.application.api.InitiateRecovery
import com.custodix.insite.local.user.vocabulary.Email
import eu.ehr4cr.workbench.local.dao.SecurityDao
import eu.ehr4cr.workbench.local.model.security.User
import eu.ehr4cr.workbench.local.properties.AccountSecuritySettings
import eu.ehr4cr.workbench.local.service.email.model.ImmutableUserRecoverMailContent
import eu.ehr4cr.workbench.local.service.email.model.MailContent
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

import javax.validation.ConstraintViolationException

import static com.custodix.insite.local.user.application.api.InitiateRecovery.BySecurityQuestionRequest
import static eu.ehr4cr.workbench.local.model.security.UserStatus.*
import static eu.ehr4cr.workbench.local.service.DomainTime.now
import static eu.ehr4cr.workbench.local.service.email.TestMailService.RecipientMail
import static eu.ehr4cr.workbench.local.testingutils.ConstraintViolationAssert.assertThat

@Title("User password recovery can be initiated by answering the security question")
class InitiateRecoveryBySecurityQuestionSpec extends AbstractSpecification {
    private static final String INCORRECT_ANSWER = "test"

    @Autowired
    InitiateRecovery initiateRecovery
    @Autowired
    SecurityDao securityDao
    @Autowired
    AccountSecuritySettings accountSecuritySettings

    @Unroll
    def "A #status user can initiate password recovery using his security question"() {
        given: "I am a #status user"
        User user = users.aUser(status)

        when: "I initiate password recovery using my security question answer"
        initiateRecovery.initiateBySecurityQuestion(createRequest(user.emailAddress, user.securityAnswer))

        then: "My account is recovering"
        User validateUser = securityDao.findUserById(user.identifier)
        validateUser.status == RECOVERING
        !validateUser.tempPassword.empty
        validateUser.tempPasswordExpirationDate == new Date(now().getTime() + accountSecuritySettings.inviteExpireUnit.toMillis(accountSecuritySettings.inviteExpireValue))

        and: "I receive a recovery e-mail"
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
    def "A #status user cannot initiate password recovery using an incorrect security question"() {
        given: "I am a #status user"
        User user = users.aUser(status)

        when: "I initiate password recovery using an incorrect security question answer"
        initiateRecovery.initiateBySecurityQuestion(createRequest(user.emailAddress, INCORRECT_ANSWER))

        then: "My account is not recovering"
        User validateUser = securityDao.findUserById(user.identifier)
        validateUser.status == status

        and: "I receive a clear error message"
        def exception = thrown UserException
        exception.message == "Incorrect security question answer"

        and: "I do not receive a recovery e-mail"
        testMailService.empty

        where:
        status           | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
    }

    @Unroll
    def "A #status user cannot initiate password recovery because he hasn't set his security question information yet"() {
        given: "I am a #status user"
        User user = users.aUser(status)

        when: "I initiate password recovery using an incorrect security question answer"
        initiateRecovery.initiateBySecurityQuestion(createRequest(user.emailAddress, INCORRECT_ANSWER))

        then: "My account is not recovering"
        User validateUser = securityDao.findUserById(user.identifier)
        validateUser.status == status

        and: "I receive a clear error message"
        def exception = thrown UserException
        exception.message == "Recovery unavailable for this account"

        and: "I do not receive a recovery e-mail"
        testMailService.empty

        where:
        status             | _
        PENDING            | _
        INVITED            | _
        EXPIRED_INVITATION | _
    }

    def "A DELETED user cannot initiate password recovery using his security question"() {
        given: "I am a DELETED user"
        User user = users.aDeletedUser()

        when: "I initiate password recovery using my security question answer"
        initiateRecovery.initiateBySecurityQuestion(createRequest(user.emailAddress, user.securityAnswer))

        then: "My account is not recovering"
        User validateUser = securityDao.findUserById(user.identifier)
        validateUser.status == DELETED

        and: "I receive a clear error message"
        def exception = thrown UserException
        exception.message == "Recovery unavailable for this account"

        and: "I do not receive a recovery e-mail"
        testMailService.empty
    }

    def "Password recovery cannot be initiated using an email for which no user exists"() {
        when: "I initiate password recovery using an email for which no user exists"
        Email email = Email.of("non-existing-user@custodix.com")
        initiateRecovery.initiateBySecurityQuestion(createRequest(email, INCORRECT_ANSWER))

        then: "I receive a clear error message"
        def exception = thrown UserException
        exception.message == "Recovery unavailable for this account"
    }

    def "Password recovery cannot be initiated using an invalid email"() {
        when: "I initiate password recovery using an invalid email"
        initiateRecovery.initiateBySecurityQuestion(createRequest(Email.of("test"), INCORRECT_ANSWER))

        then: "I receive a clear error message"
        def exception = thrown ConstraintViolationException
        assertThat(exception).containsExactly("must be a well-formed email address")
    }

    def "Password recovery cannot be initiated without an email"() {
        when: "I initiate password recovery without an email"
        initiateRecovery.initiateBySecurityQuestion(createRequest(null, INCORRECT_ANSWER))

        then: "I receive a clear error message"
        def exception = thrown ConstraintViolationException
        assertThat(exception).containsExactly("arg0.email", "must not be null")
    }

    def "Password recovery cannot be initiated using a blank security question answer"() {
        given: "I am an ACTIVE user"
        User user = users.anActiveUser()

        when: "I initiate password recovery using a blank security question answer"
        initiateRecovery.initiateBySecurityQuestion(createRequest(user.emailAddress, ""))

        then: "I receive a clear error message"
        def exception = thrown ConstraintViolationException
        assertThat(exception).containsExactly("arg0.questionAnswer", "must not be blank")
    }

    private MailContent createExpectedMailContent(User user) {
        return ImmutableUserRecoverMailContent.builder()
                .recoverExpireValue(accountSecuritySettings.getInviteExpireValue())
                .recoverExpireUnit(accountSecuritySettings.getInviteExpireUnit())
                .recoverAcceptUrl(accountSecuritySettings.getRecoveryUri(user.getId(), user.getTempPassword()))
                .build()
    }

    BySecurityQuestionRequest createRequest(Email email, String questionAnswer) {
        return BySecurityQuestionRequest.newBuilder()
                .withEmail(email)
                .withQuestionAnswer(questionAnswer)
                .build()
    }
}
