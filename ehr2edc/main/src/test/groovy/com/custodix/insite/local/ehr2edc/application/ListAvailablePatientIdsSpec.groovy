package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.domain.service.PatientEHRGateway
import com.custodix.insite.local.ehr2edc.query.ListAvailablePatientIds
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId
import static org.mockito.BDDMockito.given

@Title("List Available PatientIds for a Study")
class ListAvailablePatientIdsSpec extends AbstractSpecification {

    private static String EMPTY_FILTER = null
    private static Long NO_LIMIT = null

    private static int ARG_DEFAULT_LIMIT = 100

    @MockBean
    private PatientEHRGateway patientEHRGateway

    @Autowired
    ListAvailablePatientIds listAvailablePatientIds

    @Unroll
    def "List available patientIds for a Study with invalid StudyId"(StudyId studyId, String patientDomain, String inputErrorDescription) {
        given: "A request with a #inputErrorDescription"
        def request = aRequestWithDomainAndStudyId(patientDomain, studyId)

        when: "Listing the available patientIds for the Study"
        listAvailablePatientIds.list(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"

        where:
        studyId          | patientDomain | inputErrorDescription
        null             | "domain"      | "null study id"
        StudyId.of(null) | "domain"      | "empty study id"
    }

    @Unroll
    def "List available patientIds for a Study with invalid parameters"(String patientDomain, String inputErrorDescription, String expectedMessage) {
        given: "A request for a known study with a #inputErrorDescription"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        def request = aRequestWithDomainAndStudyId(patientDomain, studyId)

        when: "Listing the available patientIds for the Study"
        listAvailablePatientIds.list(request)

        then: "An error containing '#expectedMessage' should appear"
        Exception ex = thrown()
        ex.message.contains(expectedMessage)

        where:
        patientDomain | inputErrorDescription      | expectedMessage
        null          | "null patient domain"      | "must not be null"
        ""            | "too short patient domain" | "size must be between 1 and 50"
    }

    def "List available patientIds for an unknown study"() {
        given: "A request with an unknown study id"
        StudyId studyId = aRandomStudyId()
        def request = aRequestWithDomainAndStudyId("domain", studyId)

        when: "Listing the available patientIds in the Study"
        listAvailablePatientIds.list(request)

        then: "Indicate the user has no permission to perform the request"
        AccessDeniedException ex = thrown()
        ex.message == "User is not an assigned Investigator"
    }

    def "List available patientIds without being an assigned investigator should fail"() {
        given: "A study without investigators"
        StudySnapshot study = generateKnownStudy(USER_ID_OTHER)
        and: "A request for available patientIds"
        def request = aRequestWithDomainAndStudyId("domain", study.studyId)

        when: "Listing the available patientIds for the study"
        listAvailablePatientIds.list(request)

        then: "Indicate the user has no permission to perform the request"
        AccessDeniedException ex = thrown()
        ex.message == "User is not an assigned Investigator"
    }

    def "List available patientIds for unauthenticated user should fail"() {
        given: "A request for a known study"
        def aStudyId = generateKnownStudy(USER_ID_KNOWN).studyId
        def aPatientDomain = "domain"
        def request = aRequestWithDomainAndStudyId(aPatientDomain, aStudyId)
        and: "No user is authenticated"
        withoutAuthenticatedUser()

        when: "Listing the available patientIds for the study"
        listAvailablePatientIds.list(request)

        then: "Indicate the user has no permission to perform the request"
        AccessDeniedException ex = thrown()
        ex.message == "User is not an assigned Investigator"
    }

    def "List available patientIds within a domain for a known study without subjects"() {
        given: "A request for a known study"
        def aStudyId = generateKnownStudy(USER_ID_KNOWN).studyId
        def aPatientDomain = "domain"
        def request = aRequestWithDomainAndStudyId(aPatientDomain, aStudyId)
        and: "Available patientIds for the the domain"
        given(patientEHRGateway.getFiltered(aStudyId, aPatientDomain, EMPTY_FILTER, ARG_DEFAULT_LIMIT))
                .willReturn(somePatientsIn(aPatientDomain))

        when: "Listing the available patientIds for the study"
        def response = listAvailablePatientIds.list(request)

        then: "All the patients are returned"
        def expected = "12341".."12345"
        response.patientIds
        response.patientIds.size() == expected.size()
        response.patientIds.containsAll(expected)
    }

    def "List available patientIds within a domain for a known study with subjects"() {
        given: "A request for a known study"
        def aStudy = generateKnownStudy(USER_ID_KNOWN)
        def aStudyId = aStudy.studyId
        def aPatientDomain = "domain"
        def request = aRequestWithDomainAndStudyId(aPatientDomain, aStudyId)
        and: "Available patientIds for the the domain"
        given(patientEHRGateway.getFiltered(aStudyId, aPatientDomain, EMPTY_FILTER, ARG_DEFAULT_LIMIT))
                .willReturn(somePatientsIn(aPatientDomain))
        and: "Some patients are already assigned to the study"
        someSubjectsIn(aStudy, aPatientDomain)

        when: "Listing the available patientIds for the study"
        def response = listAvailablePatientIds.list(request)

        then: "All the patients are returned"
        response.patientIds
        response.patientIds == ["12342", "12344"]
    }

    def "List filtered patientIds within a domain for a known study"() {
        given: "A request for a known study"
        def aStudyId = generateKnownStudy(USER_ID_KNOWN).studyId
        def aPatientDomain = "domain"
        def aFilter = "123"
        def request = aRequestWithDomainStudyIdAndFilter(aPatientDomain, aStudyId, aFilter)
        and: "Available patientIds for the the domain"
        given(patientEHRGateway.getFiltered(aStudyId, aPatientDomain, aFilter, ARG_DEFAULT_LIMIT))
                .willReturn(somePatientsIn(aPatientDomain))

        when: "Listing the available patientIds for the study"
        def response = listAvailablePatientIds.list(request)

        then: "All the patients are returned"
        def expected = "12341".."12345"
        response.patientIds
        response.patientIds.size() == expected.size()
        response.patientIds.containsAll(expected)
    }

    def "PatientIds are lexicographically ordered"() {
        given: "A request for a known study"
        def aStudyId = generateKnownStudy(USER_ID_KNOWN).studyId
        def aPatientDomain = "domain"
        def aFilter = "123"
        def request = aRequestWithDomainStudyIdAndFilter(aPatientDomain, aStudyId, aFilter)
        and: "Available patientIds for the the domain"
        given(patientEHRGateway.getFiltered(aStudyId, aPatientDomain, aFilter, ARG_DEFAULT_LIMIT))
                .willReturn(somePatientsIn(aPatientDomain))

        when: "Listing the available patientIds for the study"
        def response = listAvailablePatientIds.list(request)

        then: "The patientIds are returned in order"
        def expected = "12341".."12345"
        response.patientIds
        response.patientIds == expected
    }

    @Unroll
    def "Listing #limit patientIds returns #expectedResults results for a study with 1.000 available patientIds"(Integer limit, Long expectedResults) {
        given: "A request for #limit available patientId(s) for a known study"
        def aStudyId = generateKnownStudy(USER_ID_KNOWN).studyId
        def aPatientDomain = "domain"
        def request = aRequestWithDomainStudyIdAndLimit(aPatientDomain, aStudyId, limit)
        and: "1.000 patientIds for the domain"
        given(patientEHRGateway.getFiltered(aStudyId, aPatientDomain, EMPTY_FILTER, limit == null? 100 : limit))
                .willReturn(aThousandPatientsIn(aPatientDomain))

        when: "Listing the available patientIds for the study"
        def response = listAvailablePatientIds.list(request)

        then: "#expectedResults patientId is returned"
        response.patientIds.size() == expectedResults.intValue()

        where:
        limit || expectedResults
        1     || 1
        100   || 100
        10000 || 1000
        null  || 100
    }

    def aRequestWithDomainAndStudyId(String domain, StudyId studyId) {
        return aRequest(domain, studyId, EMPTY_FILTER, NO_LIMIT)
    }

    def aRequestWithDomainStudyIdAndFilter(String domain, StudyId studyId, String filter) {
        return aRequest(domain, studyId, filter, NO_LIMIT)
    }

    def aRequestWithDomainStudyIdAndLimit(String domain, StudyId studyId, Long limit) {
        return aRequest(domain, studyId, EMPTY_FILTER, limit)
    }

    def aRequest(String domain, StudyId studyId, String filter, Long limit) {
        return ListAvailablePatientIds.Request.newBuilder()
                .withPatientDomain(domain)
                .withStudyId(studyId)
                .withFilter(filter)
                .withLimit(limit)
                .build()
    }

    def someSubjectsIn(StudySnapshot study, String domain) {
        Set<PatientCDWReference> references = ["12341", "12343", "12345"].collect {
            PatientCDWReference.newBuilder()
                    .withSource(domain)
                    .withId(it)
                    .build()
        }

        references.each {
            addRegisteredSubjectToStudy(study, it, EDCSubjectReference.of(it.id), SubjectId.of(it.id))
        }
    }

    def somePatientsIn(String domain) {
        return ("12345".."12341").collect {
            PatientCDWReference.newBuilder()
                    .withId(it)
                    .withSource(domain)
                    .build()
        }.toSet()
    }

    def aThousandPatientsIn(String domain) {
        return (1..1000).collect {
            PatientCDWReference.newBuilder()
                    .withId("" + it)
                    .withSource(domain)
                    .build()
        }.toSet()
    }
}
