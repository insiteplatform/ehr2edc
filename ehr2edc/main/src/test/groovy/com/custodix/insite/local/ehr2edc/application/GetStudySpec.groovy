package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.TestSubjectsBuilder
import com.custodix.insite.local.ehr2edc.query.GetStudy
import com.custodix.insite.local.ehr2edc.query.InvestigatorProjection
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser
import com.custodix.insite.local.ehr2edc.vocabulary.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title
import spock.lang.Unroll

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.vocabulary.PatientIdObjectMother.aRandomPatientId
import static java.util.Collections.emptyList

@Title("Get a study")
class GetStudySpec extends AbstractSpecification {
    @Autowired
    GetStudy getStudy
    @Autowired
    GetCurrentUser getCurrentUser

    @Unroll
    def "Get a study as investigator"(StudyId studyId, String name, String description, Collection<Investigator> investigators, String userId,
                      Collection<EDCSubjectReference> edcReferences, Collection<SubjectId> subjectIds, Collection<PatientCDWReference> patientIds, LocalDate expectedConsentDate) {
        given: "The following edcReferences #edcReferences with #DATE_NOW as their date of consent"
        def subjectsUnderConstruction = TestSubjectsBuilder.amountOfSubjects(edcReferences.size())
        subjectsUnderConstruction.applyToEach(edcReferences, { v, b -> b.withEdcSubjectReference(v) })
        subjectsUnderConstruction.applyToEach(subjectIds, { v, b -> b.withSubjectId(v) })
        subjectsUnderConstruction.applyToEach(patientIds, { v, b -> b.withPatientCDWReference(v) })
        subjectsUnderConstruction.applyToAll({ b -> b.withDateOfConsent(DATE_NOW) })
        def subjects = subjectsUnderConstruction.toSnapshots()
        and: "A known study in the repository with id `#studyId`, name `#name`, description `#description` and the edcReferences"
        def knownStudy = generateKnownStudyWithoutInvestigators(studyId, name, description, subjects)
        and: "that has the following #investigators"
        addInvestigators(investigators, knownStudy)
        and: "logged on user #userId"
        withCurrentUserHavingId(UserIdentifier.of(userId))

        when: "The study with id `#studyId` is retrieved"
        def result = getStudy.getStudy(GetStudy.Request.newBuilder().withStudyId(studyId).build())

        then: "The known study is retrieved with id `#studyId`, name `#name`, description `#description`"
        result.study.name == name
        result.study.description == description
        result.study.id == studyId
        and: "investigators with the ids #investigatorIds"
        result.study.investigators.id.size() == investigators.investigatorId.size()
        result.study.investigators.id.containsAll(investigators.investigatorId)
        and: "with the following subject ids #subjectIds"
        result.study.subjects.subjectId.size() == subjectIds.size()
        result.study.subjects.subjectId.containsAll(subjectIds)
        and: "with subject details"
        result.study.permissions.canSubjectsBeViewed
        and: "with the following subject references #edcReferences"
        result.study.subjects.edcSubjectReference.size() == edcReferences.size()
        result.study.subjects.edcSubjectReference.containsAll(edcReferences)
        and: "with the following patient ids #patientIds"
        result.study.subjects.patientId.size() == patientIds.size()
        result.study.subjects.patientId.containsAll(patientIds)
        and: "with the subjects having '#expectedConsentDate' as their consent date"
        result.study.subjects.consentDateTime.size() == patientIds.size()
        result.study.subjects.forEach({ assert it.consentDateTime == expectedConsentDate })

        where:
        studyId                | name     | description               | investigators                                                      | userId |
                edcReferences                                                                  | subjectIds                                               | patientIds                               | expectedConsentDate
        StudyId.of("studyId")  | "Cancer" | "Investigate chemo"       | Collections.singletonList(investigator(1L, "Gert"))                | "1"    |
                [EDCSubjectReference.of("EdcSubject1")]                                        | [SubjectId.of("subjectId1")]                             | [aRandomPatientId()]                     | DATE_NOW
        StudyId.of("studyId2") | "Flue"   | "Investigate antibiotics" | Arrays.asList(investigator(1L, "Gert"), investigator(2L, "Jelle")) | "1"    |
                [EDCSubjectReference.of("EdcSubject1"), EDCSubjectReference.of("EdcSubject2")] | [SubjectId.of("subjectId1"), SubjectId.of("subjectId2")] | [aRandomPatientId(), aRandomPatientId()] | DATE_NOW
    }

    @Unroll
    def "Get a study as non investigator"(StudyId studyId, String name, String description, Collection<Investigator> investigators, String userId,
                      Collection<EDCSubjectReference> edcReferences, Collection<SubjectId> subjectIds, Collection<PatientCDWReference> patientIds) {
        given: "The following edcReferences #edcReferences with #DATE_NOW as their date of consent"
        def subjectsUnderConstruction = TestSubjectsBuilder.amountOfSubjects(edcReferences.size())
        subjectsUnderConstruction.applyToEach(edcReferences, { v, b -> b.withEdcSubjectReference(v) })
        subjectsUnderConstruction.applyToEach(subjectIds, { v, b -> b.withSubjectId(v) })
        subjectsUnderConstruction.applyToEach(patientIds, { v, b -> b.withPatientCDWReference(v) })
        subjectsUnderConstruction.applyToAll({ b -> b.withDateOfConsent(DATE_NOW) })
        def subjects = subjectsUnderConstruction.toSnapshots()
        and: "A known study in the repository with id `#studyId`, name `#name`, description `#description` and the edcReferences"
        def knownStudy = generateKnownStudyWithoutInvestigators(studyId, name, description, subjects)
        and: "that has the following #investigators"
        addInvestigators(investigators, knownStudy)
        and: "logged on user #userId"
        withCurrentUserHavingId(UserIdentifier.of(userId))

        when: "The study with id `#studyId` is retrieved"
        def result = getStudy.getStudy(GetStudy.Request.newBuilder().withStudyId(studyId).build())

        then: "The known study is retrieved with id `#studyId`, name `#name`, description `#description`"
        result.study.name == name
        result.study.description == description
        result.study.id == studyId
        and: "investigators with the ids #investigatorIds"
        result.study.investigators.id.size() == investigators.investigatorId.size()
        result.study.investigators.id.containsAll(investigators.investigatorId)
        and: "without subjects"
        !result.study.permissions.canSubjectsBeViewed
        result.study.subjects.empty

        where:
        studyId                | name     | description               | investigators                                                      | userId |
                edcReferences                                                                  | subjectIds                                               | patientIds
        StudyId.of("studyId")  | "Cancer" | "Investigate chemo"       | Collections.singletonList(investigator(1L, "Gert"))                | "3"    |
                [EDCSubjectReference.of("EdcSubject1")]                                        | [SubjectId.of("subjectId1")]                             | [aRandomPatientId()]
        StudyId.of("studyId")  | "Cancer" | "Investigate chemo"       | emptyList()                                                        | "1"    |
                [EDCSubjectReference.of("EdcSubject1")]                                        | [SubjectId.of("subjectId1")]                             | [aRandomPatientId()]
    }

    def "Get a study where current user is not the assigned investigator"(UserIdentifier loggedInUser, Collection<Investigator> investigators) {
        given: "I am logged in as #loggedInUser"
        withCurrentUserHavingId(loggedInUser)
        and: "A study with the investigators: #investigators"
        def studyId = StudyId.of("study-1")
        def subjects = createSubjectSnapshots([EDCSubjectReference.of("S1"), EDCSubjectReference.of("S2")])
        def study = generateKnownStudyWithoutInvestigators(studyId, "Study name", "A study with investigators", subjects)
        addInvestigators(investigators, study)

        when: "The study with id `#studyId` is retrieved"
        def result = getStudy.getStudy(GetStudy.Request.newBuilder().withStudyId(studyId).build())

        then: "The study information indicates that subjects cannot be added"
        !result.study.permissions.canSubjectsBeAdded
        and: "The study information indicates that subjectdetails cannot be viewed"
        !result.study.permissions.canSubjectsBeViewed

        where:
        loggedInUser            | investigators
        UserIdentifier.of("10") | emptyList()
        UserIdentifier.of("10") | Arrays.asList(investigator(1L, "Gert"))
        UserIdentifier.of("10") | Arrays.asList(investigator(1L, "Gert"), investigator(2L, "Jelle"))
    }

    def "Get a study with a submit event to EDC connection"() {
        given: "I am logged in as #loggedInUser"
        withCurrentUserHavingId(UserIdentifier.of("1"))
        and: "A study with a submit event to EDC connection"
        def study = generateKnownStudyWithoutEDCConnection(USER_ID_KNOWN)
        addSubmitEventEDCConnection(study)
        when: "The study with id `#studyId` is retrieved"
        def result = getStudy.getStudy(GetStudy.Request.newBuilder().withStudyId(study.studyId).build())

        then: "The study information indicates that a submit event to EDC connection is available"
        result.study.permissions.canSendToEDC
    }

    def "Get a study with a submit event to a disabled EDC connection"() {
        given: "I am logged in as #loggedInUser"
        withCurrentUserHavingId(UserIdentifier.of("1"))
        and: "A study with a disabled submit event to EDC connection"
        def study = generateKnownStudyWithoutEDCConnection(USER_ID_KNOWN)
        addSubmitEventEDCConnection(study, false)
        when: "The study with id `#studyId` is retrieved"
        def result = getStudy.getStudy(GetStudy.Request.newBuilder().withStudyId(study.studyId).build())

        then: "The study information indicates that a submit event to EDC connection is not available"
        !result.study.permissions.canSendToEDC
    }

    def "Get a study without a submit event to EDC connection"() {
        given: "I am logged in as #loggedInUser"
        withCurrentUserHavingId(UserIdentifier.of("1"))
        and: "A study without a submit event to EDC connection"
        def study = generateKnownStudyWithoutEDCConnection(USER_ID_KNOWN)

        when: "The study with id `#studyId` is retrieved"
        def result = getStudy.getStudy(GetStudy.Request.newBuilder().withStudyId(study.studyId).build())

        then: "The study information indicates that a submit event to EDC connection is not available"
        !result.study.permissions.canSendToEDC
    }

    def "Get a study where subjects can be added"(UserIdentifier loggedInUser, Collection<Investigator> investigators) {
        given: "I am logged in as #loggedInUser"
        withCurrentUserHavingId(loggedInUser)
        and: "A study with the investigators: #investigators"
        def studyId = StudyId.of("study-1")
        def study = generateKnownStudyWithoutInvestigators(studyId, "Study name", "A study with investigators")
        addInvestigators(investigators, study)

        when: "The study with id `#studyId` is retrieved"
        def result = getStudy.getStudy(GetStudy.Request.newBuilder().withStudyId(studyId).build())

        then: "The study information indicates that subjects can be added"
        result.study.permissions.canSubjectsBeAdded

        where:
        loggedInUser           | investigators
        UserIdentifier.of("1") | Arrays.asList(investigator(1L, "Gert"))
        UserIdentifier.of("1") | Arrays.asList(investigator(1L, "Gert"), investigator(2L, "Jelle"))
    }

    def "Get a study where subject details can be viewed"(UserIdentifier loggedInUser, Collection<Investigator> investigators) {
        given: "I am logged in as #loggedInUser"
        withCurrentUserHavingId(loggedInUser)
        and: "A study with subjects and the investigators: #investigators"
        def studyId = StudyId.of("study-1")
        def subjects = createSubjectSnapshots([EDCSubjectReference.of("S1"), EDCSubjectReference.of("S2")])
        def study = generateKnownStudyWithoutInvestigators(studyId, "Study name", "A study with investigators", subjects)
        addInvestigators(investigators, study)

        when: "The study with id `#studyId` is retrieved"
        def result = getStudy.getStudy(GetStudy.Request.newBuilder().withStudyId(studyId).build())

        then: "The study information indicates that subjects can be viewed"
        result.study.permissions.canSubjectsBeViewed

        where:
        loggedInUser           | investigators
        UserIdentifier.of("1") | Arrays.asList(investigator(1L, "Gert"))
        UserIdentifier.of("1") | Arrays.asList(investigator(1L, "Gert"), investigator(2L, "Jelle"))
    }

    def "Get a study should fail for unauthenticated user"() {
        given: "No user is authenticated"
        withoutAuthenticatedUser()
        and: "A study without a submit event to EDC connection"
        def study = generateKnownStudyWithoutEDCConnection(USER_ID_KNOWN)

        when: "The study with id `#studyId` is retrieved"
        def request = GetStudy.Request.newBuilder().withStudyId(study.studyId).build()
        getStudy.getStudy(request)

        then: "Access is denied"
        thrown(AccessDeniedException)
    }

    Investigator investigator(Long id, String name) {
        return new Investigator(UserIdentifier.of("" + id), name)
    }

    static final class Investigator implements InvestigatorProjection {
        private final UserIdentifier id
        private final String name

        Investigator(UserIdentifier id, String name) {
            this.id = id
            this.name = name
        }

        @Override
        UserIdentifier getInvestigatorId() {
            return id
        }

        @Override
        String getName() {
            return name
        }

        @Override
        boolean equals(Object o) {
            if (this == o) {
                return true
            }
            if (o == null || getClass() != o.getClass()) {
                return false
            }
            final com.custodix.insite.local.ehr2edc.Investigator that = (com.custodix.insite.local.ehr2edc.Investigator) o
            return Objects.equals(id, that.investigatorId)
        }

        @Override
        int hashCode() {
            return Objects.hash(id)
        }

        @Override
        String toString() {
            return String.format("Investigator(id: '%s', name: '%s')", id.getId(), name)
        }
    }

}
