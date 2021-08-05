package com.custodix.insite.local.ehr2edc.infra.edc.openclinica

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.StudyObjectMother
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.vocabulary.*
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import org.springframework.http.HttpStatus
import org.springframework.web.util.UriTemplate
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType.WRITE_SUBJECT
import static com.github.tomakehurst.wiremock.client.WireMock.*

class OpenClinicaCreateSubjectSpec extends AbstractOpenClinicaSpecification {
    private static final UriTemplate URI_TEMPLATE = new UriTemplate("http://localhost:{port}/OpenClinica/pages/auth/api/clinicaldata/studies/{studyOID}/sites/{siteOID}/participants")

    def "Create subject"() {
        given: "a study"
        Study study = StudyObjectMother.aDefaultStudy()
        and: "an available site on the EDC"
        String siteOid = "S_SITE_1"
        and: "the study has a configured EDC connection to create a subject within the available site on OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, WRITE_SUBJECT)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getUri(study.studyId, siteOid))
                .build()
        and: "the authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "the EDC returns a successful response when called with a valid request"
        stubResponse(connection.clinicalDataURI, HttpStatus.OK, "createsubject-response.json")

        when: "I create the subject '001'"
        openClinicaEDCStudyGateway.createSubject(connection, study, EDCSubjectReference.of("001"))

        then: "the EDC is called with a valid request"
        verify(1, postRequestedFor(urlEqualTo(connection.clinicalDataURI.getPath())))
    }

    @Unroll
    def "When the EDC subject creation endpoint responds with status '400 BAD_REQUEST' and error '#errorCode', return the error message to the user"(String errorCode, String expectedErrorMessage) {
        given: "a study"
        Study study = StudyObjectMother.aDefaultStudy()
        and: "an available site on the EDC"
        String siteOid = "S_SITE_1"
        and: "the study has a configured EDC connection to create a subject within the available site on OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, WRITE_SUBJECT)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getUri(study.studyId, siteOid))
                .build()
        and: "the authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "the EDC returns a 400 bad_request response"
        stubBadRequest(connection.clinicalDataURI, errorCode)

        when: "I create the subject '001'"
        openClinicaEDCStudyGateway.createSubject(connection, study, EDCSubjectReference.of("001"))

        then: "the error message is returned to the user"
        def exception = thrown UserException
        exception.message == expectedErrorMessage

        where:
        errorCode                || expectedErrorMessage
        "errorCode.siteNotExist" || "No site with matching OID found on the EDC"
        "errorCode.unknownCode"  || "errorCode.unknownCode"
    }

    @Unroll
    def "When the EDC subject creation endpoint responds with status '#responseStatus', throw a SystemException"(HttpStatus responseStatus) {
        given: "a study"
        Study study = StudyObjectMother.aDefaultStudy()
        and: "an available site on the EDC"
        String siteOid = "S_SITE_1"
        and: "the study has a configured EDC connection to create a subject within the available site on OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, WRITE_SUBJECT)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getUri(study.studyId, siteOid))
                .build()
        and: "the authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "the EDC returns a response with status '#responseStatus'"
        stubResponse(connection.clinicalDataURI, responseStatus, null)

        when: "I create the subject '001'"
        openClinicaEDCStudyGateway.createSubject(connection, study, EDCSubjectReference.of("001"))

        then: "a SystemException is thrown"
        thrown SystemException

        where:
        responseStatus << [HttpStatus.UNAUTHORIZED,
                           HttpStatus.FORBIDDEN,
                           HttpStatus.NOT_FOUND,
                           HttpStatus.INTERNAL_SERVER_ERROR]
    }

    def "When the EDC authentication endpoint responds with a SystemException, throw a SystemException"() {
        given: "a study"
        Study study = StudyObjectMother.aDefaultStudy()
        and: "an available site on the EDC"
        String siteOid = "S_SITE_1"
        and: "the study has a configured EDC connection to create a subject within the available site on OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, WRITE_SUBJECT)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getUri(study.studyId, siteOid))
                .build()
        and: "the authentication token provider responds with a SystemException"
        authenticationTokenProvider.get(connection) >> new SystemException("Could not authenticate")

        when: "I create the subject '001'"
        openClinicaEDCStudyGateway.createSubject(connection, study, EDCSubjectReference.of("001"))

        then: "a SystemException is thrown"
        thrown SystemException
    }

    private stubResponse(URI expectedUri, HttpStatus httpStatus, String responseFile) {
        stubResponse(expectedUri, createResponse(httpStatus, responseFile))
    }

    private stubBadRequest(URI expectedUri, String errorCode) {
        stubResponse(expectedUri, createBadRequestResponse(errorCode))
    }

    private stubResponse(URI expectedUri, ResponseDefinitionBuilder response) {
        stubFor(post(urlEqualTo(expectedUri.getPath()))
                .withHeader("Authorization", equalTo("Bearer " + AUTHENTICATION_TOKEN.value))
                .withRequestBody(equalToJson(readFile("openclinica/createsubject/createsubject-request.json")))
                .willReturn(response))
    }

    private createResponse(HttpStatus httpStatus, String responseFile) {
        def response = aResponse()
                .withStatus(httpStatus.value())
        responseFile && response.withBody(readFile("openclinica/createsubject/${responseFile}"))
        return response
    }

    private createBadRequestResponse(String errorCode) {
        return aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withBody(readFile("openclinica/createsubject/createsubject-response-badrequest.json")
                        .replace("<ERROR_CODE>", errorCode))
    }

    URI getUri(StudyId studyId, String siteOid) {
        return URI_TEMPLATE.expand(wireMockRule.port(), studyId.id, siteOid)
    }
}
