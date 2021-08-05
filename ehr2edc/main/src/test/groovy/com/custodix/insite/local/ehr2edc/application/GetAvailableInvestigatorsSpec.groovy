package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.GetUsersController
import com.custodix.insite.local.ehr2edc.query.GetAvailableInvestigators
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.user.User
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder
import static spock.util.matcher.HamcrestSupport.expect

@Title("Get available investigators for study")
class GetAvailableInvestigatorsSpec extends AbstractSpecification {

    @Autowired
    GetAvailableInvestigators assignInvestigators

    @Autowired
    TestGetUsersController testUserRepository

    def setup() {
        testUserRepository.clear()
    }

    @Unroll
    def "Get available investigators for a study with an invalid studyId"(StudyId studyId, String typeOfError, String errorMessage) {
        given: "A request with an #typeOfError"
        GetAvailableInvestigators.Request request = GetAvailableInvestigators.Request.newBuilder().withStudyId(studyId).build()
        when: "Retrieving available investigators for the study"
        assignInvestigators.get(request)
        then: "An error for studyId containing  #errorMessage should appear"
        UseCaseConstraintViolationException ex = thrown()
        ex.constraintViolations.first().message == errorMessage
        ex.constraintViolations.first().field.startsWith("studyId")
        where:
        studyId          | typeOfError     | errorMessage
        null             | "null study id" | "must not be null"
        StudyId.of(null) | "null study id" | "must not be blank"
    }

    @Unroll
    def "Get available investigators for a study"() {
        given: "An study without investigators"
        def study = generateKnownStudyWithoutInvestigators()
        and: "the users with names #lwbUsers.name"
        testUserRepository.addUsers(lwbUsers)
        when: "Retrieving available investigators for the study"
        GetAvailableInvestigators.Request request = GetAvailableInvestigators.Request.newBuilder().withStudyId(study.studyId).build()
        def get = assignInvestigators.get(request)
        then: "All the users are returned as potential investigators"
        expect get.potentialInvestigators.name, containsInAnyOrder(expectedPotentialInvestigators.name.toArray())
        expect get.potentialInvestigators.userIdentifier, containsInAnyOrder(expectedPotentialInvestigators.userIdentifier.toArray())


        where:
        lwbUsers = [createLwbUser("Gert", 1L), createLwbUser("Bart", 2L)]
        expectedPotentialInvestigators =
                [createUser("Gert"), createUser("Bart")]
    }

    @Unroll
    def "Get available investigators for a study with existing investigators"() {
        given: "An study"
        def study = generateKnownStudyWithoutInvestigators()
        and: "having an investigator #existingInvestigator.name"
        addInvestigators(Collections.singletonList(new GetStudySpec.Investigator(existingInvestigatorId, existingInvestigatorName)), study)
        and: "the users with names #lwbUsers.name"
        testUserRepository.addUsers(lwbUsers)
        when: "Retrieving available investigators for the study"
        GetAvailableInvestigators.Request request = GetAvailableInvestigators.Request.newBuilder().withStudyId(study.studyId).build()
        def get = assignInvestigators.get(request)
        then: "Only the user #potentialInvestigatorUser.name is returned as potential investigator"
        expect get.potentialInvestigators.name, containsInAnyOrder(potentialInvestigatorUser.name)
        expect get.potentialInvestigators.userIdentifier, containsInAnyOrder(potentialInvestigatorUser.userIdentifier)


        where:
        lwbUsers = [createLwbUser("Gert", 1L), createLwbUser("Bart", 2L)]

        existingInvestigatorUser = createUser("Gert")
        existingInvestigatorId = existingInvestigatorUser.userIdentifier
        existingInvestigatorName = existingInvestigatorUser.name
        potentialInvestigatorUser = createUser("Bart")
    }

    GetUsersController.User createLwbUser(String name, Long id) {
        GetUsersController.User.newBuilder().withName(name).withId(id).build()
    }

    User createUser(String userIdAndName) {
        User.newBuilder().withName(userIdAndName).withUserId(UserIdentifier.of(userIdAndName)).build()
    }

}
