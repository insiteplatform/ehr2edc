package com.custodix.insite.local.ehr2edc.rest.study

import com.custodix.insite.local.ehr2edc.command.ClearItemQueryMappings
import com.custodix.insite.local.ehr2edc.command.DeleteItemQueryMapping
import com.custodix.insite.local.ehr2edc.command.SaveItemQueryMapping
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderProjector
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQuery
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.rest.study.StudyItemQueryMappingController
import com.custodix.insite.local.ehr2edc.rest.util.MockRemoteAddr
import org.mockito.ArgumentCaptor
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.FileCopyUtils

import static org.mockito.Mockito.verify
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [StudyItemQueryMappingController])
class StudyItemQueryMappingControllerSpec extends ControllerSpec {
    private static final String REQUEST_BODY = readJsonForController("create-item-mapping")
    static final BASE_ENDPOINT = "/ehr2edc"

    @MockBean
    SaveItemQueryMapping saveItemQueryMapping
    @MockBean
    DeleteItemQueryMapping deleteItemQueryMapping
    @MockBean
    ClearItemQueryMappings clearItemQueryMappings

    def "A valid item mapping request succeeds"() {
        given: "A valid request"
        def studyId = "STUDY", itemId = "ITEM"
        def request = post(createItemMappingEndpoint(studyId))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(createRequestBody(itemId))

        when: "The endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response should indicate a successful operation"
        response.status == HttpStatus.CREATED.value()
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

    def "An item mapping request with missing path variables fails"() {
        given: "An invalid request"
        def study = ""
        def request = post(createItemMappingEndpoint(study))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{}")

        when: "The endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response should indicate an unknown endpoint"
        response.status == HttpStatus.NOT_FOUND.value()
    }

    def "An item mapping request without body fails"() {
        given: "A request without body"
        def study = "STUDY"
        def request = post(createItemMappingEndpoint(study))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("")

        when: "The endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response should indicate wrong data was sent"
        response.status == HttpStatus.BAD_REQUEST.value()
    }

    def "A valid delete item request succeeds"() {
        given: "A valid request"
        def studyId = "STUDY", itemId = "ITEM"
        def request = delete(createDeleteItemMappingEndpoint(studyId, itemId))

        when: "The endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response should indicate a successful operation"
        response.status == HttpStatus.NO_CONTENT.value()
        and: "The use case was called with the correct request"
        def captor = ArgumentCaptor.forClass(DeleteItemQueryMapping.Request)
        verify(deleteItemQueryMapping).delete(captor.capture())
        def validateRequest = captor.value
        validateRequest.studyId.id == studyId
        validateRequest.itemId.id == itemId
    }

    def "A clearing all mappings request succeeds"() {
        given: "A valid request"
        def studyId = "STUDY"
        def request = delete(createDeleteAllMappingsEndpoint(studyId))

        when: "The endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response should indicate a successful operation"
        response.status == HttpStatus.NO_CONTENT.value()
        and: "The use case was called with the correct request"
        def captor = ArgumentCaptor.forClass(ClearItemQueryMappings.Request)
        verify(clearItemQueryMappings).clear(captor.capture())
        def validateRequest = captor.value
        validateRequest.studyId.id == studyId
    }

    @WithAnonymousUser
    def "An unauthenticated user can create an item mapping from a localhost IPv4 address"() {
        given: "A valid request from a localhost IPv4 address"
        def study = "STUDY", itemId = "ITEM"
        def request = post(createItemMappingEndpoint(study))
                .with(MockRemoteAddr.localhostIPv4())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(createRequestBody(itemId))

        when: "The endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is granted"
        resultActions.andExpect(status().isCreated())
    }

    @WithAnonymousUser
    def "An unauthenticated user can create an item mapping from a localhost IPv6 address"() {
        given: "A valid request from a localhost IPv6 address"
        def study = "STUDY", itemId = "ITEM"
        def request = post(createItemMappingEndpoint(study))
                .with(MockRemoteAddr.localhostIPv6())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(createRequestBody(itemId))

        when: "The endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is granted"
        resultActions.andExpect(status().isCreated())
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot create an item mapping from a non-localhost address"() {
        given: "A valid request from a non-localhost address"
        def study = "STUDY", itemId = "ITEM"
        def request = post(createItemMappingEndpoint(study))
                .with(MockRemoteAddr.nonLocalhost())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(createRequestBody(itemId))

        when: "The endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    @WithAnonymousUser
    def "An unauthenticated user can delete an item mapping from a localhost IPv4 address"() {
        given: "A valid request from a localhost IPv4 address"
        def studyId = "STUDY", itemId = "ITEM"
        def request = delete(createDeleteItemMappingEndpoint(studyId, itemId))
                .with(MockRemoteAddr.localhostIPv4())

        when: "The endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is granted"
        resultActions.andExpect(status().isNoContent())
    }

    @WithAnonymousUser
    def "An unauthenticated user can delete an item mapping from a localhost IPv6 address"() {
        given: "A valid request from a localhost IPv6 address"
        def studyId = "STUDY", itemId = "ITEM"
        def request = delete(createDeleteItemMappingEndpoint(studyId, itemId))
                .with(MockRemoteAddr.localhostIPv6())

        when: "The endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is granted"
        resultActions.andExpect(status().isNoContent())
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot delete an item mapping from a non-localhost address"() {
        given: "A valid request from a non-localhost address"
        def studyId = "STUDY", itemId = "ITEM"
        def request = delete(createDeleteItemMappingEndpoint(studyId, itemId))
                .with(MockRemoteAddr.nonLocalhost())

        when: "The endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    @WithAnonymousUser
    def "An unauthenticated user can delete all mappings from a localhost IPv4 address"() {
        given: "A valid request from a localhost IPv4 address"
        def studyId = "STUDY"
        def request = delete(createDeleteAllMappingsEndpoint(studyId))
                .with(MockRemoteAddr.localhostIPv4())

        when: "The endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is granted"
        resultActions.andExpect(status().isNoContent())
    }

    @WithAnonymousUser
    def "An unauthenticated user can delete all mappings from a localhost IPv6 address"() {
        given: "A valid request from a localhost IPv6 address"
        def studyId = "STUDY"
        def request = delete(createDeleteAllMappingsEndpoint(studyId))
                .with(MockRemoteAddr.localhostIPv6())

        when: "The endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is granted"
        resultActions.andExpect(status().isNoContent())
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot delete all mappings from a non-localhost address"() {
        given: "A valid request from a non-localhost address"
        def studyId = "STUDY"
        def request = delete(createDeleteAllMappingsEndpoint(studyId))
                .with(MockRemoteAddr.nonLocalhost())

        when: "The endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    def createRequestBody(itemId) {
        return REQUEST_BODY.replace("ITEM_ID", itemId)
    }

    def createItemMappingEndpoint(study) {
        return BASE_ENDPOINT +
                "/studies/$study/item-query-mappings"
    }

    def createDeleteItemMappingEndpoint(study, itemId) {
        return BASE_ENDPOINT +
                "/studies/$study/item-query-mappings/$itemId"
    }

    def createDeleteAllMappingsEndpoint(study) {
        return BASE_ENDPOINT +
                "/studies/$study/item-query-mappings"
    }

    static def readJsonForController(String fileName) {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("samples/studyitemquerymappingcontroller/" + fileName + ".json").getFile()))
    }
}
