package com.custodix.insite.local.ehr2edc.rest.study

import com.custodix.insite.local.ehr2edc.command.CreateStudy
import com.custodix.insite.local.ehr2edc.command.UpdateStudyMetadata
import com.custodix.insite.local.ehr2edc.query.GetStudy
import com.custodix.insite.local.ehr2edc.query.ListAllStudies
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.rest.util.MockRemoteAddr
import com.custodix.insite.local.ehr2edc.vocabulary.*
import org.mockito.ArgumentCaptor
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.util.FileCopyUtils

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.rest.util.MockRemoteAddr.localhostIPv4
import static com.custodix.insite.local.ehr2edc.rest.util.MockRemoteAddr.localhostIPv6
import static java.nio.charset.StandardCharsets.UTF_8
import static java.util.Collections.singletonList
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.verify
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [StudyController])
class StudyControllerSpec extends ControllerSpec {

    private static final String ENDPOINT_STUDIES = "/ehr2edc/studies"
    private static final String ENDPOINT_GET_STUDY = "/ehr2edc/studies/{studyId}"
    private static final String ENDPOINT_UPDATE_STUDY = "/ehr2edc/studies/{studyId}/metadata"

    private static final RequestPostProcessor METHOD_PUT = { req -> req.setMethod("PUT"); return req }

    @MockBean
    ListAllStudies listAllStudies
    @MockBean
    GetStudy getStudy
    @MockBean
    CreateStudy createStudy
    @MockBean
    UpdateStudyMetadata updateStudyMetadata

    def "Get study returns a study without investigators or subjects"() {
        given: "There is a known study with id STUDY-1 without investigators or subjects"
        given(getStudy.getStudy(any())).willReturn(aStudyWithoutInvestigatorsOrSubjects())

        when: "The get study endpoint is called"
        def request = get(ENDPOINT_GET_STUDY, "STUDY-1")
        def response = mockMvc.perform(request).andReturn().response

        then: "the response contains all known studies"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("getStudy-knownStudy-response-without-investigators-or-subjects"),
                JSONCompareMode.STRICT)
    }

    def "Get study returns a study with investigators and subjects"() {
        given: "There is a known study with id STUDY-1 without investigators or subjects"
        given(getStudy.getStudy(any())).willReturn(aStudyWithInvestigatorsAndSubjectsWithConsentDate())

        when: "The get study endpoint is called"
        def request = get(ENDPOINT_GET_STUDY, "STUDY-1")
        def response = mockMvc.perform(request).andReturn().response

        then: "the response contains all known studies"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("getStudy-knownStudy-response-with-investigators-and-subjects"),
                JSONCompareMode.STRICT)
    }

    def "Get study returns a study with writable EDC connection"() {
        given: "There is a known study with id STUDY-1 without investigators or subjects"
        given(getStudy.getStudy(any())).willReturn(aStudyWithWritableEDCConnection())

        when: "The get study endpoint is called"
        def request = get(ENDPOINT_GET_STUDY, "STUDY-1")
        def response = mockMvc.perform(request).andReturn().response

        then: "the response contains all known studies"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("getStudy-knownStudy-response-with-writable-edc"),
                JSONCompareMode.STRICT)
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot get study"() {
        when: "The get study endpoint is called"
        def request = get(ENDPOINT_GET_STUDY, "STUDY-1")
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    def "ListAllStudies returns a list of known studies"() {
        given: "There are known studies"
        given(listAllStudies.allStudies()).willReturn(allStudiesResponseWithResults())

        when: "The all studies endpoint is called"
        def response = mockMvc.perform(get(ENDPOINT_STUDIES)).andReturn().response

        then: "the response contains all known studies"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("listAllStudies-knownStudies-response"),
                JSONCompareMode.STRICT)
    }

    def "ListAllStudies returns an empty list if there are no studies"() {
        given: "No known studies"
        given(listAllStudies.allStudies()).willReturn(allStudiesResponseWithoutResults())

        when: "the all studies endpoint is called"
        def response = mockMvc.perform(get(ENDPOINT_STUDIES)).andReturn().response

        then: "the response contains no studies"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("listAllStudies-noStudies-response"), JSONCompareMode.STRICT)
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot get list of studies"() {
        when: "The all studies endpoint is called"
        ResultActions resultActions = mockMvc.perform(get(ENDPOINT_STUDIES))

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    def "CreateStudy with a valid file returns the new study's ID"() {
        given: "A valid file upload request"
        def odm = readSampleStudy()
        def request = multipart(ENDPOINT_STUDIES)
                .file("file", odm.getBytes(UTF_8))
        and: "The use case returns the study id"
        given(createStudy.create(argThat({ r -> r.studyODM == StudyODM.of(odm) }))).willReturn(createStudyResponse())

        when: "The createStudy endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)
        def response = resultActions.andReturn().response

        then: "The response contains the studyId"
        response.status == HttpStatus.CREATED.value()
        response.getHeader("location") == "/ehr2edc/studies/EHR2EDC"
    }

    def "CreateStudy without a file fails"() {
        given: "An invalid file upload request"
        def request = multipart(ENDPOINT_STUDIES)

        when: "The createStudy endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response indicates a faulty call"
        response.status == HttpStatus.BAD_REQUEST.value()
    }

    @WithAnonymousUser
    def "An unauthenticated user can create a study from a localhost IPv4 address"() {
        given: "A valid file upload request from a localhost IPv4 address"
        def odm = readSampleStudy()
        def request = multipart(ENDPOINT_STUDIES)
                .file("file", odm.getBytes(UTF_8))
                .with(localhostIPv4())
        and: "The use case returns the study id"
        given(createStudy.create(argThat({ r -> r.studyODM == StudyODM.of(odm) }))).willReturn(createStudyResponse())

        when: "The createStudy endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is granted"
        resultActions.andExpect(status().isCreated())
    }

    @WithAnonymousUser
    def "An unauthenticated user can create a study from a localhost IPv6 address"() {
        given: "A valid file upload request from a localhost IPv6 address"
        def odm = readSampleStudy()
        def request = multipart(ENDPOINT_STUDIES)
                .file("file", odm.getBytes(UTF_8))
                .with(localhostIPv6())
        and: "The use case returns the study id"
        given(createStudy.create(argThat({ r -> r.studyODM == StudyODM.of(odm) }))).willReturn(createStudyResponse())

        when: "The createStudy endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is granted"
        resultActions.andExpect(status().isCreated())
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot create a study from a non-localhost address"() {
        given: "A valid file upload request from a non-localhost address"
        def odm = readSampleStudy()
        def request = multipart(ENDPOINT_STUDIES)
                .file("file", odm.getBytes(UTF_8))
                .with(MockRemoteAddr.nonLocalhost())

        when: "The createStudy endpoint is called"
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    def "UpdateStudyMetadata with a valid file succeeds"() {
        given: "A valid file upload request"
        def odm = readSampleStudy()
        MockMultipartFile file = new MockMultipartFile("file", odm.getBytes(UTF_8))
        def request = multipart(ENDPOINT_UPDATE_STUDY, "EHR2EDC").file(file)
                .with(METHOD_PUT)

        when: "The updateStudyMetadata endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response succeeds"
        response.status == HttpStatus.OK.value()
        and: "The use case was called"
        ArgumentCaptor argumentCaptor = ArgumentCaptor.forClass(UpdateStudyMetadata.Request)
        verify(updateStudyMetadata).update(argumentCaptor.capture())
        and: "Correct arguments were used"
        def value = argumentCaptor.getValue()
        value.studyODM == StudyODM.of(odm)
    }

    @WithAnonymousUser
    def "UpdateStudyMetadata with a valid file succeeds for a local anonymous user"() {
        given: "A valid file upload request"
        def odm = readSampleStudy()
        MockMultipartFile file = new MockMultipartFile("file", odm.getBytes(UTF_8))
        def request = multipart(ENDPOINT_UPDATE_STUDY, "EHR2EDC").file(file)
                .with(METHOD_PUT)
                .with(remoteAddr)

        when: "The updateStudyMetadata endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response succeeds"
        response.status == HttpStatus.OK.value()

        where:
        remoteAddr << [localhostIPv4(), localhostIPv6()]
    }

    @WithAnonymousUser
    def "UpdateStudyMetadata with a valid file fails for a non-local anonymous user"() {
        given: "A valid file upload request"
        def odm = readSampleStudy()
        MockMultipartFile file = new MockMultipartFile("file", odm.getBytes(UTF_8))
        def request = multipart(ENDPOINT_UPDATE_STUDY, "EHR2EDC")
                .file(file)
                .with(METHOD_PUT)
                .with(MockRemoteAddr.nonLocalhost())

        when: "The updateStudyMetadata endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response succeeds"
        response.status == HttpStatus.UNAUTHORIZED.value()
    }

    def "UpdateStudyMetadata without a file fails"() {
        given: "A valid file upload request"
        def request = multipart(ENDPOINT_UPDATE_STUDY, "EHR2EDC")
                .with(METHOD_PUT)

        when: "The updateStudyMetadata endpoint is called"
        def response = mockMvc.perform(request).andReturn().response

        then: "The response fails"
        response.status == HttpStatus.BAD_REQUEST.value()
    }

    static def createStudyResponse() {
        return CreateStudy.Response.newBuilder()
                .withStudyId(StudyId.of("EHR2EDC"))
                .build()
    }

    static def aStudyWithoutInvestigatorsOrSubjects() {
        return GetStudy.Response.newBuilder()
                .withStudy(GetStudy.Study.newBuilder()
                        .withDescription("Description of the test study")
                        .withName("Test study name")
                        .withId(StudyId.of("STUDY-1"))
                        .withPermissions(GetStudy.Permissions.newBuilder()
                                .withCanSubjectsBeAdded(true)
                                .build())
                        .build())
                .build()

    }

    private static GetStudy.Response aStudyWithInvestigatorsAndSubjectsWithConsentDate() {
        GetStudy.Response.newBuilder()
                .withStudy(GetStudy.Study.newBuilder()
                        .withDescription("Description of the test study with inv")
                        .withName("Test study name with inv")
                        .withId(StudyId.of("STUDY-1-inv"))
                        .withInvestigators(Arrays.asList(GetStudy.Investigator.newBuilder().withId(UserIdentifier.of("1")).withName("Gert").build()))
                        .withSubjects(Arrays.asList(GetStudy.Subject.newBuilder()
                                .withSubjectId(SubjectId.of("a"))
                                .withEdcSubjectReference(EDCSubjectReference.of("b"))
                                .withPatientId(PatientCDWReference.newBuilder().withId("pid").withSource("source").build())
                                .withConsentDateTime(LocalDate.of(2017, 7, 1))
                                .build()))
                        .withPermissions(GetStudy.Permissions.newBuilder()
                                .withCanSubjectsBeAdded(true)
                                .withCanSubjectsBeViewed(true)
                                .build())
                        .build())
                .build()
    }

    static def aStudyWithInvestigatorsAndSubjectsWithoutDetails() {
        return GetStudy.Response.newBuilder()
                .withStudy(GetStudy.Study.newBuilder()
                        .withDescription("Description of the test study with inv")
                        .withName("Test study name with inv")
                        .withId(StudyId.of("STUDY-1-inv"))
                        .withInvestigators(Arrays.asList(GetStudy.Investigator.newBuilder().withId(UserIdentifier.of("1")).withName("Gert").build()))
                        .withSubjects(Arrays.asList(GetStudy.Subject.newBuilder()
                                .withSubjectId(SubjectId.of("a"))
                                .withEdcSubjectReference(EDCSubjectReference.of("b"))
                                .build()))
                        .withPermissions(GetStudy.Permissions.newBuilder()
                                .withCanSubjectsBeAdded(true)
                                .withCanSubjectsBeViewed(false)
                                .build())
                        .build())
                .build()
    }

    static def aStudyWithWritableEDCConnection() {
        return GetStudy.Response.newBuilder()
                .withStudy(GetStudy.Study.newBuilder()
                        .withDescription("Description of the test study with inv")
                        .withName("Test study name with inv")
                        .withId(StudyId.of("STUDY-1-inv"))
                        .withInvestigators(Arrays.asList(GetStudy.Investigator.newBuilder().withId(UserIdentifier.of("1")).withName("Gert").build()))
                        .withSubjects(Arrays.asList(GetStudy.Subject.newBuilder()
                                .withSubjectId(SubjectId.of("a"))
                                .withEdcSubjectReference(EDCSubjectReference.of("b"))
                                .withPatientId(PatientCDWReference.newBuilder().withId("pid").withSource("source").build())
                                .withConsentDateTime(LocalDate.of(2017, 7, 1))
                                .build()
                        ))
                        .withPermissions(GetStudy.Permissions.newBuilder()
                                .withCanSubjectsBeAdded(true)
                                .withCanSubjectsBeViewed(true)
                                .withCanSendToEDC(true)
                                .build())
                        .build())
                .build()
    }

    static def allStudiesResponseWithResults() {
        return ListAllStudies.Response.newBuilder(singletonList(
                ListAllStudies.Study.newBuilder()
                        .withId("STUDY_ID")
                        .withName("STUDY")
                        .withDescription("DESCRIPTION")
                        .build()))
                .build()
    }

    static def allStudiesResponseWithoutResults() {
        return ListAllStudies.Response.newBuilder(Collections.emptyList())
                .build()
    }


    static String readSampleStudy() {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("min-sample-study.xml").getFile()))
    }

    static def readJsonForController(String path) {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("samples/studycontroller/" + path + ".json").getFile()))
    }
}
