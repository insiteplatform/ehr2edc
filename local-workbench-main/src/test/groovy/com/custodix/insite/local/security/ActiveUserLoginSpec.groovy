package com.custodix.insite.local.security

import com.custodix.insite.local.user.AbstractSpecification
import eu.ehr4cr.workbench.local.model.security.User
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

import static com.custodix.insite.local.cohort.scenario.objectMother.Users.PASSWORD
import static eu.ehr4cr.workbench.local.WebRoutes.clinicalStudiesList
import static eu.ehr4cr.workbench.local.WebRoutes.login
import static org.assertj.core.api.Assertions.assertThat
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ActiveUserLoginSpec extends AbstractSpecification {

    private User user

    def setup() {
        user = users.persist(users.anActiveUser())
    }

    def "A user gets redirected to his original url after he logs in successfully"() {
        given: "I am not logged in"
        users.logout()
        def session = mockSession()
        String initialUrlBeforeLogin = clinicalStudiesList

        when: "I browse to an existing url in the application"
        MockHttpServletResponse response = mockMvc.perform(get(initialUrlBeforeLogin).session(session))
                .andExpect(status().is3xxRedirection())
                .andReturn()
                .getResponse()

        then: "I am redirected to the login page"
        assertThat(response.getRedirectedUrl()).endsWith(login)

        when: "I fill in the login page with a valid user and password"
        response = mockMvc.perform(createLoginRequest(session))
                .andExpect(status().is3xxRedirection())
                .andReturn()
                .getResponse()

        then: "I get redirected to the url I originally entered"
        assertThat(response.getRedirectedUrl()).endsWith(initialUrlBeforeLogin)
    }

    private MockHttpSession mockSession() {
        MockHttpSession session = new MockHttpSession()
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext())
        return session
    }

    private MockHttpServletRequestBuilder createLoginRequest(MockHttpSession session) {
        return post(login)
                .param("username", user.email)
                .param("password", PASSWORD)
                .session(session)
                .with(csrf())
    }
}