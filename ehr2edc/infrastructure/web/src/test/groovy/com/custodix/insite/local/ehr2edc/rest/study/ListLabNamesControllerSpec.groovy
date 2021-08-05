package com.custodix.insite.local.ehr2edc.rest.study

import com.custodix.insite.local.ehr2edc.query.ListLocalLabs
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.vocabulary.LabName
import org.mockito.ArgumentCaptor
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@ContextConfiguration(classes = ListLabNamesController)
class ListLabNamesControllerSpec extends ControllerSpec {
    @MockBean
    ListLocalLabs listLocalLabs

    @Unroll
    def "the study id must be provided"(String studyId) {
        when: "In invoke the controller with an empty studyId"
        invokeControllerForStudy(studyId)

        then: "an exception it thrown"
        thrown(Exception.class)

        where:
        studyId << [ null, "" ]
    }

    @Unroll
    def "invokes the ListLocalLabsQuery use case with the right argument"(String myStudyId) {
        when: "I invoke the controller"
        invokeControllerForStudy(myStudyId)

        then: "the use case was invoked"
        def requestCaptor = ArgumentCaptor.forClass(ListLocalLabs.Request)
        useCaseWasInvoked(requestCaptor)
        with(requestCaptor.value) {
            studyId.id == myStudyId
        }

        where:
        myStudyId << ["studyId"]
    }

    def "does not hide an unexpected exception"() {
        given: "that the use case throws an exception"
        when(listLocalLabs.list(any())).thenThrow(UnsupportedOperationException)

        when: "I invoke the controller"
        invokeControllerForStudy("studyId")

        then: "an exception is thrown"
        thrown(Exception)
    }

    def "returns the result returned by the use case"() {
        given: "that the use case returns a list of lab names"
        def response = ListLocalLabs.Response.newBuilder().withLocalLabs([LabName.of("labName")]).build()
        when(listLocalLabs.list(any())).thenReturn(response)

        when: "I invoke the controller"
        def controllerResponse = invokeControllerForStudy("studyId")

        then: "the response contains the expected submitted event json"
        controllerResponse.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(controllerResponse.contentAsString, '{"localLabs":[{"name":"labName"}]}', STRICT)
    }

    @WithAnonymousUser
    def "returns an unauthorized response code when the controller is invoked without authentication"() {
        when: "I invoke the controller"
        def controllerResponse = invokeControllerForStudy(("studyId"))

        then: "the response status code is 'Unauthorized'"
        controllerResponse.status == HttpStatus.UNAUTHORIZED.value()
    }

    private MockHttpServletResponse invokeControllerForStudy(String study) {
        return mockMvc.perform(get("/ehr2edc/studies/{studyId}/lab-names", study)).andReturn().response
    }

    private boolean useCaseWasInvoked(ArgumentCaptor<ListLocalLabs.Request> requestCaptor) {
        verify(listLocalLabs).list(requestCaptor.capture())
        return true
    }
}
