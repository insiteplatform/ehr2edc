package com.custodix.insite.local.ehr2edc.rest.study

import com.custodix.insite.local.ehr2edc.query.GetEvent
import com.custodix.insite.local.ehr2edc.query.GetItemProvenance
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.vocabulary.*
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
import java.util.stream.IntStream

import static java.util.Collections.emptyList
import static java.util.Collections.singletonList
import static java.util.stream.Collectors.toList
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.BDDMockito.given
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = PopulatedEventsController)
class PopulatedEventsControllerSpec extends ControllerSpec {

    private static final Instant POPULATION_TIME = Instant.parse("2019-03-04T05:06:07Z")
    private static final LocalDate REFERENCE_DATE = LocalDate.of(2015, 3, 9)
    private static final Random RANDOM = new Random(12354)

    static final String URL_GET_POPULATED_EVENT =
            "/ehr2edc/studies/{studyId}/subjects/{subjectId}/events/populated/{eventId}"
    static final String URL_GET_POPULATED_ITEM_PROVENANCE =
            "/ehr2edc/studies/{studyId}/subjects/{subjectId}/events/populated/{eventId}/items/{itemId}/provenance"

    private static final StudyId STUDY_ID = StudyId.of("study-1")
    private static final SubjectId SUBJECT_ID = SubjectId.of("subject-1")
    private static final EventDefinitionId EVENT_ID = EventDefinitionId.of("event-1")
    private static final ItemId ITEM_ID = ItemId.of("item-1")

    @MockBean
    GetEvent getEvent
    @MockBean
    GetItemProvenance getItemProvenance

    def "Get event returns an event"() {
        given: "The getEvent use case returns the event"
        given(getEvent.getEvent(getEventRequestArgumentMatcher(SUBJECT_ID.id, EVENT_ID.id)))
                .willReturn(createLatestResponse())

        when: "The get event endpoint is called"
        def request = get(URL_GET_POPULATED_EVENT, STUDY_ID.id, SUBJECT_ID.id, EVENT_ID.id)
        def response = mockMvc.perform(request).andReturn().response

        then: "The response contains the event"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(readJsonForController("getEvent"), response.contentAsString, STRICT)
    }

    def "Get event returns an event with multiple forms"() {
        given: "The getEvent use case returns the event with multiple forms"
        given(getEvent.getEvent(getEventRequestArgumentMatcher(SUBJECT_ID.id, EVENT_ID.id)))
                .willReturn(createResponse(createForms()))

        when: "The get event endpoint is called"
        def request = get(URL_GET_POPULATED_EVENT, STUDY_ID.id, SUBJECT_ID.id, EVENT_ID.id)
        def response = mockMvc.perform(request).andReturn().response

        then: "The response contains the correct event with multiple forms"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(readJsonForController("getEvent-forms-present"), response.contentAsString, STRICT)
    }

    def "Get event returns an event with an empty list of forms"() {
        given: "The getEvent use case returns the event with an empty list of forms"
        given(getEvent.getEvent(getEventRequestArgumentMatcher(SUBJECT_ID.id, EVENT_ID.id)))
                .willReturn(createEmptyResponse())

        when: "The get event endpoint is called"
        def request = get(URL_GET_POPULATED_EVENT, STUDY_ID.id, SUBJECT_ID.id, EVENT_ID.id)
        def response = mockMvc.perform(request).andReturn().response

        then: "The response contains an event with an empty list of forms"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(readJsonForController("getEvent-forms-empty"), response.contentAsString, STRICT)
    }

    def "Get item provenance returns a correct result"() {
        given: "The GetItemProvenance use case returns the provenance"
        given(getItemProvenance.get(getItemProvenanceRequestArgumentMatcher(SUBJECT_ID.id, EVENT_ID.id, ITEM_ID.id)))
                .willReturn(mockGetItemProvenanceResponse())

        when: "I call the endpoint"
        def request = get(URL_GET_POPULATED_ITEM_PROVENANCE, STUDY_ID.id, SUBJECT_ID.id, EVENT_ID.id, ITEM_ID.id)
        def response = mockMvc.perform(request).andReturn().response

        then: "The response contains the item provenance of type #type"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(readJsonForController("getItemProvenance"), response.contentAsString, STRICT)
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot get item provenance"() {
        when: "I get an item provenance"
        def request = get(URL_GET_POPULATED_ITEM_PROVENANCE, STUDY_ID.id, SUBJECT_ID.id, EVENT_ID.id, ITEM_ID.id)
        ResultActions resultActions =  mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    static def getEventRequestArgumentMatcher(subjectId, eventId) {
        argThat({ GetEvent.Request req ->
            req.getSubjectId().getId() == subjectId && req.getEventId().getId() == eventId
        })
    }

    static def getItemProvenanceRequestArgumentMatcher(subjectId, eventId, itemId) {
        argThat({ GetItemProvenance.Request req ->
            req.getSubjectId().getId() == subjectId &&
                    req.getEventId().getId() == eventId &&
                    req.getItemId().getId() == itemId
        })
    }

    def createResponse(List<GetEvent.Form> forms) {
        GetEvent.Response.newBuilder().withEvent(GetEvent.Event.newBuilder()
                .withForms(forms)
                .withName("eventName")
                .build()).build()
    }

    def createLatestResponse() {
        createResponse(singletonList(createLatestForm()))
    }

    def createEmptyResponse() {
        createResponse(emptyList())
    }

    def createForms() {
        IntStream.range(0, 5).mapToObj({ i -> createForm(i) }).collect(toList())
    }

    def createForm(int formIndex) {
        GetEvent.Form.newBuilder()
                .withId(FormId.of("form-" + (formIndex + 1)))
                .withName("Form nr. " + (formIndex + 1))
                .withReferenceDate(REFERENCE_DATE)
                .withPopulationTime(POPULATION_TIME)
                .withItemGroups(createItemGroups())
                .build()
    }

    def createLatestForm() {
        GetEvent.Form.newBuilder()
                .withId(FormId.of("form-latest"))
                .withName("Latest form")
                .withReferenceDate(REFERENCE_DATE)
                .withPopulationTime(POPULATION_TIME)
                .withItemGroups(createItemGroupForLatestForm())
                .build()
    }

    def createItemGroups() {
        IntStream.range(0, 1 + RANDOM.nextInt(2)).mapToObj({ i -> createItemGroup(i) }).collect(toList())
    }

    def createItemGroupForLatestForm() {
        singletonList(
                GetEvent.ItemGroup.newBuilder()
                        .withId(ItemGroupId.of("group-1"))
                        .withName("Group nr. 1")
                        .withItems(createItemForLatestForm())
                        .build()
        )
    }

    def createItemGroup(int itemGroupIndex) {
        GetEvent.ItemGroup.newBuilder()
                .withId(ItemGroupId.of("group-" + (itemGroupIndex + 1)))
                .withName("Group nr. " + (itemGroupIndex + 1))
                .withItems(createItems())
                .build()
    }

    def createItems() {
        IntStream.range(0, 1 + RANDOM.nextInt(7)).mapToObj({ i -> createItem(i) }).collect(toList())
    }

    def createItemForLatestForm() {
        singletonList(
                GetEvent.Item.newBuilder()
                        .withId(ItemId.of("item-1"))
                        .withName("Item nr. 1")
                        .withValue("itemValue")
                        .withExportable(true)
                        .withUnit(null)
                        .build()
        )
    }

    def createItem(int itemIndex) {
        GetEvent.Item.newBuilder()
                .withId(ItemId.of("item-" + (itemIndex + 1)))
                .withName("Item nr. " + (itemIndex + 1))
                .withValue(Integer.toString(RANDOM.nextInt()))
                .withExportable(true)
                .withUnit("kg")
                .build()
    }

    static GetItemProvenance.Response mockGetItemProvenanceResponse() {
        return GetItemProvenance.Response.newBuilder()
                .withGroups([new GetItemProvenance.ProvenanceGroup("Group 1", [
                        new GetItemProvenance.ProvenanceItem("Item 1", "Value 1"),
                        new GetItemProvenance.ProvenanceItem("Item 2", ""),
                        new GetItemProvenance.ProvenanceItem("Item 3", null)
                ])])
                .withItems([new GetItemProvenance.ProvenanceItem("Item 4", "Value 4"),
                            new GetItemProvenance.ProvenanceItem("Item 5", ""),
                            new GetItemProvenance.ProvenanceItem("Item 6", null)])
                .build()
    }

    static def readJsonForController(String path) {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("samples/eventcontroller/" + path + ".json").getFile()))
    }
}