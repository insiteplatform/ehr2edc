package com.custodix.insite.local.ehr2edc.rest.study

import com.custodix.insite.local.ehr2edc.query.ListEventDefinitions
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.EventId
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.FileCopyUtils
import spock.lang.Unroll

import java.time.Instant
import java.time.LocalDate

import static java.util.Collections.emptyList
import static org.mockito.ArgumentMatchers.any
import static org.mockito.BDDMockito.given
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [EventDefinitionsController])
class EventDefinitionsControllerSpec extends ControllerSpec {

    static final String BASE_ENDPOINT = "/ehr2edc/studies/{studyId}/subjects/{subjectId}/events"

    @MockBean
    ListEventDefinitions listEventDefinitions

    @Unroll
    def "ListEventDefinitionsInStudy returns a list of known eventdefinitions for study"(
            int nrOfEvents, int nrOfFormDefinitions,
            long eventQueryCount, long formQueryCount, String expectedJsonPath) {
        given: "A study with eventdefinitionss"
        given(listEventDefinitions.list(any()))
                .willReturn(listEventDefinitionsInStudyResponseWithResults(nrOfEvents, nrOfFormDefinitions, eventQueryCount, formQueryCount))

        when: "The list eventdefinitionss for study endpoint is called"
        def request = get(BASE_ENDPOINT, "STUDY-1", "subject-2")
        def response = mockMvc.perform(request).andReturn().response

        then: "the response contains all known studies"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(readJsonForController(expectedJsonPath),
                response.contentAsString,
                JSONCompareMode.STRICT)

        where:
        nrOfEvents | nrOfFormDefinitions | eventQueryCount | formQueryCount || expectedJsonPath
        1          | 6                   | 30              | 5              || "listEventsForStudy-knownEvents-response"
        3          | 6                   | 30              | 5              || "listEventsForStudy-lotsOfKnownEvents-response"
        1          | 0                   | 17              | 5              || "listEventsForStudy-knownEvents-no-forms-response"
        1          | 7                   | 0               | 0              || "listEventsForStudy-knownEvents-no-queries-response"
    }

    def "ListEventDefinitionsInStudy can handle events which aren't populated yet"() {
        given: "A study with eventdefinitions which haven't been populated"
        given(listEventDefinitions.list(any())).willReturn(listEventDefinitionsInStudyResponseNotPopulated())

        when: "The list eventdefinitions for study endpoint is called"
        def request = get(BASE_ENDPOINT, "STUDY-1", "subject-2")
        def response = mockMvc.perform(request).andReturn().response

        then: "the response contains te events with 'null' dates for population and reference date"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(readJsonForController("listEventsForStudy-knownEvents-notPopulated"), response.contentAsString,
                JSONCompareMode.STRICT)
    }

    def "ListEventDefinitionsInStudy returns an empty list if there are no eventdefinitions"() {
        given: "A study with eventdefinitions"
        given(listEventDefinitions.list(any())).willReturn(listEventDefinitionsInStudyResponseWithoutResults())

        when: "The list eventdefinitions for study endpoint is called"
        def request = get(BASE_ENDPOINT, "STUDY-1", "subject-2")
        def response = mockMvc.perform(request).andReturn().response

        then: "the response contains no studies"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(readJsonForController("listEventsForStudy-noEvents-response"), response.contentAsString,
                JSONCompareMode.STRICT)
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot get the list of eventdefinitions in a study"() {
        when: "The list eventdefinitions for study endpoint is called"
        def request = get(BASE_ENDPOINT, "STUDY-1", "subject-2")
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    static def listEventDefinitionsInStudyResponseWithResults(int nrOfEvents, int nrOfFormDefinitions, Long eventQueryCount, long formQueryCount) {
        listEventDefinitionsInStudyResponseWithEvents(buildEvents(nrOfEvents, nrOfFormDefinitions, eventQueryCount, formQueryCount))
    }

    static def listEventDefinitionsInStudyResponseNotPopulated() {
        listEventDefinitionsInStudyResponseWithEvents([ListEventDefinitions.EventDefinition.newBuilder()
                                                               .withEventDefinitionId(EventDefinitionId.of("event-1"))
                                                               .withName("Event nr. 1")
                                                               .withFormDefinitions(buildFormDefinitions(6, 5))
                                                               .withQueryCount(30)
                                                               .build()])
    }

    private static listEventDefinitionsInStudyResponseWithEvents(List<ListEventDefinitions.EventDefinition> events) {
        ListEventDefinitions.Response.newBuilder()
                .withEventDefinitionsInStudy(events)
                .build()
    }

    private static List<ListEventDefinitions.EventDefinition> buildEvents(int nrOfEvents, int nrOfFormDefinitions, Long eventQueryCount, long formQueryCount) {
        (1..nrOfEvents).collect { buildEvent(it, nrOfFormDefinitions, eventQueryCount, formQueryCount) }
    }

    private static ListEventDefinitions.EventDefinition buildEvent(int eventIndex, int nrOfFormDefinitions, long eventQueryCount, long formQueryCount) {
        ListEventDefinitions.EventDefinition.newBuilder()
                .withEventDefinitionId(EventDefinitionId.of("event-" + eventIndex))
                .withEventId(EventId.of("eventId-" + eventIndex))
                .withName("Event nr. " + eventIndex)
                .withFormDefinitions(buildFormDefinitions(nrOfFormDefinitions, formQueryCount))
                .withQueryCount(eventQueryCount)
                .withLastReferenceDate(LocalDate.of(2018, 12, 10))
                .withLastPopulationTime(Instant.parse("2014-05-27T10:12:12Z"))
                .withHistoryAvailable(true)
                .build()
    }

    private static List<ListEventDefinitions.FormDefinition> buildFormDefinitions(int nrOfFormDefinitions, long formQueryCount) {
        if (nrOfFormDefinitions == 0) {
            return emptyList()
        }
        (0..nrOfFormDefinitions - 1).collect { buildFormDefinition(it, formQueryCount) }
    }

    private static ListEventDefinitions.FormDefinition buildFormDefinition(int index, long formQueryCount) {
        ListEventDefinitions.FormDefinition.newBuilder()
                .withFormDefinitionId(FormDefinitionId.of("formDefinitions " + index))
                .withName("form name " + index)
                .withQueryCount(formQueryCount)
                .build()
    }

    static def listEventDefinitionsInStudyResponseWithoutResults() {
        return ListEventDefinitions.Response.newBuilder()
                .withEventDefinitionsInStudy(emptyList())
                .build()
    }

    static def readJsonForController(String path) {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("samples/studyeventdefinitionscontroller/"+path+".json").getFile()))
    }
}