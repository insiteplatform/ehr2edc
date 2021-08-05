package com.custodix.insite.local.user

import com.custodix.insite.local.user.vocabulary.PasswordExpirySettings
import eu.ehr4cr.workbench.local.model.security.User
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

import static com.custodix.insite.local.user.GetAccount.Request
import static com.custodix.insite.local.user.GetAccount.Response

class GetAccountSpec extends AbstractSpecification {
    @Autowired
    private GetAccount getAccount
    @Autowired
    private PasswordExpirySettings passwordExpirySettings;

    def "A user can retrieve his account"() {
        given: "A user"
        User user = users.aRegularUser()
        users.login(user)

        when: "The user retrieves his account"
        Request request = createRequest(user)
        Response response = getAccount.getAccount(request)

        then: "The user's account is returned"
        with(response) {
            userEmail == user.email
            userName == user.username
            passwordExpiryDate == Optional.of(DateUtils.addDays(user.passwordLastModified, passwordExpirySettings.expiryDuration.toDays() as int))
            securityQuestionId == user.securityQuestionId
            securityQuestionAnswer == user.securityAnswer
            questions.text == ["What was your childhood nickname?",
                               "What is the name of your favorite childhood friend?",
                               "In what city or town did your mother and father meet?"]
        }
    }

    def "A user cannot retrieve the account of another user"() {
        given: "A user"
        User user = users.aRegularUser()
        users.login(user)

        and: "Another user"
        User anotherUser = users.anAuthor()

        when: "The user retrieves the account of the other user"
        Request request = createRequest(anotherUser)
        getAccount.getAccount(request)

        then: "Access is denied"
        thrown AccessDeniedException
    }

    private Request createRequest(User user) {
        return Request.newBuilder()
                .withUserIdentifier(user.getIdentifier())
                .withLocale(Locale.getDefault())
                .build()
    }
}
