package com.custodix.insite.local.ehr2edc.rest.study

import com.custodix.insite.local.ehr2edc.command.LinkConnection
import com.custodix.insite.local.ehr2edc.query.ListAvailableSubjectIds
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType
import org.assertj.core.util.Lists
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

import static com.custodix.insite.local.ehr2edc.rest.util.MockRemoteAddr.*
import static com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem.RAVE
import static java.nio.charset.StandardCharsets.UTF_8
import static org.mockito.ArgumentMatchers.any
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.verify
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = [EDCStudyController])
class EDCStudyControllerSpec extends ControllerSpec {

    static final String LINK_EDC_REFERENCE_ENDPOINT = "/ehr2edc/edc/connection"
    static final String RETRIEVE_SUBJECTS_FROM_EDC_ENDPOINT = "/ehr2edc/edc/subjects?studyId={studyId}"

    @MockBean
    LinkConnection linkConnection

    @MockBean
    ListAvailableSubjectIds listAvailableSubjectIds

    def "Assign a site reference to a study"() {
        given: "A known study to link a site reference to"
        String url = LINK_EDC_REFERENCE_ENDPOINT
        String content = readJsonForController("linkReadSubjectsEDC-request")

        when: "Assigning the reference to the study"
        mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(content))
                .andExpect(status().is2xxSuccessful())

        then: "The reference is successfully passed to the usecase"
        def captor = ArgumentCaptor.forClass(LinkConnection.Request)
        verify(linkConnection).link(captor.capture())
        with(captor.value) {
            studyId.id == "56021927PCR3011"
            externalSiteId.id == "123456"
            edcSystem == RAVE
            clinicalDataURI == new URI("http:/localhost:8081/RaveWebServices/studies/raveStudyId/Subjects")
            username == "user1"
            password == "user1Pass"
            studyIdOverride.id == "otherStudyId"
            type == StudyConnectionType.READ_SUBJECTS
            enabled
        }
    }

    def "Assign a connection to a study"() {
        given: "A known study to link a connection to"
        String url = LINK_EDC_REFERENCE_ENDPOINT
        String content = readJsonForController("linkConnection-request")

        when: "Linking the connection to the study"
        mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(content))
                .andExpect(status().is2xxSuccessful())

        then: "The connection is successfully passed to the usecase"
        def captor = ArgumentCaptor.forClass(LinkConnection.Request)
        verify(linkConnection).link(captor.capture())
        with(captor.value) {
            studyId.id == "SID_001"
            type == StudyConnectionType.READ_SUBJECTS
            edcSystem == RAVE
            externalSiteId.id == "123456"
            clinicalDataURI == new URI("http:/localhost:8081/RaveWebServices/studies/raveStudyId/Subjects")
            username == "user1"
            password == "user1Pass"
            !enabled
        }
    }

    def "Assign a connection without EDCSystem to a study (LEGACY)"() {
        given: "A known study to link a connection to"
        String url = LINK_EDC_REFERENCE_ENDPOINT
        String content = readJsonForController("linkConnectionWithoutEDCSystem-request")

        when: "Linking the connection without EDCSystem to the study"
        mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(content))
                .andExpect(status().is2xxSuccessful())

        then: "The connection is successfully passed to the usecase"
        def captor = ArgumentCaptor.forClass(LinkConnection.Request)
        verify(linkConnection).link(captor.capture())
        with(captor.value) {
            studyId.id == "SID_001"
            type == StudyConnectionType.READ_SUBJECTS
            edcSystem == RAVE
            externalSiteId.id == "123456"
            clinicalDataURI == new URI("http:/localhost:8081/RaveWebServices/studies/raveStudyId/Subjects")
            username == "user1"
            password == "user1Pass"
            !enabled
        }
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot assign a site reference to a study"() {
        when: "Assigning the reference to the study"
        ResultActions resultActions = mockMvc.perform(post(LINK_EDC_REFERENCE_ENDPOINT)
                .with(nonLocalhost())
                .contentType(APPLICATION_JSON)
                .content(readJsonForController("linkReadSubjectsEDC-request")))

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    @WithAnonymousUser
    def "An unauthenticated user can assign a site reference to a study from localhost"() {
        when: "Assigning the reference to the study"
        ResultActions resultActions = mockMvc.perform(post(LINK_EDC_REFERENCE_ENDPOINT)
                .with(remoteAddr)
                .contentType(APPLICATION_JSON)
                .content(readJsonForController("linkReadSubjectsEDC-request")))

        then: "Access is granted"
        resultActions.andExpect(status().isOk())

        where:
        remoteAddr << [localhostIPv4(), localhostIPv6()]
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot assign a connection to a study"() {
        when: "Assigning the reference to the study"
        ResultActions resultActions = mockMvc.perform(post(LINK_EDC_REFERENCE_ENDPOINT)
                .with(nonLocalhost())
                .contentType(APPLICATION_JSON)
                .content(readJsonForController("linkConnection-request")))

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    @WithAnonymousUser
    def "An unauthenticated user can assign a connection to a study from localhost"() {
        when: "Assigning the reference to the study"
        ResultActions resultActions = mockMvc.perform(post(LINK_EDC_REFERENCE_ENDPOINT)
                .with(remoteAddr)
                .contentType(APPLICATION_JSON)
                .content(readJsonForController("linkConnection-request")))

        then: "Access is granted"
        resultActions.andExpect(status().isOk())

        where:
        remoteAddr << [localhostIPv4(), localhostIPv6()]
    }

    def "Assign a subject creation reference to a study"() {
        given: "A known study to link a subject creation reference to"
        String url = LINK_EDC_REFERENCE_ENDPOINT
        String content = readJsonForController("linkCreateSubjectEDC-request")

        when: "Assigning the reference to the study"
        mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(content))
                .andExpect(status().is2xxSuccessful())

        then: "The reference is successfully passed to the usecase"
        def captor = ArgumentCaptor.forClass(LinkConnection.Request)
        verify(linkConnection).link(captor.capture())
        with(captor.value) {
            studyId.id == "56021927PCR3011"
            edcSystem == RAVE
            externalSiteId.id == "123456"
            clinicalDataURI == new URI("http:/localhost:8081/RaveWebServices/studies/raveStudyId/Subjects")
            type == StudyConnectionType.WRITE_SUBJECT
            username == "user1"
            password == "user1Pass"
            enabled
        }
    }

    def "Assign a submit event reference to a study"() {
        given: "A known study to link a submit event reference to"
        String url = LINK_EDC_REFERENCE_ENDPOINT
        String content = readJsonForController("linkSubmitEventEDC-request")

        when: "Assigning the reference to the study"
        mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON)
                .content(content))
                .andExpect(status().is2xxSuccessful())

        then: "The reference is successfully passed to the usecase"
        def captor = ArgumentCaptor.forClass(LinkConnection.Request)
        verify(linkConnection).link(captor.capture())
        with(captor.value) {
            studyId.id == "56021927PCR3011"
            edcSystem == RAVE
            externalSiteId.id == "123456"
            clinicalDataURI == new URI("http:/localhost:8081/RaveWebServices/studies/raveStudyId/Subjects")
            type == StudyConnectionType.SUBMIT_EVENT
            username == "user1"
            password == "user1Pass"
            enabled
        }
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot assign a subject creation reference to a study"() {
        when: "Assigning the reference to the study"
        ResultActions resultActions = mockMvc.perform(post(LINK_EDC_REFERENCE_ENDPOINT)
                .with(nonLocalhost())
                .contentType(APPLICATION_JSON)
                .content(readJsonForController("linkCreateSubjectEDC-request")))

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    @WithAnonymousUser
    def "An unauthenticated user can assign a subject creation reference to a study from localhost"() {
        when: "Assigning the reference to the study"
        ResultActions resultActions = mockMvc.perform(post(LINK_EDC_REFERENCE_ENDPOINT)
                .with(remoteAddr)
                .contentType(APPLICATION_JSON)
                .content(readJsonForController("linkCreateSubjectEDC-request")))

        then: "Access is unauthorized"
        resultActions.andExpect(status().isOk())

        where:
        remoteAddr << [localhostIPv4(), localhostIPv6()]
    }

    def "List available subject ids for a study"() {
        given: "A study id with edc information available"
        String url = RETRIEVE_SUBJECTS_FROM_EDC_ENDPOINT
        String content = readJsonForController("availableSubjectIdsForStudy-response")
        given(listAvailableSubjectIds.list(any())).willReturn(availableSubjectIdsForStudy())

        when: "Listing the available subject ids for the study and a site reference"
        def response = mockMvc.perform(get(url, "SID_001")).andReturn().response

        then: "the response lists all known subjects ids in the study for the site reference"
        response.status == HttpStatus.OK.value()
        JSONAssert.assertEquals(response.contentAsString, content, JSONCompareMode.STRICT)
    }

    def "List available subject ids for a study without EDC"() {
        given: "A study id with edc information available"
        String url = RETRIEVE_SUBJECTS_FROM_EDC_ENDPOINT
        String content = readJsonForController("availableSubjectIdsForStudyWithoutEDC-response")
        given(listAvailableSubjectIds.list(any())).willReturn(availableSubjectIdsForStudyWithoutEDC())

        when: "Listing the available subject ids for the study and a site reference"
        def response = mockMvc.perform(get(url, "SID_001")).andReturn().response

        then: "the response lists all known subjects ids in the study for the site reference"
        response.status == HttpStatus.NOT_FOUND.value()
        JSONAssert.assertEquals(response.contentAsString, content, JSONCompareMode.STRICT)
    }

    @WithAnonymousUser
    def "An unauthenticated user cannot get the list of available subject ids for a study"() {
        when: "Listing the available subject ids for the study and a site reference"
        ResultActions resultActions = mockMvc.perform(get(RETRIEVE_SUBJECTS_FROM_EDC_ENDPOINT, "SID_001"))

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    static def availableSubjectIdsForStudyWithoutEDC() {
        return ListAvailableSubjectIds.Response.newBuilder()
                .withFromEDC(false)
                .withSubjectIds(Lists.emptyList())
                .build()
    }

    static def availableSubjectIdsForStudy() {
        return ListAvailableSubjectIds.Response.newBuilder()
                .withFromEDC(true)
                .withSubjectIds(subjectIds("SUBJ_001", "SUBJ_002", "SUBJ_003"))
                .build()
    }

    static String readJsonForController(String path) {
        def sampleResource = new ClassPathResource("samples/edcstudycontroller/"+path+".json")
        return new String(FileCopyUtils.copyToByteArray(sampleResource.inputStream), UTF_8)
    }

    static def subjectIds(String... subjectIds) {
        List<EDCSubjectReference> list = new ArrayList<>()
        for (int i = 0; i < subjectIds.size(); i++) {
            list.add(EDCSubjectReference.of(subjectIds[i]))
        }
        return list
    }
}
