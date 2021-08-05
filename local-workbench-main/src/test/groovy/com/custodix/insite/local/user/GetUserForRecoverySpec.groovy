package com.custodix.insite.local.user

import eu.ehr4cr.workbench.local.model.security.User
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

import static com.custodix.insite.local.user.GetUserForRecovery.*
import static org.assertj.core.api.Java6Assertions.assertThat

@Title("Get user for recovery")
class GetUserForRecoverySpec extends AbstractSpecification {
    private static final String INCORRECT_TEMPORARY_PASSWORD = "Incorrect temporary password"

    @Autowired
    private GetUserForRecovery getUserForRecovery

    def "A recovering user can view the details to complete his account recovery"() {
        given: "A recovering user"
        User user = users.aRecoveringUser()

        when: "The user views the details to complete his account recovery"
        Request request = createRequest(user.getId(), user.getTempPassword())
        Response response = getUserForRecovery.getUser(request)

        then: "His user info is returned"
        assertThat(response.getUserId()).isEqualTo(user.getId())
        assertThat(response.getUserEmail()).isEqualTo(user.getEmail())
        assertThat(response.isUserExpired()).isEqualTo(user.isExpired())
    }

    def "A recovering user cannot view the details to complete his account recovery with an incorrect temporary password"() {
        given: "A recovering user"
        User user = users.aRecoveringUser()

        when: "The user views the details to complete his account recovery with an incorrect temporary password"
        Request request = createRequest(user.getId(), INCORRECT_TEMPORARY_PASSWORD)
        getUserForRecovery.getUser(request)

        then: "The details are not returned"
        thrown RecoveryCompletionInvalidException
    }

    private Request createRequest(long userId, String tempPassword) {
        return Request.newBuilder()
                .withUserId(userId)
                .withTempPassword(tempPassword)
                .build()
    }
}
