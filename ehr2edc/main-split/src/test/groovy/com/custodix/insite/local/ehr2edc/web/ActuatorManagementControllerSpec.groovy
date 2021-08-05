package com.custodix.insite.local.ehr2edc.web

import com.custodix.insite.local.ehr2edc.AbstractIntegrationSpecification
import com.custodix.insite.local.ehr2edc.command.*
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderProjector
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQuery
import com.custodix.insite.local.ehr2edc.vocabulary.EHRSystem
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyODM
import org.mockito.ArgumentCaptor
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.http.*
import org.springframework.util.FileCopyUtils
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.UriTemplate

import static com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem.RAVE
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.verify
import static org.springframework.http.MediaType.APPLICATION_JSON

class ActuatorManagementControllerSpec extends AbstractIntegrationSpecification {
    private static final TestRestTemplate REST_TEMPLATE = new TestRestTemplate()
    private static final String BASE_PATH = "http://localhost:{port}/actuator/management"
    private static final UriTemplate DATEWAREHOUSE_URI = new UriTemplate("${BASE_PATH}/datawarehouse")
    private static final UriTemplate CREATE_STUDY_URI = new UriTemplate("${BASE_PATH}/studies")
    private static final UriTemplate UPDATE_STUDY_URI = new UriTemplate("${BASE_PATH}/studies/{studyId}/metadata")
    private static final UriTemplate ASSIGN_EDC_CONNECTION_URI = new UriTemplate("${BASE_PATH}/edc/connection")
    private static final UriTemplate ASSIGN_EHR_CONNECTION_URI = new UriTemplate("${BASE_PATH}/ehr/connection")
    private static final UriTemplate CREATE_ITEM_MAPPING_URI = new UriTemplate("${BASE_PATH}/studies/{studyId}/item-query-mappings")
    private static final UriTemplate DELETE_ITEM_MAPPING_URI = new UriTemplate("${BASE_PATH}/studies/{studyId}/item-query-mappings/{itemId}")
    private static final UriTemplate DELETE_ALL_ITEM_MAPPING_URI = new UriTemplate("${BASE_PATH}/studies/{studyId}/item-query-mappings")
    private static final UriTemplate DELETE_STUDY_URI = new UriTemplate("${BASE_PATH}/studies/{studyId}")

    @MockBean
    private DatawarehouseUpdated datawarehouseUpdated
    @MockBean
    CreateStudy createStudy
    @MockBean
    UpdateStudyMetadata updateStudyMetadata
    @MockBean
    LinkConnection linkConnection
    @MockBean
    LinkEHRConnection linkEHRConnection
    @MockBean
    SaveItemQueryMapping saveItemQueryMapping
    @MockBean
    DeleteItemQueryMapping deleteItemQueryMapping
    @MockBean
    ClearItemQueryMappings clearItemQueryMappings
    @MockBean
    DeleteStudy deleteStudy

    def "Updating the datawarehouse"() {
        when: "The endpoint is called"
        ResponseEntity<String> response = REST_TEMPLATE.exchange(DATEWAREHOUSE_URI.expand(managementPort), HttpMethod.POST, new HttpEntity<>(), String.class)

        then: "The response indicates a successful operation"
        response.statusCode == HttpStatus.ACCEPTED
        and: "The use case was called"
        verify(datawarehouseUpdated).update()
    }

    def "Creating a study returns the new study's ID"() {
        given: "The use case returns the study id"
        def odm = readSampleStudy()
        given(createStudy.create(argThat({ r -> r.studyODM == StudyODM.of(odm) }))).willReturn(createStudyResponse())

        when: "The endpoint is called"
        HttpEntity<LinkedMultiValueMap<String, Object>> entity = createFileRequest()
        ResponseEntity<String> response = REST_TEMPLATE.exchange(CREATE_STUDY_URI.expand(managementPort), HttpMethod.POST, entity, String.class)

        then: "The response indicates a successful operation"
        response.statusCode == HttpStatus.CREATED
        and: "The response contains the studyId"
        response.headers.getFirst("location") == "/ehr2edc/studies/EHR2EDC"
    }

    def "Updating the study metadata"() {
        given: "A valid file upload request"
        def odm = readSampleStudy()

        when: "The endpoint is called"
        HttpEntity<LinkedMultiValueMap<String, Object>> entity = createFileRequest()
        ResponseEntity<String> response = REST_TEMPLATE.exchange(UPDATE_STUDY_URI.expand(managementPort, "EHR2EDC"), HttpMethod.PUT, entity, String.class)

        then: "The response indicates a successful operation"
        response.statusCode == HttpStatus.OK
        and: "The use case was called"
        ArgumentCaptor argumentCaptor = ArgumentCaptor.forClass(UpdateStudyMetadata.Request)
        verify(updateStudyMetadata).update(argumentCaptor.capture())
        and: "Correct arguments were used"
        def value = argumentCaptor.getValue()
        value.studyODM == StudyODM.of(odm)
    }

    def "Assigning an EDC connection to a study"() {
        given: "A known study to link a connection to"
        String content = readJsonForController("linkConnection-request")

        when: "The endpoint is called"
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON)
        HttpEntity entity = new HttpEntity<>(content, httpHeaders);
        ResponseEntity<String> response = REST_TEMPLATE.exchange(ASSIGN_EDC_CONNECTION_URI.expand(managementPort), HttpMethod.POST, entity, String.class)

        then: "The response indicates a successful operation"
        response.statusCode == HttpStatus.OK
        and: "The connection is successfully passed to the usecase"
        def captor = ArgumentCaptor.forClass(LinkConnection.Request)
        verify(linkConnection).link(captor.capture())
        with(captor.value) {
            studyId.id == "SID_001"
            type == StudyConnectionType.READ_SUBJECTS
            edcSystem == RAVE
            externalSiteId.id == "123456"
            clinicalDataURI == new URI("http:/localhost:8081/RaveWebServices/studies/raveStudyId/Subjects")
            username == "user1"
            password == "user1Pass"
            !enabled
        }
    }

    def "Assigning an EHR connection to a study"() {
        given: "A known study to link a connection to"
        String content = readJsonForController("linkEHRConnection-request")

        when: "The endpoint is called"
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON)
        HttpEntity entity = new HttpEntity<>(content, httpHeaders);
        ResponseEntity<String> response = REST_TEMPLATE.exchange(ASSIGN_EHR_CONNECTION_URI.expand(managementPort), HttpMethod.POST, entity, String.class)

        then: "The response indicates a successful operation"
        response.statusCode == HttpStatus.OK
        and: "The connection is successfully passed to the usecase"
        def captor = ArgumentCaptor.forClass(LinkEHRConnection.Request)
        verify(linkEHRConnection).link(captor.capture())
        with(captor.value) {
            studyId.id == "SID_001"
            system == EHRSystem.FHIR
            uri == new URI("http:/localhost:8080/fhir")
        }
    }

    def "Creating a study item mapping"() {
        given: "A valid request"
        def studyId = "STUDY", itemId = "ITEM"

        when: "The endpoint is called"
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON)
        HttpEntity entity = new HttpEntity<>(createRequestBody(itemId), httpHeaders);
        ResponseEntity<String> response = REST_TEMPLATE.exchange(CREATE_ITEM_MAPPING_URI.expand(managementPort, studyId), HttpMethod.POST, entity, String.class)

        then: "The response indicates a successful operation"
        response.statusCode == HttpStatus.CREATED
        and: "The use case was called with the correct request"
        def captor = ArgumentCaptor.forClass(SaveItemQueryMapping.Request)
        verify(saveItemQueryMapping).save(captor.capture())
        def validateRequest = captor.value
        validateRequest.itemId.id == itemId
        validateRequest.studyId.id == studyId
        validateRequest.query instanceof DemographicQuery
        def projectors = validateRequest.projectors
        projectors.size() == 1
        projectors.get(0) instanceof GenderProjector
    }

    def "Deleting a study item mapping"() {
        given: "A valid request"
        def studyId = "STUDY", itemId = "ITEM"

        when: "The endpoint is called"
        ResponseEntity<String> response = REST_TEMPLATE.exchange(DELETE_ITEM_MAPPING_URI.expand(managementPort, studyId, itemId), HttpMethod.DELETE, new HttpEntity<>(), String.class)

        then: "The response indicates a successful operation"
        response.statusCode == HttpStatus.NO_CONTENT
        and: "The use case was called with the correct request"
        def captor = ArgumentCaptor.forClass(DeleteItemQueryMapping.Request)
        verify(deleteItemQueryMapping).delete(captor.capture())
        def validateRequest = captor.value
        validateRequest.studyId.id == studyId
        validateRequest.itemId.id == itemId
    }

    def "Deleting all study item mappings"() {
        given: "A valid request"
        def studyId = "STUDY"

        when: "The endpoint is called"
        ResponseEntity<String> response = REST_TEMPLATE.exchange(DELETE_ALL_ITEM_MAPPING_URI.expand(managementPort, studyId), HttpMethod.DELETE, new HttpEntity<>(), String.class)

        then: "The response indicates a successful operation"
        response.statusCode == HttpStatus.NO_CONTENT
        and: "The use case was called with the correct request"
        def captor = ArgumentCaptor.forClass(ClearItemQueryMappings.Request)
        verify(clearItemQueryMappings).clear(captor.capture())
        def validateRequest = captor.value
        validateRequest.studyId.id == studyId
    }

    def "Deleting a study"() {
        given: "A valid request"
        def studyId = "STUDY"

        when: "The endpoint is called"
        ResponseEntity<String> response = REST_TEMPLATE.exchange(DELETE_STUDY_URI.expand(managementPort, studyId), HttpMethod.DELETE, new HttpEntity<>(), String.class)

        then: "The response indicates a successful operation"
        response.statusCode == HttpStatus.NO_CONTENT
        and: "The use case was called with the correct request"
        def captor = ArgumentCaptor.forClass(DeleteStudy.Request)
        verify(deleteStudy).delete(captor.capture())
        def validateRequest = captor.value
        validateRequest.studyId.id == studyId
    }

    static def createStudyResponse() {
        return CreateStudy.Response.newBuilder()
                .withStudyId(StudyId.of("EHR2EDC"))
                .build()
    }

    def createRequestBody(itemId) {
        return readJsonForController("create-item-mapping").replace("ITEM_ID", itemId)
    }

    static String readSampleStudy() {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("samples/min-sample-study.xml").getFile()))
    }

    static def readJsonForController(String fileName) {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("samples/actuatormanagementcontroller/" + fileName + ".json").getFile()))
    }

    private HttpEntity<LinkedMultiValueMap<String, Object>> createFileRequest() {
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>()
        parameters.add("file", new ClassPathResource("samples/min-sample-study.xml"))
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.MULTIPART_FORM_DATA)
        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<LinkedMultiValueMap<String, Object>>(parameters, headers)
        entity
    }
}
