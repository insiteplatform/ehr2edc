package com.custodix.insite.local.actuator

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import spock.lang.Narrative
import spock.lang.Title

import static org.assertj.core.api.Java6Assertions.assertThat
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Title("Actuator Prometheus")
@Narrative("The micrometer Prometheus actuator should only be visible when you are authenticated with VIEW_ACTUATOR_DETAILS role")
class PrometheusSpec extends ActuatorSpec {
    def "The Prometheus actuator endpoint is visible when you are authenticated with ActuatorDetails_View role"() {
        given: "The Prometheus actuator endpoint"
        when: "a call is made as an authenticated user with VIEW_ACTUATOR_DETAILS role"
        def perform = mvc.perform(get("/actuator/prometheus").with(httpBasic("actuator@custodix.com", "test")))
        then: "the endpoint responds with extra information"
        perform.andExpect(status().isOk())
        assertThat(perform.andReturn().getResponse().getContentAsString()).isNotBlank()
    }

    def "The Prometheus actuator endpoint is not visible when you are not authenticated"() {
        given: "The Prometheus actuator endpoint"
        when: "a call is made without authentication"
        def perform = mvc.perform(get("/actuator/prometheus"))
        then: "the endpoint redirects to login"
        perform.andExpect(status().isFound()).andExpect(redirectedUrl("http://localhost/auth/login"))
    }

    def "The Prometheus actuator endpoint is not visible when you are authenticated without ActuatorDetails_View role"() {
        given: "The Prometheus actuator endpoint"
        when: "a call is made as an authenticated user without VIEW_ACTUATOR_DETAILS role"
        def perform = mvc.perform(get("/actuator/prometheus").with(authentication(createUnauthorizedAuthentication())))
        then: "the endpoint is forbidden"
        perform.andExpect(status().isForbidden())
    }

    private UsernamePasswordAuthenticationToken createUnauthorizedAuthentication() {
        return new UsernamePasswordAuthenticationToken("actuator@custodix.com", "unauthorizedPassword",
                new ArrayList<GrantedAuthority>());
    }
}
