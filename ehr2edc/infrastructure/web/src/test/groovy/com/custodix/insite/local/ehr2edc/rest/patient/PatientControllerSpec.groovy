package com.custodix.insite.local.ehr2edc.rest.patient

import com.custodix.insite.local.ehr2edc.query.ListAvailableStudies
import com.custodix.insite.local.ehr2edc.query.ListRegisteredStudies
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.rest.patient.PatientController
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.FileCopyUtils

import java.time.LocalDate

import static java.nio.charset.StandardCharsets.UTF_8
import static org.mockito.ArgumentMatchers.any
import static org.mockito.BDDMockito.given
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = PatientController)
class PatientControllerSpec extends ControllerSpec {

    static final String BASE_ENDPOINT = "/ehr2edc/patient"

    @MockBean
    ListRegisteredStudies listRegisteredStudies
    @MockBean
    ListAvailableStudies listAvailableStudies

    def "List all studies (available and registered) for a Patient, with no studies"() {
        given: "No known studies"
        given(listRegisteredStudies.registeredStudies(any())).willReturn(registeredStudiesResponseWithoutResults())
        given(listAvailableStudies.availableStudies(any())).willReturn(availableStudiesResponseWithoutResults())

        when: "the all studies endpoint is called"
        def response = mockMvc.perform(get(BASE_ENDPOINT + "/HIS_01/PID_042/studies")).andReturn().response

        then: "the response contains no studies"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("allStudiesForPatient-noStudies-response"), JSONCompareMode.STRICT)
    }

    def "List all studies (available and registered) for a Patient, with only a registered study"() {
        given: "No known studies"
        given(listRegisteredStudies.registeredStudies(any())).willReturn(registeredStudiesResponseWithResults())
        given(listAvailableStudies.availableStudies(any())).willReturn(availableStudiesResponseWithoutResults())

        when: "the all studies endpoint is called"
        def response = mockMvc.perform(get(BASE_ENDPOINT + "/HIS_01/PID_042/studies")).andReturn().response

        then: "the response contains no studies"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(
                readJsonForController("allStudiesForPatient-onlyRegisteredStudy-response"),response.contentAsString, JSONCompareMode.STRICT)
    }

    def "List all studies (available and registered) for a Patient, with only an available study"() {
        given: "No known studies"
        given(listRegisteredStudies.registeredStudies(any())).willReturn(registeredStudiesResponseWithoutResults())
        given(listAvailableStudies.availableStudies(any())).willReturn(availableStudiesResponseWithResults())

        when: "the all studies endpoint is called"
        def response = mockMvc.perform(get(BASE_ENDPOINT + "/HIS_01/PID_042/studies")).andReturn().response

        then: "the response contains no studies"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("allStudiesForPatient-onlyAvailableStudy-response"), JSONCompareMode.STRICT)
    }

    def "List all studies (available and registered) for a Patient, with both registered and available study"() {
        given: "No known studies"
        given(listRegisteredStudies.registeredStudies(any())).willReturn(registeredStudiesResponseWithResults())
        given(listAvailableStudies.availableStudies(any())).willReturn(availableStudiesResponseWithResults())

        when: "the all studies endpoint is called"
        def response = mockMvc.perform(get(BASE_ENDPOINT + "/HIS_01/PID_042/studies")).andReturn().response

        then: "the response contains no studies"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(readJsonForController("allStudiesForPatient-response"),response.contentAsString,
                 JSONCompareMode.STRICT)
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot list all studies for a patient"() {
        when: "the all studies endpoint is called"
        ResultActions resultActions = mockMvc.perform(get(BASE_ENDPOINT + "/HIS_01/PID_042/studies"))

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    static def registeredStudiesResponseWithoutResults() {
        return ListRegisteredStudies.Response.newBuilder().build()
    }

    static def registeredStudiesResponseWithResults() {
        LocalDate dateOfConsent = LocalDate.of(2019, 4, 18)
        List<ListRegisteredStudies.Study> registeredStudies = new ArrayList<>()
        registeredStudies.add(ListRegisteredStudies.Study.newBuilder()
                .withStudyId(StudyId.of("STUD_001"))
                .withName("A Study")
                .withDescription("A very serious study")
                .withSubject(ListRegisteredStudies.Subject.newBuilder()
                        .withSubjectId(SubjectId.of("SUBJ_001_ID"))
                        .withEdcSubjectReference(EDCSubjectReference.of("SUBJ_001_REF"))
                        .withPatientCDWReference(PatientCDWReference.newBuilder()
                                .withSource("HIS_01")
                                .withId("PID_042")
                                .build())
                        .withDateOfConsent(dateOfConsent)
                        .build())
                .build())
        return ListRegisteredStudies.Response.newBuilder()
                .withRegisteredStudies(registeredStudies)
                .build()
    }

    static def availableStudiesResponseWithoutResults() {
        return ListAvailableStudies.Response.newBuilder().build()
    }

    static def availableStudiesResponseWithResults() {
        List<ListAvailableStudies.Study> availableStudies = new ArrayList<>()
        availableStudies.add(ListAvailableStudies.Study.newBuilder()
                .withStudyId(StudyId.of("STUD_002"))
                .withName("Another Study")
                .withDescription("Another very serious study")
                .build())
        return ListAvailableStudies.Response.newBuilder()
                .withAvailableStudies(availableStudies)
                .build()
    }

    static String readJsonForController(String path) {
        def sampleResource = new ClassPathResource("samples/patientcontroller/"+path+".json")
        return new String(FileCopyUtils.copyToByteArray(sampleResource.inputStream), UTF_8)
    }

}
