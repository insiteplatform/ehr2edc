package com.custodix.insite.local.ehr2edc.security

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.StudyRepository
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventRepository
import com.custodix.insite.local.ehr2edc.query.security.IsAny
import com.custodix.insite.local.ehr2edc.query.security.IsAssignedInvestigator
import com.custodix.insite.local.ehr2edc.query.security.IsAuthenticated
import com.custodix.insite.local.ehr2edc.query.security.IsDRM
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.snapshots.InvestigatorSnapshot
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEventRepository
import com.custodix.insite.local.ehr2edc.usecase.impl.security.IsAnyGuard
import com.custodix.insite.local.ehr2edc.usecase.impl.security.IsAssignedInvestigatorGuard
import com.custodix.insite.local.ehr2edc.usecase.impl.security.IsDRMGuard
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.context.ContextConfiguration
import spock.lang.Title

@Title("AssignedInvestigator Authorization")
@ContextConfiguration(classes = [TestConfiguration.class, SecurityConfiguration.class, AuthorizationTestConfig.class])
class AuthorizationControllerSpec extends AbstractSpecification {

    @Autowired
    PublicRequest publicExample
    @Autowired
    AuthorizedRequest authorizedExample

    @SpringBean
    StudyRepository studyRepository = Mock()
    @SpringBean
    SubmittedEventRepository submittedEventRepository = Mock()
    @SpringBean
    PopulatedEventRepository populatedEventRepository = Mock()
    @SpringBean
    GetCurrentUser getCurrentUser = Mock()

    def "Executing a use case with unspecified security when you're not signed in"() {
        given: "A request"
        def request = PublicRequest.Request.newBuilder()
                .withStudyId(StudyId.of("aStudy"))
                .build()
        and: "No user is signed in"
        notSignedIn()

        when: "Executing the public use case"
        publicExample.doDisallowedRequest(request)

        then: "An access denied exception is thrown"
        def ex = thrown(AccessDeniedException)
        ex.message == "Access denied."
    }

    def "Executing a use case with unspecified security when you're signed in"() {
        given: "A request"
        def request = PublicRequest.Request.newBuilder()
                .withStudyId(StudyId.of("aStudy"))
                .build()
        and: "A user is signed in"
        signInAs("username")

        when: "Executing the public use case"
        publicExample.doDisallowedRequest(request)

        then: "An access denied exception is thrown"
        def ex = thrown(AccessDeniedException)
        ex.message == "Access denied."
    }

    def "Executing a public use case when you're not signed in"() {
        given: "A request"
        def request = PublicRequest.Request.newBuilder()
                .withStudyId(StudyId.of("aStudy"))
                .build()
        and: "No user is signed in"
        notSignedIn()

        when: "Executing the public use case"
        publicExample.doRequest(request)

        then: "No exceptions are thrown"
        noExceptionThrown()
    }

    def "Executing a public use case when you're signed in"() {
        given: "A request"
        def request = PublicRequest.Request.newBuilder()
                .withStudyId(StudyId.of("aStudy"))
                .build()
        and: "A user is signed in"
        signInAs("username")

        when: "Executing the public use case"
        publicExample.doRequest(request)

        then: "No exceptions are thrown"
        noExceptionThrown()
    }

    def "Executing an authenticated use case when you're not signed in"() {
        given: "A request"
        def request = AuthorizedRequest.Request.newBuilder()
                .withStudyId(StudyId.of("aStudy"))
                .build()
        and: "No user is signed in"
        notSignedIn()

        when: "Executing the public use case"
        authorizedExample.doAuthenticatedRequest(request)

        then: "An access denied exception is thrown"
        def ex = thrown(AccessDeniedException)
        ex.message == "Authentication required."
    }

    def "Executing an authenticated use case when you're signed in"() {
        given: "A request"
        def request = AuthorizedRequest.Request.newBuilder()
                .withStudyId(StudyId.of("aStudy"))
                .build()
        and: "A user is signed in"
        signInAs("username")

        when: "Executing the public use case"
        authorizedExample.doAuthenticatedRequest(request)

        then: "No exceptions are thrown"
        noExceptionThrown()
    }

    def "Executing a investigator-only use case when you're not signed-in"() {
        given: "A request"
        def request = AuthorizedRequest.Request.newBuilder()
                .withStudyId(StudyId.of("aStudy"))
                .build()
        and: "No user is signed in"
        noUser()

        when: "Executing the authorized use case"
        authorizedExample.doRequest(request)

        then: "An access denied exception is thrown"
        def ex = thrown(DomainException)
        ex.message == "No authenticated user found"
    }

    def "Executing a investigator-only use case without a request when you're not signed in"() {
        given: "No request"
        def request = null
        and: "No user is signed in"
        noUser()

        when: "Executing the authorized use case"
        authorizedExample.doRequest(request)

        then: "An access denied exception is thrown"
        def ex = thrown(DomainException)
        ex.message == "No authenticated user found"
    }

    def "Executing a investigator-only use case without a correlator in the request when you're not signed in"() {
        given: "A request without a studyId"
        def request = AuthorizedRequest.Request.newBuilder()
                .withStudyId(null)
                .build()
        and: "No user is signed in"
        noUser()

        when: "Executing the authorized use case"
        authorizedExample.doRequest(request)

        then: "An access denied exception is thrown"
        def ex = thrown(DomainException)
        ex.message == "No authenticated user found"
    }

    def "Executing a investigator-only use case when you're not an assigned investigator"(String user) {
        given: "A request"
        def studyId = StudyId.of("aStudy")
        def request = AuthorizedRequest.Request.newBuilder()
                .withStudyId(studyId)
                .build()
        and: "A user '#user' is signed in"
        signInAs(user)
        and: "The user is not assigned as investigator"
        studyWithoutInvestigator(studyId)

        when: "Executing the authorized use case"
        authorizedExample.doRequest(request)

        then: "An access denied exception is thrown"
        def ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"

        where:
        user = "username"
    }

    def "Executing a investigator-only use case when you're an assigned investigator"(String user) {
        given: "A request"
        def studyId = StudyId.of("aStudy")
        def request = AuthorizedRequest.Request.newBuilder()
                .withStudyId(studyId)
                .build()
        and: "A user '#user' is signed in"
        signInAs(user)
        and: "The user is assigned as investigator"
        studyWithInvestigator(studyId, user)

        when: "Executing the authorized use case"
        authorizedExample.doRequest(request)

        then: "No exception is thrown"
        noExceptionThrown()

        where:
        user = "username"
    }

    def "Executing a DRM-only use case when you're not signed-in"() {
        given: "No user is signed in"
        noUser()

        when: "Executing the authorized use case"
        authorizedExample.doDRMRequest()

        then: "An access denied exception is thrown"
        def ex = thrown(AccessDeniedException)
        ex.message == "User is not a DRM"
    }

    def "Executing a DRM-only use case when you're not a DRM"() {
        given: "The user is not a DRM"
        userIsDRM(false)

        when: "Executing the authorized use case"
        authorizedExample.doDRMRequest()

        then: "An access denied exception is thrown"
        def ex = thrown(AccessDeniedException)
        ex.message == "User is not a DRM"
    }

    def "Executing a DRM-only use case when you're a DRM"() {
        given: "The user is a DRM"
        userIsDRM(true)

        when: "Executing the authorized use case"
        authorizedExample.doDRMRequest()

        then: "No exception is thrown"
        noExceptionThrown()
    }

    def studyWithoutInvestigator(StudyId studyId) {
        def investigators = []
        returnAStudy(studyId, investigators)
    }

    def studyWithInvestigator(StudyId studyId, String user) {
        def investigators = [
                InvestigatorSnapshot.newBuilder()
                        .withUserId(UserIdentifier.of(user))
                        .build()]
        returnAStudy(studyId, investigators)
    }

    private void returnAStudy(StudyId studyId, List<InvestigatorSnapshot> investigators) {
        Study study = Study.restoreSnapshot(StudySnapshot.newBuilder()
                .withStudyId(studyId)
                .withInvestigators(investigators)
                .build())
        1 * studyRepository.getStudyById(_) >> study
    }

    private void noUser() {
        getCurrentUser.getUserId() >> { throw new DomainException("No authenticated user found") }
        getCurrentUser.isAuthenticated() >> false
    }

    private void notSignedIn() {
        logout()
    }

    private void signInAs(String username) {
        getCurrentUser.getUserId() >> UserIdentifier.of(username)
        getCurrentUser.isAuthenticated() >> true
    }

    private void userIsDRM(boolean drm) {
        getCurrentUser.isDRM() >> drm
        getCurrentUser.isAuthenticated() >> true
    }

    @Configuration
    static class AuthorizationTestConfig {

        @Bean
        IsAny isAny() {
            return new IsAnyGuard()
        }

        @Bean
        IsAuthenticated isAuthenticated(GetCurrentUser getCurrentUser) {
            return new com.custodix.insite.local.ehr2edc.usecase.impl.security.IsAuthenticatedGuard(getCurrentUser)
        }

        @Bean
        IsAssignedInvestigator isAssignedInvestigator(GetCurrentUser getCurrentUser, StudyRepository studyRepository, PopulatedEventRepository populatedEventRepository, SubmittedEventRepository submittedEventRepository) {
            return new IsAssignedInvestigatorGuard(getCurrentUser, studyRepository, populatedEventRepository, submittedEventRepository)
        }

        @Bean
        IsDRM isDRM(GetCurrentUser getCurrentUser) {
            return new IsDRMGuard(getCurrentUser)
        }
    }
}
