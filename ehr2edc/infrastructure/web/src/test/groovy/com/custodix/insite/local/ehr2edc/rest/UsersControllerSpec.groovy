package com.custodix.insite.local.ehr2edc.rest

import com.custodix.insite.local.ehr2edc.query.GetAvailableInvestigators
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import org.mockito.ArgumentCaptor
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.FileCopyUtils

import java.util.stream.Collectors
import java.util.stream.LongStream

import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.verify
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = UsersController)
class UsersControllerSpec extends ControllerSpec {

    static final String BASE_ENDPOINT = "/ehr2edc/users?status=unassigned&studyId={studyId}"

    @MockBean
    GetAvailableInvestigators getAvailableInvestigatorsForStudy

    def "Retrieving potential investigators returns a list of potential investigators"() {
        given: "A study"
        def studyId = StudyId.of("STUDY-1")
        and: "that has potential investigators"
        def containsStudyId = { GetAvailableInvestigators.Request req -> Objects.equals(req.getStudyId(), studyId) }
        given(getAvailableInvestigatorsForStudy.get(argThat(containsStudyId))).willReturn(potentialInvestigators())

        when: "The potential investigators are returned for the study"
        def response = mockMvc.perform(get(BASE_ENDPOINT, studyId.id)).andReturn().response
        def captor = ArgumentCaptor.forClass(GetAvailableInvestigators.Request)
        verify(getAvailableInvestigatorsForStudy).get(captor.capture())
        then: "The response indicates that the operation succeeded"
        response.status == HttpStatus.OK.value()
        and: "The usecase was called with the correct request"
        captor.value.studyId.id == studyId.id
        and: "The potential investigators are returned"
        JSONAssert.assertEquals(readJsonForController("unassigned-response"),
                response.contentAsString,
                JSONCompareMode.STRICT)
    }

    def "Retrieving potential investigators with incorrect status in url"() {
        given: "A study"
        def studyId = StudyId.of("STUDY-1")
        and: "that has potential investigators"
        def containsStudyId = { GetAvailableInvestigators.Request req -> Objects.equals(req.getStudyId(), studyId) }
        given(getAvailableInvestigatorsForStudy.get(argThat(containsStudyId))).willReturn(potentialInvestigators())

        when: "I get the potential investigators"
        ResultActions resultActions = mockMvc.perform(get("/ehr2edc/users?status=something&studyId={studyId}", studyId.id))

        then: "the status is 'not found'"
        resultActions.andExpect(status().isNotFound())
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot get potential investigators"() {
        given: "A study"
        def studyId = StudyId.of("STUDY-1")
        and: "that has potential investigators"
        def containsStudyId = { GetAvailableInvestigators.Request req -> Objects.equals(req.getStudyId(), studyId) }
        given(getAvailableInvestigatorsForStudy.get(argThat(containsStudyId))).willReturn(potentialInvestigators())

        when: "I get the potential investigators"
        ResultActions resultActions = mockMvc.perform(get(BASE_ENDPOINT, studyId.id))

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    static def readJsonForController(String path) {
        def location = String.format("samples/userscontroller/%s.json", path)
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource(location).getFile()))
    }

    def potentialInvestigators() {
        GetAvailableInvestigators.Response.newBuilder().withPotentialInvestigators(
                LongStream.range(1, 11)
                        .mapToObj { i ->
                            GetAvailableInvestigators.PotentialInvestigator.newBuilder()
                                    .withName("User nr. " + i)
                                    .withUserId(UserIdentifier.of("User id. " + i))
                                    .build()
                        }
                        .collect(Collectors.toList())).build()
    }
}
