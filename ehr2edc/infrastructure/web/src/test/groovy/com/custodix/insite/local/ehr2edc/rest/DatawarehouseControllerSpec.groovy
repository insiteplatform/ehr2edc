package com.custodix.insite.local.ehr2edc.rest

import com.custodix.insite.local.ehr2edc.command.DatawarehouseUpdated
import com.custodix.insite.local.ehr2edc.rest.util.MockRemoteAddr
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.request.RequestPostProcessor

import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyZeroInteractions
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@ContextConfiguration(classes = [DatawarehouseController])
@TestPropertySource(properties = ["ehr2edc.api.admin.credentials.username=testUser", "ehr2edc.api.admin.credentials.password=testPassword"])
class DatawarehouseControllerSpec extends ControllerSpec {
    static final BASE_ENDPOINT = "/ehr2edc/datawarehouse"

    @MockBean
    private DatawarehouseUpdated datawarehouseUpdated

    def "An unauthenticated request to the datawarehouse updated endpoint"() {
        given: "A valid request"
        def request = post(BASE_ENDPOINT)

        when: "The endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response should indicate a forbidden operation"
        response.status == HttpStatus.FORBIDDEN.value()
        verifyZeroInteractions(datawarehouseUpdated)
    }

    def "An authenticated request to the datawarehouse updated endpoint with incorrect credentials"() {
        given: "A valid request"
        def request = post(BASE_ENDPOINT).with(httpBasic("failingTestUser", "failingTestPassword"))

        when: "The endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response should indicate a unauthorized operation"
        response.status == HttpStatus.UNAUTHORIZED.value()
        verifyZeroInteractions(datawarehouseUpdated)
    }

    def "An authenticated request to the datawarehouse updated endpoint with incorrect credentials from localhost"(RequestPostProcessor localhost) {
        given: "A valid request"
        def request = post(BASE_ENDPOINT).with(httpBasic("failingTestUser", "failingTestPassword")).with(localhost)

        when: "The endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response should indicate a unauthorized operation"
        response.status == HttpStatus.UNAUTHORIZED.value()
        verifyZeroInteractions(datawarehouseUpdated)

        where:
        localhost                      | _
        MockRemoteAddr.localhostIPv4() | _
        MockRemoteAddr.localhostIPv6() | _
    }

    def "An authenticated request to the datawarehouse updated endpoint with correct credentials from localhost"(RequestPostProcessor localhost) {
        given: "A valid request"
        def request = post(BASE_ENDPOINT).with(httpBasic("testUser", "testPassword")).with(localhost)

        when: "The endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response should indicate a successful operation"
        response.status == HttpStatus.ACCEPTED.value()
        verify(datawarehouseUpdated).update()

        where:
        localhost                      | _
        MockRemoteAddr.localhostIPv4() | _
        MockRemoteAddr.localhostIPv6() | _
    }


    def "An authenticated request to the datawarehouse updated endpoint with correct credentials from remote"() {
        given: "A valid request"
        def request = post(BASE_ENDPOINT).with(httpBasic("testUser", "testPassword")).with(MockRemoteAddr.nonLocalhost())

        when: "The endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response should indicate a forbidden operation"
        response.status == HttpStatus.FORBIDDEN.value()
        verifyZeroInteractions(datawarehouseUpdated)
    }
}
