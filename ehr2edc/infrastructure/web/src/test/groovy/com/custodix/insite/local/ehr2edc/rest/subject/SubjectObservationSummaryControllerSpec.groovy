package com.custodix.insite.local.ehr2edc.rest.subject

import com.custodix.insite.local.ehr2edc.query.GetObservationSummary
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.rest.subject.SubjectObservationSummaryController
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.FileCopyUtils

import java.time.LocalDate
import java.util.stream.IntStream

import static java.nio.charset.StandardCharsets.UTF_8
import static java.util.Collections.emptyList
import static java.util.stream.Collectors.toList
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.BDDMockito.given
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@ContextConfiguration(classes = SubjectObservationSummaryController)
class SubjectObservationSummaryControllerSpec extends ControllerSpec {

    @MockBean
    private GetObservationSummary getObservationSummary

    def "Requesting the subject observation summary with summary items"() {
        given: "A subject has observation summary items"
        given(getObservationSummary.getSummary(argThat({ r -> subjectIdMatches(r, "subject-1") })))
                .willReturn(GetObservationSummary.Response.newBuilder()
                        .withSummaryItems(summaryItems())
                        .build())

        when: "I request the summary items for that subject"
        def response = mockMvc.perform(get("/ehr2edc/studies/{studyId}/subjects/{edcSubjectReference}/observations", "study-1", "subject-1")).andReturn().response

        then: "The response returns all summary items"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("getSubjectObservationSummary"),
                JSONCompareMode.STRICT)
    }

    def "Requesting the subject observation summary with no summary items"() {
        given: "A subject has observation summary items"
        given(getObservationSummary.getSummary(argThat({ r -> subjectIdMatches(r, "subject-1") })))
                .willReturn(GetObservationSummary.Response.newBuilder()
                        .withSummaryItems(emptyList())
                        .build())

        when: "I request the summary items for that subject"
        def response = mockMvc.perform(get("/ehr2edc/studies/{studyId}/subjects/{edcSubjectReference}/observations", "study-1", "subject-1")).andReturn().response

        then: "The response returns all summary items"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("getSubjectObservationSummary-noObservations"),
                JSONCompareMode.STRICT)
    }

    private static boolean subjectIdMatches(GetObservationSummary.Request request, String subjectId) {
        request.subjectId.id == subjectId
    }

    private List<GetObservationSummary.SummaryItem> summaryItems() {
        IntStream.range(1, 31)
                .mapToObj({ makeSummaryItem(it) })
                .collect(toList())
    }

    private static GetObservationSummary.SummaryItem makeSummaryItem(int day) {
        GetObservationSummary.SummaryItem.newBuilder()
                .withAmountOfObservations(3)
                .withDate(LocalDate.of(2013, 6, day))
                .build()
    }



    static String readJsonForController(String path) {
        def sampleResource = new ClassPathResource("samples/subjectobservationsummarycontroller/"+path+".json")
        return new String(FileCopyUtils.copyToByteArray(sampleResource.inputStream), UTF_8)
    }
}