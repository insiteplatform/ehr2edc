package com.custodix.insite.local.ehr2edc.rest.study

import com.custodix.insite.local.ehr2edc.command.AssignInvestigator
import com.custodix.insite.local.ehr2edc.command.UnassignInvestigator
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import org.mockito.ArgumentCaptor
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.util.FileCopyUtils
import spock.lang.Unroll

import static org.mockito.Mockito.verify
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(classes = InvestigatorController)
class InvestigatorControllerSpec extends ControllerSpec {

    private static final String INVESTIGATOR_ASSIGN = "/ehr2edc/studies/{studyId}/investigators"
    private static final String INVESTIGATOR_UNASSIGN = INVESTIGATOR_ASSIGN + "/{investigatorId}"

    @MockBean
    AssignInvestigator assignInvestigators
    @MockBean
    UnassignInvestigator unassignInvestigators

    def "assign with a valid investigator succeeds"() {
        given: "A study id"
        def studyId = StudyId.of("STUDY-1")
        and: "A potential investigator"
        def investigator = readJsonForController("addInvestigators-single-investigator")

        when: "The investigator gets added to the study"
        def request = put(INVESTIGATOR_ASSIGN, studyId.id)
                .content(investigator)
                .contentType(APPLICATION_JSON)
        def response = mockMvc.perform(request).andReturn().response

        then: "The response indicates that the operation succeeded"
        response.status == HttpStatus.OK.value()
        and: "The correct investigator was added to the correct study"
        def captor = ArgumentCaptor.forClass(AssignInvestigator.Request)
        verify(assignInvestigators).assign(captor.capture())
        with(captor) {
            value.studyId == studyId
            value.investigatorId.id == "2"
        }
    }

    @WithAnonymousUser
    def "assign with a valid investigator fails for an unauthenticated user"() {
        when: "an investigator gets added to the study"
        def request = put(INVESTIGATOR_ASSIGN, "STUDY-1")
                .content(readJsonForController("addInvestigators-single-investigator"))
                .contentType(APPLICATION_JSON)
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    @Unroll
    def "Unassign investigator from study"(String studyId, String investigatorId) {
        when: "unassigning the given investigator"
        def request = delete(INVESTIGATOR_UNASSIGN, studyId, investigatorId)
        def response = mockMvc.perform(request).andReturn().response

        then: "The response indicates that the operation succeeded"
        response.status == HttpStatus.OK.value()
        and: "the unassignment is correctly executed"
        def captor = ArgumentCaptor.forClass(UnassignInvestigator.Request)
        verify(unassignInvestigators).unassign(captor.capture())
        with(captor) {
            value.studyId.id == studyId
            value.investigatorId == UserIdentifier.of(investigatorId)
        }

        where:
        studyId         | investigatorId
        "STUDY-1"       | "INVESTIGATOR-1"
    }

    @WithAnonymousUser
    def "unassign with a valid investigator fails for an unauthenticated user"() {
        when: "The investigator is unassigned from the study"
        def request = delete(INVESTIGATOR_UNASSIGN, "STUDY-1", "INVESTIGATOR-1")
        ResultActions resultActions = mockMvc.perform(request)

        then: "Access is unauthorized"
        resultActions.andExpect(status().isUnauthorized())
    }

    static def readJsonForController(String path) {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("samples/studyinvestigatorscontroller/"+path+".json").getFile()))
    }
}
