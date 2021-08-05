package com.custodix.insite.local.ehr2edc.rest.study

import com.custodix.insite.local.ehr2edc.command.SubmitReviewedEvent
import com.custodix.insite.local.ehr2edc.query.GetSubmittedEvent
import com.custodix.insite.local.ehr2edc.query.GetSubmittedEventHistory
import com.custodix.insite.local.ehr2edc.query.GetSubmittedItemProvenance
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.vocabulary.*
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.FileCopyUtils

import java.time.Instant
import java.time.LocalDate

import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.verify
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = SubmittedEventsController)
class SubmittedEventsControllerSpec extends ControllerSpec {

    private static final String URL_POST_SUBMITTED_EVENT =
            "/ehr2edc/studies/{studyId}/subjects/{subjectId}/events/submitted"
    private static final String URL_GET_SUBMITTED_EVENT =
            "/ehr2edc/studies/{studyId}/subjects/{subjectId}/events/submitted/{eventId}"
    private static final String URL_GET_SUBMITTED_EVENT_HISTORY =
            "/ehr2edc/studies/{studyId}/subjects/{subjectId}/events/submitted/{eventId}/history"
    private static final String URL_GET_SUBMITTED_ITEM_PROVENANCE =
            "/ehr2edc/studies/{studyId}/subjects/{subjectId}/events/submitted/{eventId}/items/{itemId}/provenance"

    private static final StudyId STUDY_ID = StudyId.of("study-1")
    private static final SubjectId SUBJECT_ID = SubjectId.of("subject-1")
    private static final EventDefinitionId EVENT_ID = EventDefinitionId.of("event-1")
    private static final SubmittedItemId ITEM_ID = SubmittedItemId.of("item-1")

    @MockBean
    SubmitReviewedEvent submitReviewedEvent
    @MockBean
    GetSubmittedEvent getSubmittedEvent
    @MockBean
    GetSubmittedEventHistory getSubmittedEventHistory
    @MockBean
    GetSubmittedItemProvenance getSubmittedItemProvenance

    def "Submitting the reviewed events calls the SubmitReviewedEvent use case"(String samplePath, Integer expectedAmountOfForms) {
        given: 'An event will be submitted'
        final SubmittedEventId submittedEventId = SubmittedEventId.of("submitted-event-1")
        final SubmitReviewedEvent.Response validResponse = SubmitReviewedEvent.Response.newBuilder()
                .withSubmittedEventId(submittedEventId)
                .build()
        given(submitReviewedEvent.submit(ArgumentMatchers.any())).willReturn(validResponse)

        when: "I submit the reviewed events via ajax"
        def request = post(URL_POST_SUBMITTED_EVENT, STUDY_ID.id, SUBJECT_ID.id)
                .contentType(APPLICATION_JSON)
                .content(readJsonForController(samplePath))
        def response = mockMvc.perform(request).andReturn().response
        def requestCaptor = ArgumentCaptor.forClass(SubmitReviewedEvent.Request)
        verify(submitReviewedEvent).submit(requestCaptor.capture())

        then: "The ajax call has succeeded"
        response.status == HttpStatus.CREATED.value()
        response.getHeader('Location') == submittedEventId.getId()
        and: "The SubmitReviewedEvent use case has been called with the correct amount of forms, item groups and items"
        with(requestCaptor.value) {
            eventId.id == "populated-event-id-098"
            reviewedForms.size() == expectedAmountOfForms
            reviewedForms.forEach({ form -> verifyForm(form) })
        }

        where:
        samplePath                          || expectedAmountOfForms
        "submit-reviewed-event"             || 2
        "submit-reviewed-event-single-item" || 1
    }

    def "Get submitted event"() {
        given: 'A matching event with populator #populator'
        def submittedEventResponse = createGetSubmittedEventResponse(populator)
        given(getSubmittedEvent.get(getRequestArgumentMatcher(EVENT_ID.id))).willReturn(submittedEventResponse)

        when: "I get the submitted event"
        def request = get(URL_GET_SUBMITTED_EVENT, STUDY_ID.id, SUBJECT_ID.id, EVENT_ID.id)
        def response = mockMvc.perform(request).andReturn().response

        then: "the response contains the expected submitted event json"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString, readJsonForController(expectedContentFile), STRICT)

        where:
        populator   | expectedContentFile
        "Test user" | "getSubmittedEvent"
        null        | "getSubmittedEventNoPopulator"
    }

    def "Get empty reviewed form history returns a correct response"() {
        given: "The use case returning a listing of the reviewed event history"
        given(getSubmittedEventHistory.get(getHistoryRequestArgumentMatcher(SUBJECT_ID.id, EVENT_ID.id)))
                .willReturn(createGetSubmittedEventHistoryEmptyResponse())

        when: "I get the latest reviewed event"
        def request = get(URL_GET_SUBMITTED_EVENT_HISTORY, STUDY_ID.id, SUBJECT_ID.id, EVENT_ID.id)
        def response = mockMvc.perform(request).andReturn().response

        then: "the response contains no history items"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(readJsonForController("submittedFormHistory-empty"), response.contentAsString, STRICT)
    }

    def "Get reviewed form history returns a correct response"() {
        given: "The use case returning a listing of the reviewed event history"
        given(getSubmittedEventHistory.get(getHistoryRequestArgumentMatcher(SUBJECT_ID.id, EVENT_ID.id)))
                .willReturn(createGetSubmittedEventHistoryResponse())

        when: "I get the latest reviewed event"
        def request = get(URL_GET_SUBMITTED_EVENT_HISTORY, STUDY_ID.id, SUBJECT_ID.id, EVENT_ID.id)
        def response = mockMvc.perform(request).andReturn().response

        then: "the response contains 2 history items"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(readJsonForController("submittedFormHistory-present"), response.contentAsString, STRICT)
    }

    def "Get submitted item provenance returns a correct response"() {
        given: "The GetReviewedItemProvenance use case returns the provenance"
        def useCaseResponse = mockGetReviewedItemProvenanceResponse()
        given(getSubmittedItemProvenance.get(getItemProvenanceRequestArgumentMatcher(SUBJECT_ID.id, EVENT_ID.id, ITEM_ID.id)))
                .willReturn(useCaseResponse)

        when: "I call the endpoint"
        def request = get(URL_GET_SUBMITTED_ITEM_PROVENANCE, STUDY_ID.id, SUBJECT_ID.id, EVENT_ID.id, ITEM_ID.id)
        def response = mockMvc.perform(request).andReturn().response

        then: "The response contains the item provenance"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(readJsonForController("getSubmittedItemProvenance"), response.contentAsString, STRICT)
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot get reviewed item provenance"() {
        when: "I get a reviewed item provenance"
        def request = get(URL_GET_SUBMITTED_ITEM_PROVENANCE, STUDY_ID.id, SUBJECT_ID.id, EVENT_ID.id, ITEM_ID.id)
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    static def getRequestArgumentMatcher(reviewedEventId) {
        argThat({ GetSubmittedEvent.Request req ->
            req.getSubmittedEventId().getId() == reviewedEventId
        })
    }

    static def getHistoryRequestArgumentMatcher(subjectId, eventId) {
        argThat({ GetSubmittedEventHistory.Request req ->
            req.getSubjectId().getId() == subjectId && req.getEventId().getId() == eventId
        })
    }

    static def getItemProvenanceRequestArgumentMatcher(subjectId, eventId, itemId) {
        argThat({ GetSubmittedItemProvenance.Request req ->
            req.getSubjectId().getId() == subjectId &&
                    req.getSubmittedEventId().getId() == eventId &&
                    req.getSubmittedItemId().getId() == itemId
        })
    }

    private static GetSubmittedEvent.Response createGetSubmittedEventResponse(String populator) {
        return GetSubmittedEvent.Response.newBuilder()
                .withReviewTime(Instant.parse("2019-08-27T07:35:26.116Z"))
                .withPopulationTime(Instant.parse("2019-07-06T07:35:26.116Z"))
                .withReferenceDate(LocalDate.of(2012, 4, 13))
                .withPopulator(populator)
                .withReviewer(UserIdentifier.of("test-user"))
                .withReviewedForms(Arrays.asList(
                        GetSubmittedEvent.Form.newBuilder()
                                .withId(FormId.of("ba8c92ab-3040-493a-8575-f82f35527b40"))
                                .withName("Demographics")
                                .withItemGroups(Collections.singletonList(
                                        GetSubmittedEvent.ItemGroup.newBuilder()
                                                .withId(ItemGroupDefinitionId.of("8e3f7f89-3f94-40e7-b766-07526470ddab"))
                                                .withName("DM_GL_900")
                                                .withItems(Arrays.asList(
                                                        GetSubmittedEvent.Item.newBuilder()
                                                                .withId(SubmittedItemId.of("eaea28c6-64ce-4d75-a259-b85e0e67ff06"))
                                                                .withName("What is the subject's date of birth?")
                                                                .withValue("28 Sep 1918")
                                                                .build(),
                                                        GetSubmittedEvent.Item.newBuilder()
                                                                .withId(SubmittedItemId.of("d8aaf0f9-13fb-4f98-9f54-adfeeb96269b"))
                                                                .withName("What is the sex of the subject?")
                                                                .withValue("M")
                                                                .build()
                                                ))
                                                .build()
                                ))
                                .build(),
                        GetSubmittedEvent.Form.newBuilder()
                                .withId(FormId.of("c04918eb-391f-4df0-9135-1493361818d3"))
                                .withName("Hematology")
                                .withItemGroups(Collections.singletonList(
                                        GetSubmittedEvent.ItemGroup.newBuilder()
                                                .withId(ItemGroupDefinitionId.of("c369f4a8-60f5-4ef1-8b16-8be87f5d71d6"))
                                                .withName("LB_GL_903")
                                                .withItems(Arrays.asList(
                                                        GetSubmittedEvent.Item.newBuilder()
                                                                .withId(SubmittedItemId.of("989b7e32-7b2f-4f1b-a8f0-72c9d947a99e"))
                                                                .withName("Collection Date")
                                                                .withValue("26 Jan 2015")
                                                                .build(),
                                                        GetSubmittedEvent.Item.newBuilder()
                                                                .withId(SubmittedItemId.of("ea6baa1b-3e9c-4b45-b559-fce4032b3a2f"))
                                                                .withName("White blood cell (WBC) count")
                                                                .withValue("198")
                                                                .withUnit("x10E9/L")
                                                                .build(),
                                                        GetSubmittedEvent.Item.newBuilder()
                                                                .withId(SubmittedItemId.of("a8c85d5f-e5ba-4333-9fb2-9d992b9ed856"))
                                                                .withName("Platelet Count")
                                                                .withValue("198")
                                                                .withUnit("x10E9/L")
                                                                .build()
                                                ))
                                                .build()
                                ))
                                .build()
                ))
                .build()

    }

    private static GetSubmittedEventHistory.Response createGetSubmittedEventHistoryEmptyResponse() {
        return GetSubmittedEventHistory.Response.newBuilder()
                .withHistoryItems(Collections.emptyList())
                .build()

    }

    private static GetSubmittedEventHistory.Response createGetSubmittedEventHistoryResponse() {
        return GetSubmittedEventHistory.Response.newBuilder()
                .withHistoryItems(Arrays.asList(
                        GetSubmittedEventHistory.SubmittedEventHistoryItem.newBuilder()
                                .withReviewDateTime(Instant.parse("2017-10-19T08:43:00Z"))
                                .withReviewedEventId(SubmittedEventId.of("edc9bfbc-ec43-4894-bc1c-c0871b935407"))
                                .withReviewer(UserIdentifier.of("Test user"))
                                .build(),
                        GetSubmittedEventHistory.SubmittedEventHistoryItem.newBuilder()
                                .withReviewDateTime(Instant.parse("2016-05-07T15:01:00Z"))
                                .withReviewedEventId(SubmittedEventId.of("eced7cb3-c74e-42c7-b702-1f4810618968"))
                                .withReviewer(UserIdentifier.of("Test user"))
                                .build()
                ))
                .build()

    }

    private static GetSubmittedItemProvenance.Response mockGetReviewedItemProvenanceResponse() {
        return GetSubmittedItemProvenance.Response.newBuilder()
                .withGroups([new GetSubmittedItemProvenance.ProvenanceGroup("Group 1", [
                        new GetSubmittedItemProvenance.ProvenanceItem("Item 1", "Value 1"),
                        new GetSubmittedItemProvenance.ProvenanceItem("Item 2", ""),
                        new GetSubmittedItemProvenance.ProvenanceItem("Item 3", null)
                ])])
                .withItems([new GetSubmittedItemProvenance.ProvenanceItem("Item 4", "Value 4"),
                            new GetSubmittedItemProvenance.ProvenanceItem("Item 5", ""),
                            new GetSubmittedItemProvenance.ProvenanceItem("Item 6", null)])
                .build()
    }

    static def readJsonForController(String path) {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("samples/reviewedeventcontroller/" + path + ".json").getFile()))
    }

    def verifyForm(SubmitReviewedEvent.ReviewedForm reviewedForm) {
        with(reviewedForm) {
            id.id.startsWith("form-")
            !itemGroups.empty
            itemGroups.forEach({ group -> verifyGroup(group) })
        }
    }

    def verifyGroup(SubmitReviewedEvent.ItemGroup itemGroup) {
        with(itemGroup) {
            id.id.startsWith("group-")
            !items.empty
            items.forEach({ item -> verifyItem(item) })
        }
    }

    def verifyItem(SubmitReviewedEvent.Item item) {
        with(item) {
            id.id.startsWith("item-")
        }
    }
}