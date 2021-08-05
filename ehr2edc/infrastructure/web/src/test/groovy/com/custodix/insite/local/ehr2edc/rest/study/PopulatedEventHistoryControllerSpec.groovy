package com.custodix.insite.local.ehr2edc.rest.study

import com.custodix.insite.local.ehr2edc.query.GetPopulatedEventHistory
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.EventId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.mockito.ArgumentMatcher
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.FileCopyUtils

import java.time.Instant
import java.time.LocalDate
import java.util.stream.IntStream

import static java.util.stream.Collectors.toList
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.BDDMockito.given
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@ContextConfiguration(classes = PopulatedEventHistoryController)
class PopulatedEventHistoryControllerSpec extends ControllerSpec {
    private static final StudyId STUDY_ID = StudyId.of("study-1")
    private static final SubjectId SUBJECT_ID = SubjectId.of("subject-1")
    private static final EventDefinitionId EVENT_ID = EventDefinitionId.of("event-1")

    private static final String BASE_ENDPOINT =
            "/ehr2edc/studies/{studyId}/subjects/{subjectId}/events/populated/history?eventDefinitionId={eventDefinitionId}"

    @MockBean
    private GetPopulatedEventHistory getPopulatedEventHistory

    def "Calling the populatedEventHistory call with correct parameters returns the expected history json"(Integer amountOfHistoryItems, String expectedJsonFileName) {
        given:
        "The population was executed once for subject $SUBJECT_ID on event $EVENT_ID"
        given(getPopulatedEventHistory.get(argThat(requestHasMatchingSubjectAndEvent()))).willReturn(buildResponse(amountOfHistoryItems))

        when:
        "I call the populatedEventHistory call for $SUBJECT_ID and $EVENT_ID"
        def request = get(BASE_ENDPOINT, STUDY_ID.id, SUBJECT_ID.id, EVENT_ID.id)
        def response = mockMvc.perform(request).andReturn().response
        then: "I expect the call to succeed"
        response.status == HttpStatus.OK.value()
        and: "The json matches the expected value"
        JSONAssert.assertEquals(readJsonForController(expectedJsonFileName), response.contentAsString, JSONCompareMode.STRICT)

        where:
        amountOfHistoryItems || expectedJsonFileName
        0                    || "no-history-items-response"
        1                    || "single-history-item-response"
        10                   || "multiple-history-items-response"
    }

    private static ArgumentMatcher<GetPopulatedEventHistory.Request> requestHasMatchingSubjectAndEvent() {
        { arg -> (arg.subjectId == SUBJECT_ID && arg.eventId == EVENT_ID) }
    }

    GetPopulatedEventHistory.Response buildResponse(Integer amountOfHistoryItems) {
        def historyItems = IntStream.range(1, amountOfHistoryItems + 1).mapToObj({ index -> createHistoryItem(index) }).collect(toList())
        GetPopulatedEventHistory.Response.newBuilder()
                .withHistoryItems(historyItems)
                .build()
    }

    def createHistoryItem(int index) {
        return GetPopulatedEventHistory.PopulatedEventHistoryItem.newBuilder()
                .withEventId(EventId.of("event-" + index))
                .withPopulationTime(Instant.parse("2019-06-23T16:34:00Z"))
                .withReferenceDate(LocalDate.parse("2015-11-05"))
                .withPopulator(index % 2 == 0 ? "Test user" : null)
                .build()
    }

    static def readJsonForController(String path) {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("samples/populatedeventhistorycontroller/" + path + ".json").getFile()))
    }
}