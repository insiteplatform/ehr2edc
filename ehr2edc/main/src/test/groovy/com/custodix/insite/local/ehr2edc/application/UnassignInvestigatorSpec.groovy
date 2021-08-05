package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.command.UnassignInvestigator
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title

import static com.custodix.insite.local.ehr2edc.command.UnassignInvestigator.Request
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId

@Title("Assign Investigators")
class UnassignInvestigatorSpec extends AbstractSpecification {

    @Autowired
    UnassignInvestigator unAssignInvestigators

    def "Unassign Investigators with an empty Request"() {
        given: "An empty request"
        Request request = Request.newBuilder().build()
        and: "Current user is a DRM"
        withCurrentUserDRM()

        when: "Unassign users as Investigator"
        unAssignInvestigators.unassign(request)

        then: "Indicate parameters are missing"
        thrown UseCaseConstraintViolationException
    }

    def "Unassign Investigators without any Investigators"() {
        given: "An request without any Investigators"
        Request request = Request.newBuilder().withStudyId(StudyId.of("STUDY_B")).build()
        and: "Current user is a DRM"
        withCurrentUserDRM()

        when: "Unassign users as Investigator"
        unAssignInvestigators.unassign(request)

        then: "Indicate parameters are missing"
        thrown UseCaseConstraintViolationException
    }

    def "Unassign Investigators without a Study"() {
        given: "An request without a StudyId"
        Request request = Request.newBuilder().withInvestigatorId(userIdentifier("1")).build()
        and: "Current user is a DRM"
        withCurrentUserDRM()

        when: "Unassign users as Investigator"
        unAssignInvestigators.unassign(request)

        then: "Indicate parameters are missing"
        thrown UseCaseConstraintViolationException
    }

    def "Unassign Investigators from an unknown Study"() {
        given: "A StudyId of an unknown study and some Investigators"
        StudyId studyId = aRandomStudyId()
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withInvestigatorId(userIdentifier("1")).build()
        and: "Current user is a DRM"
        withCurrentUserDRM()

        when: "Unassign users as Investigator"
        unAssignInvestigators.unassign(request)

        then: "Indicate parameters are missing"
        UserException ex = thrown()
        ex.message == DomainException.Type.getMessage(DomainException.Type.NOT_EXISTS, studyId)
    }


    def "Unassign Investigators from a known Study"() {
        given: "A StudyId of an existing study and some Investigators"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withInvestigatorId(userIdentifier("1"))
                .build()
        and: "Current user is a DRM"
        withCurrentUserDRM()

        when: "Assigning users as Investigator"
        unAssignInvestigators.unassign(request)

        then: "Indicate parameters are missing"
        studyRepository.getStudyById(studyId).investigators.getAssigned().size() == 1
    }

    def "Unassign Investigators from a Study with Investigators"() {
        given: "A StudyId of an existing study and some Investigators"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withInvestigatorId(USER_ID_KNOWN)
                .build()
        and: "Current user is a DRM"
        withCurrentUserDRM()

        when: "Unassign users as Investigator"
        unAssignInvestigators.unassign(request)

        then: "Indicate parameters are missing"
        studyRepository.getStudyById(studyId).investigators.getAssigned().isEmpty()
    }

    def "Unassign Investigators should fail for non authenticated user"() {
        given: "A StudyId of an existing study and some Investigators"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withInvestigatorId(USER_ID_KNOWN)
                .build()
        and: "No user is authenticated"
        withoutAuthenticatedUser()

        when: "Unassign users as Investigator"
        unAssignInvestigators.unassign(request)

        then: "Access is denied"
        thrown(AccessDeniedException)
    }

    def "Unassign Investigators should fail for a user who is no DRM"() {
        given: "A StudyId of an existing study and some Investigators"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withInvestigatorId(USER_ID_KNOWN)
                .build()
        and: "The authenticated user is no drm"
        withCurrentUserNoDRM()

        when: "Unassign users as Investigator"
        unAssignInvestigators.unassign(request)

        then: "Access is denied"
        thrown(AccessDeniedException)
    }

    def "Unassign already removed Investigator from a Study with Investigators"() {
        given: "A StudyId of an existing study and some Investigators"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withInvestigatorId(USER_ID_KNOWN)
                .build()
        and: "Current user is a DRM"
        withCurrentUserDRM()

        when: "Unassign users as Investigator"
        unAssignInvestigators.unassign(request)

        then: "Indicate parameters are missing"
        studyRepository.getStudyById(studyId).investigators.getAssigned().isEmpty()
    }

    def userIdentifier(String userIdentifier) {
        UserIdentifier.of(userIdentifier)
    }
}
