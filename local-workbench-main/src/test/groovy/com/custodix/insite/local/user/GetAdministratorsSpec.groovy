package com.custodix.insite.local.user

import com.custodix.insite.local.cohort.scenario.UserGroup
import com.custodix.insite.local.user.AbstractSpecification
import com.custodix.insite.local.user.GetAdministrators
import eu.ehr4cr.workbench.local.model.security.User
import org.springframework.beans.factory.annotation.Autowired

import static com.custodix.insite.local.user.GetAdministrators.Response
import static org.assertj.core.api.Java6Assertions.assertThat

class GetAdministratorsSpec extends AbstractSpecification {
    @Autowired
    private GetAdministrators getAdministrators

    def "A user can retrieve the administrators of the application"() {
        given: "A user"
        User user = users.aRegularUser()
        users.login(user)

        and: "An administrator"
        User admin = users.aUser(UserGroup.ADMINISTRATOR)

        when: "The user retrieves the administrators"
        Response response = getAdministrators.getAdministrators()

        then: "The administrators are returned"
        validateAdministrators(response, admin)
    }

    private void validateAdministrators(Response response, User admin) {
        List<String> adminEmails = response.getEmails()
        assertThat(adminEmails).hasSize(2)
        assertThat(adminEmails).contains(admin.getEmailAddress().getValue())
    }
}
