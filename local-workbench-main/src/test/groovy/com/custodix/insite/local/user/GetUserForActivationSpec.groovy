package com.custodix.insite.local.user

import eu.ehr4cr.workbench.local.model.security.User
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

import static com.custodix.insite.local.user.GetUserForActivation.*
import static org.assertj.core.api.Java6Assertions.assertThat

@Title("Get user for activation")
class GetUserForActivationSpec extends AbstractSpecification {
    private static final String INCORRECT_TEMPORARY_PASSWORD = "Incorrect temporary password"

    @Autowired
    private GetUserForActivation getUserForActivation

    def "An invited user can view the details to complete his account activation"() {
        given: "An invited user"
        User user = users.anInvitedUser()

        when: "The user views the details to complete his account activation"
        Request request = createRequest(user.getId(), user.getTempPassword())
        Response response = getUserForActivation.getUser(request)

        then: "His user info is returned"
        assertThat(response.getUserId()).isEqualTo(user.getId())
        assertThat(response.getUserEmail()).isEqualTo(user.getEmail())
        assertThat(response.isUserExpired()).isEqualTo(user.isExpired())

        and: "The list of security questions is returned"
        assertThat(response.getSecurityQuestions()).isNotEmpty()
    }

    def "An invited user cannot view the details to complete his account activation with an incorrect temporary password"() {
        given: "An invited user"
        User user = users.anInvitedUser()

        when: "The user views the details to complete his account activation with an incorrect temporary password"
        Request request = createRequest(user.getId(), INCORRECT_TEMPORARY_PASSWORD)
        getUserForActivation.getUser(request)

        then: "The details are not returned"
        thrown InvitationCompletionInvalidException
    }

    private Request createRequest(long userId, String tempPassword) {
        return Request.newBuilder()
                .withUserId(userId)
                .withTempPassword(tempPassword)
                .withLocale(Locale.getDefault())
                .build()
    }
}
