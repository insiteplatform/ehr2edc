package com.custodix.insite.local.security

import com.custodix.insite.local.user.AbstractSpecification
import eu.ehr4cr.workbench.local.model.security.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

import static com.custodix.insite.local.cohort.scenario.objectMother.Users.PASSWORD
import static eu.ehr4cr.workbench.local.WebRoutes.login
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
class DeletedUserLoginSpec extends AbstractSpecification {
    @Autowired
    private MockMvc mockMvc

    def "A user that is deleted can not login"() {
        given: "I am a user that is deleted"
        User user = users.aDeletedUser()

        when: "I fill in the login page with a valid username and password"
        MockHttpServletResponse response = mockMvc.perform(createLoginRequest(user))
                .andExpect(status().is3xxRedirection())
                .andReturn()
                .getResponse()

        then: "I am still on the login page"
        response.status == HttpStatus.FOUND.value()
        response.redirectedUrl == login
    }

    private MockHttpServletRequestBuilder createLoginRequest(User user) {
        return post(login)
                .param("username", user.email)
                .param("password", PASSWORD)
                .with(csrf())
    }
}
