package com.custodix.insite.local.ehr2edc.infra.edc.rave

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.*
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.matching.EqualToXmlPattern
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

class RaveEDCStudyGatewaySpec extends AbstractSpecification {
    static final def EDC_REFERENCE = EDCSubjectReference.newBuilder().withId("subjectId").build()
    static final def EXTERNAL_SITE_ID = ExternalSiteId.of("123456")
    public static final int NEVER = 0
    private static final StudyId STUDY_ID_OVERRIDE = StudyId.of("overridden-study-id")

    @Rule
    WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort())

    @Autowired
    RaveEDCStudyGateway raveEDCStudyGateway

    def "Finding registered subject ids on an EDC study when EDC returns no Data"() {
        given:
        getWithResponse(null)

        when:
        def result = raveEDCStudyGateway.findRegisteredSubjectIds(edcConnectionData())

        then:
        result.size() == 0
        verify(getRequestedFor(urlEqualTo(edcConnectionData().clinicalDataURI.getPath())))
    }

    def "Finding registered subject ids on an EDC study when EDC returns invalid Data"() {
        given:
        getWithResponse(readSample("rave/getsubjects/GetSubjects-invalid.xml"))

        when:
        def result = raveEDCStudyGateway.findRegisteredSubjectIds(edcConnectionData())

        then:
        result.size() == 0
        verify(getRequestedFor(urlEqualTo(edcConnectionData().clinicalDataURI.getPath())))
    }

    def "Finding registered subject ids on an EDC study when EDC returns Data for other sites"() {
        given:
        getWithResponse(readSample("rave/getsubjects/GetSubjects-otherSites.xml"))

        when:
        def result = raveEDCStudyGateway.findRegisteredSubjectIds(edcConnectionData())

        then:
        result.size() == 0
        verify(getRequestedFor(urlEqualTo(edcConnectionData().clinicalDataURI.getPath())))
    }

    def "Finding registered subject ids on an EDC study when EDC returns Data for other studies"() {
        given:
        getWithResponse(readSample("rave/getsubjects/GetSubjects-otherStudy.xml"))

        when:
        def result = raveEDCStudyGateway.findRegisteredSubjectIds(edcConnectionData())

        then:
        result.size() == 0
        verify(getRequestedFor(urlEqualTo(edcConnectionData().clinicalDataURI.getPath())))
    }

    def "Finding registered subject ids on an EDC study when EDC returns null clinical data"() {
        given:
        getWithResponse(readSample("rave/getsubjects/GetSubjects-nullClinicalData.xml"))

        when:
        def result = raveEDCStudyGateway.findRegisteredSubjectIds(edcConnectionData())

        then:
        result.size() == 0
        verify(getRequestedFor(urlEqualTo(edcConnectionData().clinicalDataURI.getPath())))
    }

    def "Finding registered subject ids on a disabled EDC study"() {
        given:
        getWithResponse(readSample("rave/getsubjects/GetSubjects.xml"))

        when:
        def result = raveEDCStudyGateway.findRegisteredSubjectIds(disabledEDCConnection())

        then:
        result.size() == 0
        verify(NEVER, getRequestedFor(urlEqualTo(edcConnectionData().clinicalDataURI.getPath())))
    }

    def "Finding registered subject ids on an EDC study"() {
        given:
        getWithResponse(readSample("rave/getsubjects/GetSubjects.xml"))

        when:
        def result = raveEDCStudyGateway.findRegisteredSubjectIds(edcConnectionData())

        then:
        result.size() == 100
        verify(getRequestedFor(urlEqualTo(edcConnectionData().clinicalDataURI.getPath())))
    }

    def "Finding registered subject ids can override the studyId from the configured connection"() {
        given:
        getWithResponse(readSample("rave/getsubjects/GetSubjects-overrideStudyId.xml"))
        and: "The EDC connection is configured to override the study id"
        def connection = defaultExternalEDCConnectionBuilder()
                .withStudyIdOVerride(STUDY_ID_OVERRIDE)
                .build()

        when:
        def result = raveEDCStudyGateway.findRegisteredSubjectIds(connection)

        then:
        result.size() == 100
        verify(getRequestedFor(urlEqualTo(connection.clinicalDataURI.getPath())))
    }

    @Unroll
    def "Testing whether an EDC-reference exists"() {
        given: "A valid response when querying Rave"
        getWithResponse(readSample("rave/getsubjects/GetSubjects.xml"))

        when: "Testing whether an '#reference' is registered in Rave"
        def result = raveEDCStudyGateway.findRegisteredSubjectIds(edcConnectionData())

        then: "'#expected' is returned"
        result.size() == 100
        verify(getRequestedFor(urlEqualTo(edcConnectionData().clinicalDataURI.getPath())))

        where:
        reference      || expected
        null           || false
        "ref"          || false
        "123456000002" || true
    }

    def "Creating a subject on an EDC"() {
        given: "An EDCConnection"
        ExternalEDCConnection connection = ExternalEDCConnection.newBuilder(edcConnectionData())
                .build()
        and: "Rave will return ok"
        stubPostOk()
        def study = Study.restoreSnapshot(StudySnapshot.newBuilder().withStudyId(STUDY_ID).build())


        when: "Creating a subject"
        raveEDCStudyGateway.createSubject(study, EDC_REFERENCE, connection)

        then: "The EDC URL was called with correct ODM XML"
        checkOdmXml(connection.clinicalDataURI, "rave/createsubject/CreateSubject.xml")
    }

    def "Creating a subject on an EDC can override the studyId from the configured connection"() {
        given: "A study"
        def study = Study.restoreSnapshot(StudySnapshot.newBuilder().withStudyId(STUDY_ID).build())
        and: "The study EDC connection is configured to override the study id"
        def connection = defaultExternalEDCConnectionBuilder()
                .withStudyIdOVerride(STUDY_ID_OVERRIDE)
                .build()
        and: "Rave will return ok"
        stubPostOk()

        when: "Creating a subject"
        raveEDCStudyGateway.createSubject(study, EDC_REFERENCE, connection)

        then: "The EDC URL was called with correct ODM XML"
        checkOdmXml(connection.clinicalDataURI, "rave/createsubject/CreateSubject-overrideStudyId.xml")
    }

    def "Attempt creating a subject on an EDC that has already been created should be ignored"() {
        given: "An EDCConnection"
        ExternalEDCConnection connection = ExternalEDCConnection.newBuilder(edcConnectionData())
                .build()
        stubFor(post(urlPathMatching(edcConnectionData().clinicalDataURI.getPath()))
                .willReturn(aResponse()
                        .withBody(readSample("rave/createsubject/CreateSubject-subjectAlreadyExists.xml"))
                        .withStatus(409)))

        def study = Study.restoreSnapshot(StudySnapshot.newBuilder().withStudyId(STUDY_ID).build())


        when: "Creating an already existing subject"
        raveEDCStudyGateway.createSubject(study, EDC_REFERENCE, connection)

        then: "The EDC URL was called with correct ODM XML"
        checkOdmXml(connection.clinicalDataURI, "rave/createsubject/CreateSubject.xml")
    }


    def "Attempt creating a subject on an EDC with any other error should still fail"() {
        given: "An EDCConnection"
        ExternalEDCConnection connection = ExternalEDCConnection.newBuilder(edcConnectionData())
                .build()
        stubFor(post(urlPathMatching(edcConnectionData().clinicalDataURI.getPath()))
                .willReturn(aResponse()
                        .withBody(readSample("rave/createsubject/CreateSubject-anyOtherError.xml"))
                        .withStatus(409)))

        def study = Study.restoreSnapshot(StudySnapshot.newBuilder().withStudyId(STUDY_ID).build())

        when: "Creating an already existing subject"
        raveEDCStudyGateway.createSubject(study, EDC_REFERENCE, connection)

        then: "Expect a SystemException"
        def ex = thrown(SystemException)
        ex.message == "Error during rave communication"
    }

    def "Creating a subject on a disabled EDC"() {
        given: "A study"
        def study = Study.restoreSnapshot(StudySnapshot.newBuilder().withStudyId(STUDY_ID).build())

        when: "Creating a subject with a disabled connection"
        raveEDCStudyGateway.createSubject(study, EDC_REFERENCE, disabledEDCConnection())

        then: "The EDC URL was not called"
        verify(NEVER, postRequestedFor(urlEqualTo(disabledEDCConnection().clinicalDataURI.getPath())))
    }

    @Unroll
    def "Is subject '#subject' registered returns '#expectedResult' when list of registered subjects is 123456000001 to 123456000100"(
            EDCSubjectReference subject, boolean expectedResult) {
        given: "an EDC connection to read subjects from Rave"
        ExternalEDCConnection connection = edcConnectionData()
        and: "the list of registered subjects on the EDC is 123456000001 to 123456000100"
        getWithResponse(readSample("rave/getsubjects/GetSubjects.xml"))

        when: "I check if the subject '#subject' is registered"
        def result = raveEDCStudyGateway.isRegisteredSubject(connection, subject)

        then: "The result is '#expectedResult'"
        result == expectedResult

        where:
        subject                                || expectedResult
        EDCSubjectReference.of("123456000050") || true
        EDCSubjectReference.of("123456000101") || false
    }

    private def edcConnectionData() {
        defaultExternalEDCConnectionBuilder().build()
    }

    private def defaultExternalEDCConnectionBuilder() {
        ExternalEDCConnection.newBuilder()
                .withStudyId(STUDY_ID)
                .withEdcSystem(EDCSystem.RAVE)
                .withExternalSiteId(EXTERNAL_SITE_ID)
                .withClinicalDataURI(new URI("http://localhost:${wireMockRule.port()}/RaveWebServices/studies/${STUDY_ID.id}/Subjects"))
                .withUsername("user1")
                .withPassword("user1Pass")
                .withEnabled(true)
    }

    private def disabledEDCConnection() {
        ExternalEDCConnection.newBuilder(edcConnectionData())
                .withEnabled(false)
                .build()
    }

    void checkOdmXml(URI edcServiceURI, String xmlFile) {
        verify(postRequestedFor(urlEqualTo(edcServiceURI.getPath()))
                .withRequestBody(equalXmlUsingWiremockXmlRequestMatchingPlaceholders(xmlFile)))
    }

    private EqualToXmlPattern equalXmlUsingWiremockXmlRequestMatchingPlaceholders(String xmlFile) {
        equalToXml(readSample(xmlFile), true, "\\[\\[", "]]")
    }


    private def getWithResponse(String responseBody) {
        ResponseDefinitionBuilder response
        if (responseBody != null) {
            response = aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_XML.toString())
                    .withBody(responseBody)
        } else {
            response = aResponse()
                    .withStatus(200)
        }
        stubFor(get(urlPathMatching(edcConnectionData().clinicalDataURI.getPath())).willReturn(response))
    }

    private def stubPostOk() {
        stubFor(post(urlPathMatching(edcConnectionData().clinicalDataURI.getPath()))
                .willReturn(aResponse().withStatus(200)))
    }

}
