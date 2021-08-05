package com.custodix.insite.local.ehr2edc.rest.study

import com.custodix.insite.local.ehr2edc.command.DeregisterSubject
import com.custodix.insite.local.ehr2edc.command.RegisterSubject
import com.custodix.insite.local.ehr2edc.query.GetEventPopulationReadiness
import com.custodix.insite.local.ehr2edc.query.GetSubject
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.vocabulary.*
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

import java.time.LocalDate

import static java.nio.charset.StandardCharsets.UTF_8
import static org.mockito.ArgumentMatchers.any
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.*
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = StudySubjectController)
class StudySubjectControllerSpec extends ControllerSpec {

    static final String URL_PUT_SUBJECT = "/ehr2edc/studies/{studyId}/subjects"
    static final String URL_SUBJECT = "/ehr2edc/studies/{studyId}/subjects/{subjectId}"

    static final LocalDate FIRST = LocalDate.of(2019, 04, 12)
    static final LocalDate LAST = LocalDate.of(2019, 05, 12)
    static final LocalDate DATE_2009_09_09 = LocalDate.of(2009, 9, 9)

    static final StudyId STUDY_ID = StudyId.of('SID_01')
    static final SubjectId SUBJECT_ID = SubjectId.of('SUBJ_042')

    @MockBean
    RegisterSubject registerSubject
    @MockBean
    DeregisterSubject deregisterSubject 
    @MockBean
    GetSubject getSubjectInStudy
    @MockBean
    GetEventPopulationReadiness isStudyReadyForEventPopulation

    def "Register Patient as a new Subject to a Study"() {
        given: "A registration request"
        String content = readJsonForController("registerSubject-request")
        doReturn(RegisterSubject.Response.newBuilder().withSubjectId(SUBJECT_ID).build()).when(registerSubject).register(any())

        when: "Registering the Subject"
        def request = put(URL_PUT_SUBJECT, STUDY_ID.id).contentType(APPLICATION_JSON).content(content)
        def response = mockMvc.perform(request).andReturn().response
        def captor = ArgumentCaptor.forClass(RegisterSubject.Request)
        verify(registerSubject).register(captor.capture())

        then: "The response indicates that the registration succeeded"
        response.status == HttpStatus.CREATED.value()
        and: "An empty response body is returned"
        response.contentAsString == ""
        and: "The request matches the expected parameters"
        def value = captor.value
        value.patientId.id == "PID_01"
        value.patientId.source == "HIS_01"
        value.studyId.id == STUDY_ID.id
        value.edcSubjectReference.id == "SUB_01_REF"
        value.dateOfConsent == FIRST
        value.lastName == "Smith"
        value.firstName == "John"
        value.birthDate == DATE_2009_09_09
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot register a patient as a subject in a study"() {
        when: "I register a patient as a subject in a study"
        def content = readJsonForController("registerSubject-request")
        def request = put(URL_PUT_SUBJECT, STUDY_ID.id).contentType(APPLICATION_JSON).content(content)
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    def "Deregister Patient as Subject from a Study"() {
        given: "A deregistration request"
        String content = readJsonForController("deregisterSubject-request")
        doNothing().when(deregisterSubject).deregister(any())

        when: "Deregistering the Subject"
        def request = delete(URL_SUBJECT, STUDY_ID.id, SUBJECT_ID.id)
                .contentType(APPLICATION_JSON)
                .content(content)
        def response = mockMvc.perform(request).andReturn().response
        def captor = ArgumentCaptor.forClass(DeregisterSubject.Request)
        verify(deregisterSubject).deregister(captor.capture())

        then: "The response indicates that the deregistration succeeded"
        response.status == HttpStatus.NO_CONTENT.value()
        and: "An empty response body is returned"
        response.contentAsString == ""
        and: "The request matches the expected parameters"
        def value = captor.value
        value.studyId.id == STUDY_ID.id
        value.subjectId.id == SUBJECT_ID.id
        value.endDate == LAST
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot deregister a patient as a subject from a study"() {
        when: "I deregister a patient as a subject in a study"
        def content = readJsonForController("deregisterSubject-request")
        def request = delete(URL_SUBJECT, STUDY_ID.id, SUBJECT_ID.id)
                .contentType(APPLICATION_JSON)
                .content(content)
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    def "GetSubjectInStudy "() {
        given: "There is a known study with investigators or subjects"
        given(getSubjectInStudy.getSubject(any())).willReturn(aSubjectInAStudy())

        when: "The details of a subject in the study are asked"
        def request = get(URL_SUBJECT, STUDY_ID.id, "SUBJ_042_4")
        def response = mockMvc.perform(request).andReturn().response

        then: "the response will contain the details of a known subject for the study"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("getSubjectInStudy-knownStudy-knownSubject"),
                JSONCompareMode.STRICT)
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot get a subject in a study"() {
        when: "I get a subject in a study"
        def request = get(URL_SUBJECT, STUDY_ID.id, "SUBJ_042_4")
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    static def aSubjectInAStudy() {
        return GetSubject.Response.newBuilder()
                .withSubject(GetSubject.Subject.newBuilder()
                        .withPatientId(PatientCDWReference.newBuilder().withSource("HIS_001").withId("PID_042").build())
                        .withSubjectId(SubjectId.of("SUBJ_042_4"))
                        .withEdcSubjectReference(EDCSubjectReference.of("edc-subj"))
                        .withDateOfConsent(FIRST)
                        .withDateOfConsentWithdrawn(LAST)
                        .withDataCaptureStopReason(DeregisterReason.CONSENT_RETRACTED)
                        .build())
                .build()
    }

    static String readJsonForController(String path) {
        def sampleResource = new ClassPathResource("samples/studysubjectcontroller/"+path+".json")
        return new String(FileCopyUtils.copyToByteArray(sampleResource.inputStream), UTF_8)
    }

}
