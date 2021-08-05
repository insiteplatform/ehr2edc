package com.custodix.insite.local.ehr2edc.rest.study

import com.custodix.insite.local.ehr2edc.command.DeregisterSubject
import com.custodix.insite.local.ehr2edc.command.RegisterSubject
import com.custodix.insite.local.ehr2edc.query.GetEventPopulationReadiness
import com.custodix.insite.local.ehr2edc.query.GetSubject
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.rest.EHR2EDCControllerAdviceSpec
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.FileCopyUtils

import java.nio.charset.StandardCharsets

import static com.custodix.insite.local.ehr2edc.vocabulary.PopulationNotReadyReason.SUBJECT_MIGRATION_NOT_EXISTING
import static com.custodix.insite.local.ehr2edc.vocabulary.PopulationNotReadyReason.SUBJECT_MIGRATION_STARTED
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.BDDMockito.given
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@ContextConfiguration(classes = [StudySubjectController, EHR2EDCControllerAdviceSpec])
class StudySubjectControllerGetEventPopulationReadinessSpec extends ControllerSpec {

    private static final String BASE_ENDPOINT = "/ehr2edc/studies/{studyId}/subjects/{subjectId}/event-population-readiness"
    private static final String SUBJECT_ID_1 = "subject-1"
    private static final String STUDY_ID_1 = "study-1"
    public static final String PATIENT_EXPORTER_ID = "patientExporterId-098"

    @MockBean
    private RegisterSubject registerSubject
    @MockBean
    private DeregisterSubject deregisterSubject
    @MockBean
    private GetSubject getSubject
    @MockBean
    private GetEventPopulationReadiness getEventPopulationReadiness

    def "Is study ready for population returns ready"() {
        given: "A study is ready for even population"
        given(getEventPopulationReadiness.getEventPopulationReadiness(argThat({ r -> requestMatches(r, STUDY_ID_1, SUBJECT_ID_1) })))
                .willReturn(GetEventPopulationReadiness.Response.newBuilder()
                .withReady(true)
                .build())

        when: "requesting whether the study is ready"
        def response = mockMvc.perform(get(BASE_ENDPOINT, STUDY_ID_1, SUBJECT_ID_1)).andReturn().response

        then: "The response returns study is ready for population"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("isReadyForPopulation"),
                JSONCompareMode.STRICT)

    }

    def "Is study ready for population returns not ready because subject migration has started"() {
        given: "A study is not ready for even population because subject migration has started"
        given(getEventPopulationReadiness.getEventPopulationReadiness(argThat({ r -> requestMatches(r, STUDY_ID_1, SUBJECT_ID_1) })))
                .willReturn(GetEventPopulationReadiness.Response.newBuilder()
                        .withReady(false)
                        .withNotReadyReason(SUBJECT_MIGRATION_STARTED)
                        .withSubjectMigrationInProgress(true)
                        .build())

        when: "requesting whether the study is ready"
        def response = mockMvc.perform(get(BASE_ENDPOINT, STUDY_ID_1, SUBJECT_ID_1)).andReturn().response

        then: "The response returns study is not ready for population"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("isNotReadyBecauseSubjectMigrationHasStarted"),
                JSONCompareMode.STRICT)

    }

    def "Is study ready for population returns not ready because subject migration is not existing"() {
        given: "A study is not ready for even population because subject migration is not present"
        given(getEventPopulationReadiness.getEventPopulationReadiness(argThat({ r -> requestMatches(r, STUDY_ID_1, SUBJECT_ID_1) })))
                .willReturn(GetEventPopulationReadiness.Response.newBuilder()
                        .withReady(false)
                        .withNotReadyReason(SUBJECT_MIGRATION_NOT_EXISTING)
                        .withSubjectMigrationInProgress(true)
                        .build())

        when: "requesting whether the study is ready"
        def response = mockMvc.perform(get(BASE_ENDPOINT, STUDY_ID_1, SUBJECT_ID_1)).andReturn().response

        then: "The response returns study is not ready for population"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("isNotReadyBecauseSubjectMigrationNotExisting"),
                JSONCompareMode.STRICT)

    }

    private boolean requestMatches(GetEventPopulationReadiness.Request request, String studyId, String subjectId) {
        request.studyId.id == studyId && request.subjectId.id == subjectId
    }

    static String readJsonForController(String path) {
        def sampleResource = new ClassPathResource("samples/studysubjectcontroller/geteventpopulationreadiness/"+path+".json")
        return new String(FileCopyUtils.copyToByteArray(sampleResource.inputStream), StandardCharsets.UTF_8)
    }
}
