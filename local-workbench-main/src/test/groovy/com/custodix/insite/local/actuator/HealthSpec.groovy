package com.custodix.insite.local.actuator

import spock.lang.Narrative
import spock.lang.Title

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@Title("Actuator Health")
@Narrative("The health actuator should only show extra information when you are authenticated with VIEW_ACTUATOR_DETAILS role")
class HealthSpec extends ActuatorSpec {
    def "The health actuator endpoint gives more information when you are authenticated with ActuatorDetails_View role"() {
        given: "The health actuator endpoint"
        when: "a call is made with configured authentication credentials"
        def perform = mvc.perform(get("/actuator/health").with(httpBasic("actuator@custodix.com", "test")))
        then: "the endpoint responds with extra information"
        perform.andExpect(jsonPath("\$.details").exists())
    }

    def "The health actuator endpoint gives basic information when you are not authenticated"() {
        given: "The health actuator endpoint"
        when: "a call is made without authentication"
        def perform = mvc.perform(get("/actuator/health"))
        then: "the endpoint responds without extra information"
        perform.andExpect(jsonPath("\$.details").doesNotExist())
    }

    def "The health actuator endpoint gives basic information when you are wrongly authenticated"() {
        given: "The health actuator endpoint"
        when: "a call is made with a wrong authentication"
        def perform = mvc.perform(get("/actuator/health").with(httpBasic("actuator@custodix.com", "testPassword2")))
        then: "the endpoint responds without extra information"
        perform.andExpect(jsonPath("\$.details").doesNotExist())
    }
}
