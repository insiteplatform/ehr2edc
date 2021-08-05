package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.query.ListAllStudies
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title

@Title("List All Studies")
class ListAllStudiesSpec extends AbstractSpecification {

    @Autowired
    ListAllStudies listAllStudies

    def "Lists known studies as a DRM"() {
        given: "A known study without investigators"
        def knownStudy = generateKnownStudyWithoutInvestigators()
        and: "Current user is a DRM"
        withCurrentUserDRM()

        when: "All studies are retrieved"
        def result = listAllStudies.allStudies()

        then: "The known study should be included in the result"
        result.getStudies() != null
        with(result.getStudies().find { it.id == knownStudy.studyId.getId() }) {
            name == knownStudy.name
            description == knownStudy.description
        }
    }

    def "Lists known studies as a assigned investigator"() {
        given: "A known user without DRM privileges"
        withCurrentUserHavingId(USER_ID_KNOWN)
        withCurrentUserNoDRM()
        and: "A assigned study"
        def assignedTo = generateKnownStudy(USER_ID_KNOWN)
        and: "Another study"
        def other = generateKnownStudyWithoutInvestigators()

        when: "All studies are retrieved"
        def result = listAllStudies.allStudies()

        then: "The assigned study should be included in the result"
        result.getStudies() != null
        result.getStudies().find { it.id == assignedTo.studyId.id }
        and: "The other study is not included"
        !result.getStudies().find { it.id == other.studyId.id }
    }

    def "Lists known studies with a user without assigned studies"() {
        given: "A known study in the repository"
        generateKnownStudy(USER_ID_OTHER)

        when: "All studies are retrieved"
        ListAllStudies.Response result = listAllStudies.allStudies()

        then: "An empty list is returned"
        result.studies == []
    }

    def "Lists known studies as a non authenticated user"() {
        given: "A known study in the repository"
        withoutAuthenticatedUser()
        and: "An unknown user"
        withoutAuthenticatedUser()

        when: "All studies are retrieved"
        ListAllStudies.Response result = listAllStudies.allStudies()

        then: "Access is denied"
        thrown AccessDeniedException
    }
}
