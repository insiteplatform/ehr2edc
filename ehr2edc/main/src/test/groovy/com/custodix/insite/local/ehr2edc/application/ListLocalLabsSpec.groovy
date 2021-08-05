package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.LabAnalyteRangesGateway
import com.custodix.insite.local.ehr2edc.query.ListLocalLabs
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection
import com.custodix.insite.local.ehr2edc.vocabulary.LabName
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId
import static java.util.Collections.emptyList
import static java.util.Collections.singletonList

@Title("List Available Local Labs in a Study")
class ListLocalLabsSpec  extends AbstractSpecification {
    @Autowired
    ListLocalLabs listLocalLabs

    @SpringBean
    private LabAnalyteRangesGateway labAnalyteRangesGateway = Mock()

    def "List available local labs for an unknown study"() {
        given: "A request without a known study id"
        def studyId = aRandomStudyId()
        def request = ListLocalLabs.Request.newBuilder()
                .withStudyId(studyId)
                .build()

        when: "Listing the available local labs in the Study"
        listLocalLabs.list(request)

        then: "Indicate the user has no permission to perform the request"
        AccessDeniedException ex = thrown()
        ex.message == "User is not an assigned Investigator"
    }

    def "List available local labs for a study the current user is not assigned to"() {
        given: "A request for a study the current user is not assigned to as investigator"
        def studyId = generateKnownStudyId(USER_ID_OTHER)
        def request = ListLocalLabs.Request.newBuilder()
                .withStudyId(studyId)
                .build()

        when: "Listing the available local labs in the Study"
        listLocalLabs.list(request)

        then: "Indicate the user has no permission to perform the request"
        AccessDeniedException ex = thrown()
        ex.message == "User is not an assigned Investigator"
    }

    def "List available local labs for an unauthenticated user should fail"() {
        given: "A request for a study the current user is not assigned to as investigator"
        def studyId = generateKnownStudyId(USER_ID_KNOWN)
        def request = ListLocalLabs.Request.newBuilder()
                .withStudyId(studyId)
                .build()
        and: "No user is authenticated"
        withoutAuthenticatedUser()

        when: "Listing the available local labs in the Study"
        listLocalLabs.list(request)

        then: "Indicate the user has no permission to perform the request"
        AccessDeniedException ex = thrown()
        ex.message == "User is not an assigned Investigator"
    }

    @Unroll
    def "List available local labs for a Study without study id should fail"(
            StudyId studyId) {
        given: "A request with an #typeOfError"
        def request = ListLocalLabs.Request.newBuilder()
                .withStudyId(studyId)
                .build()
        when: "Listing the available local labs in the Study"
        listLocalLabs.list(request)

        then: "Access is denied"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"

        where:
        studyId          | _
        null             | _
        StudyId.of(null) | _
    }

    def "List available local labs for a study without EDC connection"() {
        given: "A known study with the current user as assigned investigator"
        def studyId = aRandomStudyId()
        generateKnownStudy(studyId, "Test Study " + studyId.id, "Test Study " + studyId.id + " description", USER_ID_KNOWN)
        labAnalyteRangesGateway.findActiveLabs(_) >> emptyList()
        def request = ListLocalLabs.Request.newBuilder()
                .withStudyId(studyId)
                .build()

        when: "Listing the available local labs in the Study"
        def response = listLocalLabs.list(request)

        then: "No local labs are available"
        response.getLocalLabs().empty
    }

    def "The gateway is not called when there is no EDC connection for the study"() {
        given: "A known study with the current user as assigned investigator"
        def studyId = generateKnownStudyWithoutEDCConnection(USER_ID_KNOWN).studyId
        def request = ListLocalLabs.Request.newBuilder()
                .withStudyId(studyId)
                .build()

        when: "Listing the available local labs in the Study"
        listLocalLabs.list(request)

        then: "the gateway is not called"
        0 * labAnalyteRangesGateway.findActiveLabs(_)
    }

    def "List available local labs for a study with a malfunctioning lab analyte ranges gateway"() {
        given: "A known study with the current user as assigned investigator"
        def studyId = aRandomStudyId()
        generateKnownStudy(studyId, "Test Study " + studyId.id, "Test Study " + studyId.id + " description", USER_ID_KNOWN)
        def request = ListLocalLabs.Request.newBuilder()
                .withStudyId(studyId)
                .build()
        and: "a malfunctioning lab analytes ranges gateway"
        labAnalyteRangesGateway.findActiveLabs(_) >> { throw new IOException("simulated failure")}

        when: "Listing the available local labs in the Study"
        listLocalLabs.list(request)

        then: "A system exception is thrown"
        thrown(SystemException)
    }

    def "Listing available local labs for a study shall pass the study id to the lab analyte ranges gateway"() {
        given: "A known study with the current user as assigned investigator"
        def studyId = aRandomStudyId()
        def study = generateKnownStudyWithoutEDCConnection(studyId, "Test Study " + studyId.id, "Test Study " + studyId.id + " description", USER_ID_KNOWN)
        def request = ListLocalLabs.Request.newBuilder()
                .withStudyId(studyId)
                .build()
        and: "an EDC connection to allow retrieving lab analyte ranges"
        def connection = addReadLabAnalyteRangesConnection(study)
        when: "Listing the available local labs in the Study"
        listLocalLabs.list(request)

        then: "the study id is passed to the gateway"
        1 * labAnalyteRangesGateway.findActiveLabs({
            it.studyId.id == connection.id.studyId
            it.connectionType == connection.id.connectionType
        }) >> emptyList()
    }

    def "List available local labs for a study with an EDC connection"() {
        given: "A known study with the current user as assigned investigator"
        def studyId = aRandomStudyId()
        generateKnownStudy(studyId, "Test Study " + studyId.id, "Test Study " + studyId.id + " description", USER_ID_KNOWN)
        def request = ListLocalLabs.Request.newBuilder()
                .withStudyId(studyId)
                .build()
        and: "an active local lab for the study"
        def activeLocalLabs = singletonList(LabName.newBuilder().withName("local_lab").build())
        labAnalyteRangesGateway.findActiveLabs(_ as ExternalEDCConnection) >> activeLocalLabs

        when: "Listing the available local labs in the Study"
        def response = listLocalLabs.list(request)

        then: "the local lab is returned"
        response.getLocalLabs() == activeLocalLabs
    }

}
