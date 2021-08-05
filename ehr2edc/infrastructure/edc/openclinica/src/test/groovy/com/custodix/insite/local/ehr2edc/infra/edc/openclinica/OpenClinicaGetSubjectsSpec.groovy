package com.custodix.insite.local.ehr2edc.infra.edc.openclinica

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.StudyObjectMother
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import com.custodix.insite.local.ehr2edc.vocabulary.*
import org.springframework.http.HttpStatus
import org.springframework.web.util.UriTemplate
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType.READ_SUBJECTS
import static com.github.tomakehurst.wiremock.client.WireMock.*

class OpenClinicaGetSubjectsSpec extends AbstractOpenClinicaSpecification {
    private static final UriTemplate SUBJECTS_URI = new UriTemplate("http://localhost:{port}/OpenClinica/pages/auth/api/clinicaldata/studies/{studyOid}/participants")

    @Unroll
    def "When the EDC subjects endpoint responds with response status '#responseStatus' and body '#responseBodyFile', return '#expectedSubjects'"(HttpStatus responseStatus, String responseBodyFile, List expectedSubjects) {
        given: "a study"
        Study study = StudyObjectMother.aDefaultStudy()
        and: "the study has a configured EDC connection to read subjects from OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, READ_SUBJECTS)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getUri(study.studyId))
                .build()
        and: "The authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "The EDC returns a response with status #responseStatus and body #responseBodyFile"
        stubResponse(connection.clinicalDataURI, responseStatus, responseBodyFile)

        when: "I get the subjects registered in the study"
        List<EDCSubjectReference> subjects = openClinicaEDCStudyGateway.findRegisteredSubjectIds(connection)

        then: "'#expectedSubjects' is returned"
        subjects == expectedSubjects

        where:
        responseStatus                   | responseBodyFile              || expectedSubjects
        HttpStatus.OK                    | "getsubjects.json"            || [EDCSubjectReference.of("001"), EDCSubjectReference.of("002")]
        HttpStatus.OK                    | "getsubjects-empty.json"      || []
        HttpStatus.OK                    | "getsubjects-invalid.json"    || []
        HttpStatus.BAD_REQUEST           | "getsubjects-badrequest.json" || []
        HttpStatus.UNAUTHORIZED          | null                          || []
        HttpStatus.FORBIDDEN             | null                          || []
        HttpStatus.NOT_FOUND             | null                          || []
        HttpStatus.INTERNAL_SERVER_ERROR | null                          || []
    }

    def "When the EDC authentication endpoint responds with a SystemException, return '[]'"() {
        given: "a study"
        Study study = StudyObjectMother.aDefaultStudy()
        and: "the study has a configured EDC connection to read subjects from OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, READ_SUBJECTS)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getUri(study.studyId))
                .build()
        and: "The authentication token provider responds with a SystemException"
        authenticationTokenProvider.get(connection) >> new SystemException("Could not authenticate")

        when: "I get the subjects registered in the study"
        List<EDCSubjectReference> subjects = openClinicaEDCStudyGateway.findRegisteredSubjectIds(connection)

        then: "an empty list is returned"
        subjects == []
    }

    private stubResponse(URI expectedUri, HttpStatus httpStatus, String responseFile) {
        stubFor(get(urlEqualTo(expectedUri.getPath()))
                .withHeader("Authorization", equalTo("Bearer " + AUTHENTICATION_TOKEN.value))
                .willReturn(createResponse(httpStatus, responseFile)))
    }

    private createResponse(HttpStatus httpStatus, String responseFile) {
        def response = aResponse()
                .withStatus(httpStatus.value())
        responseFile && response.withBody(readFile("openclinica/getsubjects/${responseFile}"))
        return response
    }

    private URI getUri(StudyId studyId) {
        return SUBJECTS_URI.expand(wireMockRule.port(), studyId.id)
    }
}
