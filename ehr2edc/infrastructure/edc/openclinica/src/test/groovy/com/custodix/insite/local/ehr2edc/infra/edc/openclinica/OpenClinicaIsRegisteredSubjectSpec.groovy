package com.custodix.insite.local.ehr2edc.infra.edc.openclinica

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.StudyObjectMother
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import com.custodix.insite.local.ehr2edc.vocabulary.*
import org.springframework.http.HttpStatus
import org.springframework.web.util.UriTemplate
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReferenceObjectMother.aRandomEdcSubjectReference
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType.READ_SUBJECTS
import static com.github.tomakehurst.wiremock.client.WireMock.*

class OpenClinicaIsRegisteredSubjectSpec extends AbstractOpenClinicaSpecification {
    private static final UriTemplate SUBJECTS_URI = new UriTemplate("http://localhost:{port}/OpenClinica/pages/auth/api/clinicaldata/studies/{studyOid}/participants")

    @Unroll
    def "Is subject '#subject' registered returns '#expectedResult' when list of registered subjects is '#subjects'"(
            EDCSubjectReference subject, HttpStatus responseStatus, String responseBodyFile, List subjects, boolean expectedResult) {
        given: "a study"
        Study study = StudyObjectMother.aDefaultStudy()
        and: "the study has a configured EDC connection to read subjects from OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, READ_SUBJECTS)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getUri(study.studyId))
                .build()
        and: "The authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "The EDC returns the list of registered subjects '#subjects'"
        stubResponse(connection.clinicalDataURI, responseStatus, responseBodyFile)

        when: "I check if the subject '#subject' is registered"
        def result = openClinicaEDCStudyGateway.isRegisteredSubject(connection, subject)

        then: "The result is '#expectedResult'"
        result == expectedResult

        where:
        subject                       | responseStatus                   | responseBodyFile              || subjects                                                       | expectedResult
        EDCSubjectReference.of("002") | HttpStatus.OK                    | "getsubjects.json"            || [EDCSubjectReference.of("001"), EDCSubjectReference.of("002")] | true
        EDCSubjectReference.of("003") | HttpStatus.OK                    | "getsubjects.json"            || [EDCSubjectReference.of("001"), EDCSubjectReference.of("002")] | false
        EDCSubjectReference.of("002") | HttpStatus.OK                    | "getsubjects-empty.json"      || []                                                             | false
        EDCSubjectReference.of("002") | HttpStatus.OK                    | "getsubjects-invalid.json"    || []                                                             | false
        EDCSubjectReference.of("002") | HttpStatus.BAD_REQUEST           | "getsubjects-badrequest.json" || []                                                             | false
        EDCSubjectReference.of("002") | HttpStatus.UNAUTHORIZED          | null                          || []                                                             | false
        EDCSubjectReference.of("002") | HttpStatus.FORBIDDEN             | null                          || []                                                             | false
        EDCSubjectReference.of("002") | HttpStatus.NOT_FOUND             | null                          || []                                                             | false
        EDCSubjectReference.of("002") | HttpStatus.INTERNAL_SERVER_ERROR | null                          || []                                                             | false
    }

    def "When the EDC authentication endpoint responds with a SystemException, return false"() {
        given: "a study"
        Study study = StudyObjectMother.aDefaultStudy()
        and: "the study has a configured EDC connection to read subjects from OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, READ_SUBJECTS)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getUri(study.studyId))
                .build()
        and: "The authentication token provider responds with a SystemException"
        authenticationTokenProvider.get(connection) >> new SystemException("Could not authenticate")

        when: "I check if a subject is registered"
        def result = openClinicaEDCStudyGateway.isRegisteredSubject(connection, aRandomEdcSubjectReference())

        then: "The result is 'false'"
        !result
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
