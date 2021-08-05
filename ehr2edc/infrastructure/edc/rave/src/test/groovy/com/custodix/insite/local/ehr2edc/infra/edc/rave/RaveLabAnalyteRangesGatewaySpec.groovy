package com.custodix.insite.local.ehr2edc.infra.edc.rave


import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalSiteId
import com.custodix.insite.local.ehr2edc.vocabulary.LabName
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.X509Certificate

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

class RaveLabAnalyteRangesGatewaySpec extends Specification {

    public static final String DEFAULT_URL_FOR_RETRIEVING_LABANALYTERANGES =
            "/RaveWebServices/datasets/LabAnalyteRanges.csv?studyoid=studyId"

    RaveLabAnalyteRangesGateway raveLabAnalyteRangesGateway

    HostnameVerifier defaultHostnameVerifier

    SSLSocketFactory defaultSSLSocketFactory

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
            wireMockConfig().containerThreads(13).dynamicHttpsPort().dynamicPort());

    def setup() {
        configureClientSSL()
        raveLabAnalyteRangesGateway = new RaveLabAnalyteRangesGateway(new RaveRestClient())
    }

    def cleanup() {
        HttpsURLConnection.setDefaultHostnameVerifier(defaultHostnameVerifier)
        HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory)
    }

    def "Should use the connection url parameter to connect to Rave"() {
        given: "a valid connection"
        ExternalEDCConnection externalEDCConnection = aValidEDCConnection()
        and: "a Rave instance expecting requests at the configured URL"
        raveReturningLabAnalyteRanges()
        when: "searching for active labs for a given study"
        raveLabAnalyteRangesGateway.findActiveLabs(externalEDCConnection)
        then: "Rave is invoked at the expected endpoint"
        verify(getRequestedFor(urlEqualTo(DEFAULT_URL_FOR_RETRIEVING_LABANALYTERANGES)))
    }

    def "Requires the connection to be non-null"() {
        when: "called with a null connection"
        raveLabAnalyteRangesGateway.findActiveLabs(null)
        then: "an exception is thrown"
        thrown(Exception.class)
    }

    @Unroll
    def "Should use the configured username '#username' and password '#password' for authenticating"(
            username, password, authorizationHeaderValue) {
        given: "a valid connection with username and password"
        def externalEDCConnection = ExternalEDCConnection.newBuilder()
                .withClinicalDataURI(labAnalyteRangesEndpoint())
                .withExternalSiteId(ExternalSiteId.of("whatever"))
                .withUsername(username)
                .withPassword(password)
                .withEnabled(true)
                .build()
        and: "a Rave instance expecting requests at the configured URL"
        raveReturningLabAnalyteRanges()
        when: "searching for active labs for a given study"
        raveLabAnalyteRangesGateway.findActiveLabs(externalEDCConnection)
        then: "Rave is invoked at the expected endpoint"
        verify(getRequestedFor(urlEqualTo(DEFAULT_URL_FOR_RETRIEVING_LABANALYTERANGES))
                .withHeader("Authorization", equalTo(authorizationHeaderValue)))

        where:
        username  | password     | authorizationHeaderValue
        "Aladdin" | "OpenSesame" | "Basic QWxhZGRpbjpPcGVuU2VzYW1l"
    }

    def "Should return an empty list if no lab analyte ranges have been defined"() {
        given: "a Rave instance returning an empty list of lab analyte ranges"
        raveReturningEmptyLabAnalyteRanges()

        when: "finding labs with active analyte ranges for the given study"
        def labList = raveLabAnalyteRangesGateway.findActiveLabs(aValidEDCConnection())

        then: "an empty list is returned"
        labList.empty
    }

    def "Should return all labs for the given siteRef that have at least one active lab analyte range"() {
        given: "a Rave instance returning an list with two active lab analyte ranges for two separate labs"
        raveReturningLabAnalyteRanges()

        when: "finding labs with active analyte ranges for the given study"
        def labList = raveLabAnalyteRangesGateway.findActiveLabs(aValidEDCConnection())

        then: "a list containing both labs is returned"
        labList.size() == 2
        labList.contains(LabName.of("my_local_lab"))
        labList.contains(LabName.of("my_local_lab_2"))
    }

    def "Should return an empty list for a disabled connection"() {
        given: "a Rave instance returning an list with two active lab analyte ranges for two separate labs"
        raveReturningLabAnalyteRanges()
        and: "A disabled edc connection"
        def connection = ExternalEDCConnection.newBuilder(aValidEDCConnection())
                .withEnabled(false)
                .build()

        when: "finding labs with active analyte ranges for the given study"
        def labList = raveLabAnalyteRangesGateway.findActiveLabs(connection)

        then: "an empty list is returned"
        labList.empty
    }

    def "Should throw an exception if Rave returns an error code"() {
        given: "a Rave instance returning an error (anything other than 200 OK)"
        raveReturningAnError()

        when: "finding labs with active analyte ranges for the given study"
        raveLabAnalyteRangesGateway.findActiveLabs(aValidEDCConnection())

        then: "an exception is thrown"
        thrown(Exception)
    }

    private void configureClientSSL() {
        defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()
        defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()
        acceptAllSSLCertsForLocalhost()
    }

    private void acceptAllSSLCertsForLocalhost() {
        HttpsURLConnection.setDefaultHostnameVerifier({ hostname, session ->
            hostname == "localhost" ? true : defaultHostnameVerifier.verify(hostname, session)
        })
        TrustManager[] trustAllCerts = [
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                }
        ]
        SSLContext sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, new SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
    }

    private ExternalEDCConnection aValidEDCConnection() {
        def externalEDCConnection = ExternalEDCConnection.newBuilder()
                .withClinicalDataURI(labAnalyteRangesEndpoint())
                .withExternalSiteId(ExternalSiteId.of("12345"))
                .withEnabled(true)
                .build()
        externalEDCConnection
    }

    private URI labAnalyteRangesEndpoint() {
        new URI("https://localhost:" + wireMockRule.httpsPort()
                + DEFAULT_URL_FOR_RETRIEVING_LABANALYTERANGES)
    }

    private static StubMapping raveReturningEmptyLabAnalyteRanges() {
        raveReturningBody("/rave/getlabs/EmptyLabAnalyteRanges.csv")
    }

    private static StubMapping raveReturningLabAnalyteRanges() {
        raveReturningBody("/rave/getlabs/LabAnalyteRanges.csv")
    }

    private static StubMapping raveReturningBody(String classpathLocation) {
        stubFor(any(anyUrl()).willReturn(aResponse()
                .withBody(fromFile(classpathLocation))))
    }

    private static StubMapping raveReturningAnError() {
        stubFor(any(anyUrl()).willReturn(aResponse().withStatus(500)))
    }

    private static String fromFile(final String locationOnClasspath) {
        return new Scanner(RaveLabAnalyteRangesGatewaySpec.class.getResourceAsStream(locationOnClasspath))
                .useDelimiter("\\Z").next()
    }
}
