package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.GetUsersController
import com.custodix.insite.local.ehr2edc.Investigator
import com.custodix.insite.local.ehr2edc.command.AssignInvestigator
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.command.AssignInvestigator.Request
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId

@Title("Assign Investigators")
class AssignInvestigatorSpec extends AbstractSpecification {

    private static final long EXISTING_USER_ID = 2L
    private static final UserIdentifier EXISTING_USER_IDENTIFIER = userIdentifier(EXISTING_USER_ID.toString())

    @Autowired
    AssignInvestigator assignInvestigators
    @Autowired
    TestGetUsersController getUsersController

    def setup() {
        withCurrentUserDRM()
        addTestUser()
    }

    @Unroll
    def 'Assign Investigators to a study with an invalid user'(UserIdentifier user, String field, String message) {
        given: "An existing study"
        def study = generateKnownStudyWithoutInvestigators()
        and: "a request without a user name"
        Request request = Request.newBuilder().withStudyId(study.studyId).withInvestigatorId(user).build()

        when: "Assigning users as Investigator"
        assignInvestigators.assign(request)

        then: "An error should be shown that the #field #message"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field == field
        ex.constraintViolations.first().message == message
        where:
        user                 | field               | message
        null                 | "investigatorId"    | "must not be null"
        userIdentifier(null) | "investigatorId.id" | "must not be null"
    }

    def "Assign Investigators with an empty Request"() {
        given: "An empty request"
        Request request = Request.newBuilder().build()

        when: "Assigning users as Investigator"
        assignInvestigators.assign(request)

        then: "An error should be shown that the users should not be empty"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.size() == 2
    }

    def "Assign Investigators with empty investigators"() {
        given: "A request with studyId but empty investigators"
        Request request = createRequest(aRandomStudyId(), null)

        when: "Assigning users as Investigator"
        assignInvestigators.assign(request)

        then: "An error should be shown that the users should not be empty"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field.equals("investigatorId")
        ex.constraintViolations.first().message == "must not be null"
    }

    def "Assign Investigators with no investigators"() {
        given: "A request with studyId but no investigators"
        Request request = Request.newBuilder()
                .withStudyId(aRandomStudyId())
                .build()

        when: "Assigning users as Investigator"
        assignInvestigators.assign(request)

        then: "An error should be shown that the users should not be empty"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field.equals("investigatorId")
        ex.constraintViolations.first().message == "must not be null"
    }

    def "Assign Investigators with null investigators"() {
        given: "A request with studyId but null investigators"
        Request request = createRequest(aRandomStudyId(), null)

        when: "Assigning users as Investigator"
        assignInvestigators.assign(request)

        then: "An error should be shown that the users should not be empty"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field.equals("investigatorId")
        ex.constraintViolations.first().message == "must not be null"
    }

    def "Assign Investigators without a Study"() {
        given: "A request with investigators but no study"
        Request request = createRequestWithOnlyInvestigators()

        when: "Assigning users as Investigator"
        assignInvestigators.assign(request)

        then: "An error should be shown that the studyId should not be null"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field.equals("studyId")
        ex.constraintViolations.first().message == "must not be null"
    }

    def "Assign Investigators to an unknown Study"() {
        given: "A StudyId of an unknown study and some Investigators"
        StudyId studyId = aRandomStudyId()

        and: "A user exists"
        Request request = createRequest(studyId, EXISTING_USER_IDENTIFIER)

        when: "Assigning users as Investigator"
        assignInvestigators.assign(request)

        then: "An error should be shown that the study does not exist"
        UserException ex = thrown()
        ex.message == DomainException.Type.getMessage(DomainException.Type.NOT_EXISTS, studyId)
    }

    @Unroll
    def "Assign Investigators to a Study"() {
        given: "An existing study"
        def study = generateKnownStudyWithoutInvestigators()

        when: "Assigning user with name #name and id #userId.id as investigator"
        Request request = createRequest(study.studyId, userId)
        assignInvestigators.assign(request)

        then: "The investigators should contain one investigator"
        studyRepository.getStudyById(study.getStudyId()).investigators.assigned.size() == 1
        and: "that investigator has the name #name and id #userId"
        studyRepository.getStudyById(study.getStudyId()).investigators.assigned.first().name == userId.id
        studyRepository.getStudyById(study.getStudyId()).investigators.assigned.first().investigatorId == userId

        where:
        name = "Lowie"
        userId = EXISTING_USER_IDENTIFIER
    }

    def "Assign Investigators to a Study with an invalid user identifier"() {
        given: "An existing study"
        def study = generateKnownStudyWithoutInvestigators()

        when: "Assigning user with name #name and unknown id #userId.id as investigator"
        Request request = createRequest(study.studyId, userId)
        assignInvestigators.assign(request)

        then: "An exception should be thrown"
        def exception = thrown(UserException)
        exception.message == "User for UserIdentifier{id='999999'} not found."

        where:
        name = "Lowie"
        userId = userIdentifier("999999")
    }

    def "Assign already added Investigator to a Study with Investigators"(String name, UserIdentifier userId) {
        given: "An existing study"
        def study = generateKnownStudyWithoutInvestigators()
        and: "that has an investigor with name #name and id #userId"
        addInvestigators(Collections.singletonList(new GetStudySpec.Investigator(userId, name)), study)

        when: "Assigning user with name #name and id #userId as investigator"
        Request request = createRequest(study.studyId, userId)
        assignInvestigators.assign(request)

        then: "The investigators should still contain one investigator"
        studyRepository.getStudyById(study.getStudyId()).investigators.assigned.size() == 1
        and: "that investigator has the name #name and id #userId"
        studyRepository.getStudyById(study.getStudyId()).investigators.assigned.first().name == name
        studyRepository.getStudyById(study.getStudyId()).investigators.assigned.first().investigatorId == userId

        where:
        name = "Lowie"
        userId = EXISTING_USER_IDENTIFIER
    }

    def "Assign Investigators with unauthenciated user should fail"() {
        given: "An request"
        Request request = Request.newBuilder().build()
        and: "an unknown user"
        withoutAuthenticatedUser()

        when: "Assigning users as Investigator"
        assignInvestigators.assign(request)

        then: "An error should be shown that the users should not be empty"
        def ex = thrown(AccessDeniedException)
        ex.message == "User is not a DRM"
    }

    def "Assign Investigators with user that is no drm should fail"() {
        given: "An request"
        Request request = Request.newBuilder().build()
        and: "a user who isn't DRM"
        withCurrentUserNoDRM()

        when: "Assigning users as Investigator"
        assignInvestigators.assign(request)

        then: "An error should be shown that the users should not be empty"
        def ex = thrown(AccessDeniedException)
        ex.message == "User is not a DRM"
    }

    def addTestUser() {
        def user = GetUsersController.User.newBuilder()
                .withId(EXISTING_USER_ID)
                .withName(EXISTING_USER_ID.toString())
                .build()
        getUsersController.addUsers([user])
    }

    Investigator investigator(String name, UserIdentifier userId) {
        Investigator.create(userId, name)
    }

    Request createRequestWithOnlyInvestigators() {
        Request.newBuilder()
                .withInvestigatorId(userIdentifier("2"))
                .build()
    }

    Request createRequest(StudyId studyId, UserIdentifier userIdentifier) {
        Request.newBuilder()
                .withStudyId(studyId)
                .withInvestigatorId(userIdentifier)
                .build()
    }

    static def userIdentifier(String userIdentifier) {
        UserIdentifier.of(userIdentifier)
    }
}