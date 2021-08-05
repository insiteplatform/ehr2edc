package com.custodix.insite.local.ehr2edc.rest.study


import com.custodix.insite.local.ehr2edc.command.PopulateEvent
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatcher
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.FileCopyUtils

import java.time.LocalDate

import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.verify
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = PopulateEventController)
class PopulateEventControllerSpec extends ControllerSpec {
    private static final PopulateEvent.Response validResponse = PopulateEvent.Response.newBuilder()
            .withPopulatedDataPoints(1)
            .build()

    @MockBean
    PopulateEvent populateEvent

    def "extractDataPoints with valid parameters"() {
        given: "A data point will be populated"
        given(populateEvent.populate(argThat(new CreateEventRequestMatcher(expectedRequest())))).willReturn(validResponse)

        when: "I trigger the population"
        def requestBody = '{ "eventDefinitionId": "EV_ID", "referenceDate": "2013-04-20" }'
        def request = post("/ehr2edc/studies/ST_ID/subjects/SU_ID/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody)
        def response = mockMvc.perform(request).andReturn().response
        def captor = ArgumentCaptor.forClass(PopulateEvent.Request)
        verify(populateEvent).populate(captor.capture())

        then: "The response indicates that the population succeeded"
        response.status == HttpStatus.CREATED.value()
        and: "The response contains the amount of populated datapoints"

        JSONAssert.assertEquals(response.contentAsString, readJsonForController("populateEvent-response"), JSONCompareMode.STRICT)
        and: "The request matches the expected parameters"
        def value = captor.value
        value.studyId.id == "ST_ID"
        value.subjectId.id == "SU_ID"
        value.eventDefinitionId.id == "EV_ID"
        value.referenceDate == LocalDate.of(2013, 4, 20)
    }

    def "extractDataPoints fails for invalid referencedate format"() {
        when: "I trigger the population with in invalid referencedate"
        def requestBody = '{ "eventDefinitionId": "EV_ID", "referenceDate": "20/04/2013" }'
        def request = post("/ehr2edc/studies/ST_ID/subjects/SU_ID/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody)
        def response = mockMvc.perform(request).andReturn().response

        then: "The response indicates that the population succeeded"
        response.status == HttpStatus.BAD_REQUEST.value()
    }

    def "extractDataPoints fails without referencedate"() {
        when: "I trigger the population without a referencedate"
        def requestBody = '{ "eventDefinitionId": "EV_ID" }'
        def request = post("/ehr2edc/studies/ST_ID/subjects/SU_ID/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody)
        def response = mockMvc.perform(request).andReturn().response

        then: "The response indicates that the population failed"
        response.status == HttpStatus.BAD_REQUEST.value()
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot extract data points"() {
        when: "I extract data points"
        def requestBody = '{ "eventDefinitionId": "EV_ID", "referenceDate": "2013-04-20" }'
        def request = post("/ehr2edc/studies/ST_ID/subjects/SU_ID/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody)
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    private static PopulateEvent.Request expectedRequest() {
        return PopulateEvent.Request.newBuilder()
                .withStudyId(StudyId.of("ST_ID"))
                .withSubjectId(SubjectId.of("SU_ID"))
                .withEventDefinitionId(EventDefinitionId.of("EV_ID"))
                .withReferenceDate(LocalDate.of(2013, 4, 20))
                .build()
    }

    private class CreateEventRequestMatcher implements ArgumentMatcher<PopulateEvent.Request> {

        private PopulateEvent.Request request

        CreateEventRequestMatcher(PopulateEvent.Request request) {
            this.request = request
        }

        @Override
        boolean matches(PopulateEvent.Request argument) {
            return argument.studyId == request.studyId &&
                    argument.subjectId == request.subjectId &&
                    argument.eventDefinitionId == request.eventDefinitionId &&
                    argument.referenceDate == request.referenceDate
        }
    }

    static def readJsonForController(String path) {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("samples/studyeventscontroller/"+path+".json").getFile()))
    }
}
