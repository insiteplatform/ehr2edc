package com.custodix.insite.local.ehr2edc.infra.edc.openclinica

import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionObjectMother
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.util.UriTemplate

import static com.github.tomakehurst.wiremock.client.WireMock.*

class AuthenticationTokenProviderSpec extends AbstractOpenClinicaSpecification {
    private static final UriTemplate URI_TEMPLATE = new UriTemplate("http://localhost:{port}/user-service/api/oauth/token")

    @Autowired
    OpenClinicaRestClient restClient

    AuthenticationTokenProvider provider

    def setup() {
        provider = new AuthenticationTokenProvider(getUri(), restClient)
    }

    def "Get authentication token for a connection with valid credentials"() {
        given: "a connection"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aDefaultStudyConnection()
        and: "the connection credentials are valid"
        stubResponseSuccess()

        when: "I get an authentication token for the connection"
        AuthenticationToken token = provider.get(connection)

        then: "the authentication token is returned"
        token == AUTHENTICATION_TOKEN
    }

    def "Get authentication token for a connection with invalid credentials throws exception"() {
        given: "a connection"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aDefaultStudyConnection()
        and: "the connection credentials are invalid"
        stubResponseError()

        when: "I get an authentication token for the connection"
        provider.get(connection)

        then: "an exception is thrown"
        thrown SystemException
    }

    private stubResponseSuccess() {
        wireMockRule.givenThat(post(getUri().getPath()).withRequestBody(equalToJson(readFile("openclinica/authentication-request.json")))
                .willReturn(aResponse().withStatus(200).withBody(AUTHENTICATION_TOKEN.value)))
    }

    private stubResponseError() {
        wireMockRule.givenThat(post(getUri().getPath()).withRequestBody(equalToJson(readFile("openclinica/authentication-request.json")))
                .willReturn(aResponse().withStatus(500).withBody("An error occurred")))
    }

    private URI getUri() {
        return URI_TEMPLATE.expand(wireMockRule.port())
    }
}
