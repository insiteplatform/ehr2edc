package com.custodix.insite.local.security

import com.custodix.insite.local.user.AbstractSpecification
import com.custodix.insite.local.user.vocabulary.Password
import eu.ehr4cr.workbench.local.model.security.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

import static eu.ehr4cr.workbench.local.WebRoutes.login
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
class PasswordExpiredLoginSpec extends AbstractSpecification {
    @Autowired
    private MockMvc mockMvc

    def "A user that has an expired password cannot login"() {
        given: "I am an active user"
        User user = users.anActiveUser()
        and: "My password has expired"
        Password expiredPassword = users.setExpiredPassword(user)

        when: "I fill in the login page with my email and password"
        MockHttpServletResponse response = mockMvc.perform(createLoginRequest(user, expiredPassword))
                .andExpect(status().is3xxRedirection())
                .andReturn()
                .getResponse()

        then: "I am still on the login page"
        response.status == HttpStatus.FOUND.value()
        response.redirectedUrl == login
    }

    private MockHttpServletRequestBuilder createLoginRequest(User user, Password password) {
        return post(login)
                .param("username", user.email)
                .param("password", password.value)
                .with(csrf())
    }
}
