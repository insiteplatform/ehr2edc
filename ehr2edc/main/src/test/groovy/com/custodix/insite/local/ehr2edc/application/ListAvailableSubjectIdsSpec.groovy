package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.EDCStudyGateway
import com.custodix.insite.local.ehr2edc.query.ListAvailableSubjectIds
import com.custodix.insite.local.ehr2edc.query.ListAvailableSubjectIds.Request
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

@Title("List Available Subject Ids in a Study")
class ListAvailableSubjectIdsSpec extends AbstractSpecification {

    @Autowired
    ListAvailableSubjectIds listAvailableSubjectIds

    @MockBean
    private EDCStudyGateway edcStudyGateway

    @Unroll
    def "List available subject ids without study id is not allowed"() {
        given: "A request with an #typeOfError"
        def request = Request.newBuilder()
                .withStudyId(studyId)
                .build()
        when: "Listing the available subject ids in the Study"
        listAvailableSubjectIds.list(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"

        where:
        studyId          | _
        null             | _
        StudyId.of(null) | _
    }

    def "List available subject ids for an unknown study"() {
        given: "A request without a known study id"
        StudyId studyId = aRandomStudyId()
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .build()

        when: "Listing the available subject ids in the Study"
        listAvailableSubjectIds.list(request)

        then: "Indicate the user has no permission to perform the request"
        AccessDeniedException ex = thrown()
        ex.message == "User is not an assigned Investigator"
    }

    @Unroll
    def "List available subject ids for a study #fromEDC"(
            StudyId studyId, ExternalEDCConnection externalEDCConnection,
            List<EDCSubjectReference> edcSubjectReferences, List<EDCSubjectReference> edcSubjectIds, List<EDCSubjectReference> availableEdcSubjectReferences, boolean fromEDC) {
        given: "A known study with id `#studyId.id`"
        and: "has the following already locally registered edcSubjectReferences `#edcSubjectReferences.id`"
        def subjects = createSubjectSnapshots(edcSubjectReferences)
        generateKnownStudyWithoutEDCConnection(studyId, "Test Study " + studyId.id, "Test Study " + studyId.id + " description", subjects, USER_ID_KNOWN)
        if (externalEDCConnection != null) {
            and: "registered site reference `#externalEDCConnection.externalSiteId.id`"
            saveInStudyConnectionSnapshotRepository(externalEDCConnection)
        }
        and: "the EDC has following available edcSubjectReferences `#edcSubjectIds.id`"
        withAvailableSubjectIds(studyId, edcSubjectIds, fromEDC)

        when: "The available subject ids for the study with id `#studyId.id` are retrieved"
        def response = listAvailableSubjectIds.list(Request.newBuilder().withStudyId(studyId).build())

        then: "The response should contain all available edcSubjectReferences which have not been registered `#availableEdcSubjectReferences.id`"
        response.subjectIds.size() == availableEdcSubjectReferences.size()
        response.subjectIds.containsAll(availableEdcSubjectReferences)
        if (externalEDCConnection != null) {
            verify(edcStudyGateway).findRegisteredSubjectIds(eq(studyId))
        }
        and: "The response should indicate whether an EDCConnection was used for retrieving the subjects"
        response.fromEDC == fromEDC

        where:
        studyId          | externalEDCConnection                                                        | edcSubjectReferences                               | edcSubjectIds                                                  || availableEdcSubjectReferences          || fromEDC
        aRandomStudyId() | null                                                                         | createEdcSubjectReferences("SUBJ_001")             | Collections.emptyList()                                        || Collections.emptyList()                || false
        aRandomStudyId() | generateEDCConnectionData(studyId, StudyConnectionType.READ_SUBJECTS)        | Collections.emptyList()                            | Collections.emptyList()                                        || Collections.emptyList()                || true
        aRandomStudyId() | generateEDCConnectionData(studyId, StudyConnectionType.READ_SUBJECTS)        | Collections.emptyList()                            | createEdcSubjectReferences("SUBJ_001")                         || createEdcSubjectReferences("SUBJ_001") || true
        aRandomStudyId() | generateEDCConnectionData(studyId, StudyConnectionType.READ_SUBJECTS)        | createEdcSubjectReferences("SUBJ_001")             | createEdcSubjectReferences("SUBJ_001")                         || Collections.emptyList()                || true
        aRandomStudyId() | generateEDCConnectionData(studyId, StudyConnectionType.READ_SUBJECTS)        | createEdcSubjectReferences("SUBJ_001", "SUBJ_002") | createEdcSubjectReferences("SUBJ_001", "SUBJ_002", "SUBJ_003") || createEdcSubjectReferences("SUBJ_003") || true
        aRandomStudyId() | generateEDCConnectionData(studyId, StudyConnectionType.READ_SUBJECTS, false) | Collections.emptyList()                            | Collections.emptyList()                                        || Collections.emptyList()                || false
        aRandomStudyId() | generateEDCConnectionData(studyId, StudyConnectionType.READ_SUBJECTS, false) | Collections.emptyList()                            | Collections.emptyList()                                        || Collections.emptyList()                || false
        aRandomStudyId() | generateEDCConnectionData(studyId, StudyConnectionType.READ_SUBJECTS, false) | createEdcSubjectReferences("SUBJ_001")             | Collections.emptyList()                                        || Collections.emptyList()                || false
        aRandomStudyId() | generateEDCConnectionData(studyId, StudyConnectionType.READ_SUBJECTS, false) | createEdcSubjectReferences("SUBJ_001", "SUBJ_002") | Collections.emptyList()                                        || Collections.emptyList()                || false
    }

    def "List available subjects after it has been deregistered from the study before"() {
        given: "A known study and subject"
        def study = generateKnownStudy(USER_ID_KNOWN)
        def subject = generateKnownSubject(study.studyId)
        and: "The subject was de-registered from the study"
        generateEDCConnectionData(study.studyId, StudyConnectionType.READ_SUBJECTS)
        addReadSubjectConnection(study)
        given(edcStudyGateway.findRegisteredSubjectIds(eq(study.studyId)))
                .willReturn(EDCStudyGateway.RegisteredSubjects.fromEdc(createEdcSubjectReferences(subject.subjectId.id)))
        addDeregisteredSubjectToStudy(study, subject.patientCDWReference, subject.edcSubjectReference, subject.subjectId)

        when: "The available subject ids for the study with id `#study.studyId.id` are retrieved"
        def response = listAvailableSubjectIds.list(Request.newBuilder().withStudyId(study.studyId).build())

        then: "The subject that was deregistered before is present in the result"
        response.subjectIds.stream().anyMatch({ subjectId -> subjectId.id == subject.subjectId.id })
    }

    def "List available subjects as an unauthenticated user should fail"() {
        withoutAuthenticatedUser()
        given: "A known study and subject"
        def study = generateKnownStudy(USER_ID_KNOWN)

        when: "The available subject ids for the study with id `#study.studyId.id` are retrieved"
        def response = listAvailableSubjectIds.list(Request.newBuilder().withStudyId(study.studyId).build())

        then: "Access is denied"
        thrown(AccessDeniedException)
    }

    def "List available subjects as a user who is not an investigator should fail"() {
        given: "A known study and subject"
        def study = generateKnownStudy(USER_ID_OTHER)

        when: "The available subject ids for the study with id `#study.studyId.id` are retrieved"
        def response = listAvailableSubjectIds.list(Request.newBuilder().withStudyId(study.studyId).build())

        then: "Access is denied"
        thrown(AccessDeniedException)
    }

    def createEdcSubjectReferences(String... edcSubjectReferences) {
        List<EDCSubjectReference> list = new ArrayList<>()
        for (int i = 0; i < edcSubjectReferences.size(); i++) {
            list.add(EDCSubjectReference.of(edcSubjectReferences[i]))
        }
        return list
    }

    def withAvailableSubjectIds(StudyId studyId, List<EDCSubjectReference> availableSubjectIds, boolean fromEDC) {
        when(edcStudyGateway.findRegisteredSubjectIds(eq(studyId)))
                .thenReturn(fromEDC ?
                        EDCStudyGateway.RegisteredSubjects.fromEdc(availableSubjectIds)
                        : EDCStudyGateway.RegisteredSubjects.noEdc())
    }

}
