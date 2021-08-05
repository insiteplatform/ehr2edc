package com.custodix.insite.local.ehr2edc.rest.study

import com.custodix.insite.local.ehr2edc.query.ListAvailablePatientIds
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.FileCopyUtils

import static java.nio.charset.StandardCharsets.UTF_8
import static org.mockito.ArgumentMatchers.any
import static org.mockito.BDDMockito.given
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = EHRPatientsController)
class EHRPatientsControllerSpec extends ControllerSpec {

    static final String RETRIEVE_PATIENTS = "/ehr2edc/ehr/patients?patientDomain={patientDomain}&studyId={studyId}"

    private static final StudyId STUDY_ID = StudyId.of('SID_01')
    private static final String PATIENT_DOMAIN = "domain"

    @MockBean
    ListAvailablePatientIds listAvailablePatientIds

    def "List available patient ids for a study"() {
        given: "A study id with edc information available"
        String url = RETRIEVE_PATIENTS + "?filter=123&limit=100"
        String content = readJsonForController("availablePatientIdsForStudy-response")
        given(listAvailablePatientIds.list(any())).willReturn(availablePatientIdsForStudy())

        when: "Listing the available subject ids for the study and a site reference"
        def request = get(url, PATIENT_DOMAIN, STUDY_ID.id)
        def response = mockMvc.perform(request).andReturn().response

        then: "the response lists all known subjects ids in the study for the site reference"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString, content, JSONCompareMode.STRICT)
    }

    def "List available patient ids for a study without optional parameters"() {
        given: "A study id with edc information available"
        String url = RETRIEVE_PATIENTS
        String content = readJsonForController("availablePatientIdsForStudy-response")
        given(listAvailablePatientIds.list(any())).willReturn(availablePatientIdsForStudy())

        when: "Listing the available subject ids for the study and a site reference"
        def request = get(url, PATIENT_DOMAIN, STUDY_ID.id)
        def response = mockMvc.perform(request).andReturn().response

        then: "the response lists all known subjects ids in the study for the site reference"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString, content, JSONCompareMode.STRICT)
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot get the list of available patient ids for a study"() {
        when: "Listing the available subject ids for the study and a site reference"
        def request = get(RETRIEVE_PATIENTS, PATIENT_DOMAIN, STUDY_ID.id)
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    static def availablePatientIdsForStudy() {
        return ListAvailablePatientIds.Response.newBuilder()
                .withPatientIds(["PAT_001", "PAT_002", "PAT_003"])
                .build()
    }

    static String readJsonForController(String path) {
        def sampleResource = new ClassPathResource("samples/ehrpatientscontroller/"+path+".json")
        return new String(FileCopyUtils.copyToByteArray(sampleResource.inputStream), UTF_8)
    }

}
