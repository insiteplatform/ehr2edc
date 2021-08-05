package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.mongo.app.audit.ReviewContextDocument
import com.custodix.insite.local.ehr2edc.mongo.app.submitted.SubmittedEventMongoRepository
import com.custodix.insite.local.ehr2edc.query.ListEventDefinitions
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title

import java.time.Instant

import static com.custodix.insite.local.ehr2edc.mongo.app.audit.ReviewContextDocumentObjectMother.aDefaultReviewContextDocumentBuilder
import static com.custodix.insite.local.ehr2edc.mongo.app.review.ReviewedEventMongoSnapshotObjectMother.aDefaultReviewedEventMongoSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.query.ListEventDefinitions.Request
import static com.custodix.insite.local.ehr2edc.query.ListEventDefinitions.Response
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId

@Title("List eventdefinitions in Study")
class ListEventDefinitionsSpec extends AbstractSpecification {
    private static final Instant LAST_POPULATION_TIME = Instant.parse("2018-12-12T13:10:12Z")

    @Autowired
    ListEventDefinitions listEventDefinitionsInStudy
    @Autowired
    private SubmittedEventMongoRepository.AuditedReviewSnapshotRepository reviewContextRepository

    def "List eventdefinitions in Study with an empty Request"() {
        given: "An empty request"
        Request request = Request.newBuilder().build()

        when: "Listing the eventdefinitions in the study"
        listEventDefinitionsInStudy.list(request)

        then: "Access is denied"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
    }

    def "List eventdefinitions in Study with an invalid SubjectId"(SubjectId subjectId, String fieldName, String errMsg) {
        given: "A request without StudyId"
        StudySnapshot study = generateKnownStudyWithNoFormDefinitions()
        Request request = Request.newBuilder()
                .withStudyId(study.studyId)
                .withSubjectId(subjectId)
                .build()

        when: "Listing the eventdefinitions in the study"
        listEventDefinitionsInStudy.list(request)

        then: "An indication that request parameters are missing"
        UseCaseConstraintViolationException exception = thrown UseCaseConstraintViolationException
        exception.constraintViolations.size() == 1
        exception.constraintViolations[0].field.equals(fieldName)
        exception.constraintViolations[0].message == errMsg

        where:
        subjectId          | fieldName      | errMsg
        null               | "subjectId"    | "must not be null"
        SubjectId.of(null) | "subjectId.id" | "must not be blank"
    }

    def "List eventdefinitions in Study with an unknown StudyId"() {
        given: "A request without a StudyId"
        StudyId studyId = aRandomStudyId()
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withSubjectId(SubjectId.of("subject-1"))
                .build()

        when: "Listing the eventdefinitions in the study"
        listEventDefinitionsInStudy.list(request)

        then: "Indicate the user has no permission to perform the request"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
    }

    def "List eventdefinitions in known Study"() {
        given: "A study with attached metadata"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        populateEvents(study)
        and: "A request for eventdefinitions in that study"
        def subjectId = new ArrayList<SubjectSnapshot>(study.subjects).get(0).subjectId
        Request request = Request.newBuilder()
                .withStudyId(study.studyId)
                .withSubjectId(subjectId)
                .build()

        when: "Listing the eventdefinitions in the study"
        Response response = listEventDefinitionsInStudy.list(request)

        then: "A response is returned"
        response
        and: "The response contains the known eventdefinitions"
        response.eventDefinitionsInStudy.each {
            assert it.eventDefinitionId.id == "eventDefinitionId"
            assert it.name == "Event Name"
            assert it.formDefinitions.size() == 5
            assert it.queryCount == 15
            assert it.lastPopulationTime == LAST_POPULATION_TIME
            assert it.eventId != null
            assert !it.historyAvailable
        }
        response.eventDefinitionsInStudy.formDefinitions.flatten().each {
            assert it.formDefinitionId.id.startsWith("form-")
            assert it.name.startsWith("Form nr. ")
            assert it.queryCount == 3
        }
    }

    def "An user not assigned as investigator cannot list eventdefinitions"() {
        given: "A study with attached metadata"
        StudySnapshot study = generateKnownStudy(USER_ID_OTHER)
        populateEvents(study)
        and: "A request for eventdefinitions in that study"
        def subjectId = new ArrayList<SubjectSnapshot>(study.subjects).get(0).subjectId
        Request request = Request.newBuilder()
                .withStudyId(study.studyId)
                .withSubjectId(subjectId)
                .build()

        when: "Listing the eventdefinitions in the study"
        listEventDefinitionsInStudy.list(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"
    }

    def "An unauthenticated user cannot list eventdefinitions"() {
        given: "A study with attached metadata"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        populateEvents(study)
        and: "A request for eventdefinitions in that study"
        def subjectId = new ArrayList<SubjectSnapshot>(study.subjects).get(0).subjectId
        Request request = Request.newBuilder()
                .withStudyId(study.studyId)
                .withSubjectId(subjectId)
                .build()
        and: "No user is authenticated"
        withoutAuthenticatedUser()

        when: "Listing the eventdefinitions in the study"
        listEventDefinitionsInStudy.list(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"
    }

    def "List eventdefinitions in known Study when the study was submitted to the EDC already"() {
        given: "A study with attached metadata"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        def events = populateEvents(study)
        and: "The event was already sent to the EDC"
        def reviewedEvent = aDefaultReviewedEventMongoSnapshotBuilder()
                .withSubjectId(study.subjects[0].subjectId.id)
                .withEventDefinitionId(events[0].eventDefinitionId.id)
                .withReviewedForms(Collections.emptyList()).build()
        ReviewContextDocument reviewContextMongoSnapshot = aDefaultReviewContextDocumentBuilder()
                .withReviewedEvent(reviewedEvent)
                .build()
        reviewContextMongoRepository.save(reviewContextMongoSnapshot)
        and: "A request for eventdefinitions in that study"
        def subjectId = new ArrayList<SubjectSnapshot>(study.subjects).get(0).subjectId
        Request request = Request.newBuilder()
                .withStudyId(study.studyId)
                .withSubjectId(subjectId)
                .build()

        when: "Listing the eventdefinitions in the study"
        Response response = listEventDefinitionsInStudy.list(request)

        then: "A response is returned"
        response
        and: "The response contains the known eventdefinitions"
        response.eventDefinitionsInStudy.each {
            assert it.eventDefinitionId.id == "eventDefinitionId"
            assert it.name == "Event Name"
            assert it.formDefinitions.size() == 5
            assert it.queryCount == 15
            assert it.lastPopulationTime == LAST_POPULATION_TIME
            assert it.eventId != null
            assert it.historyAvailable
        }
    }

    def "List eventdefinitions in known Study with no form definitions"() {
        given: "A study with attached metadata with no form definitions"
        StudySnapshot study = generateKnownStudyWithNoFormDefinitions()
        and: "A request for eventdefinitions in that study"
        Request request = Request.newBuilder()
                .withStudyId(study.studyId)
                .withSubjectId(SubjectId.of("subject-1"))
                .build()

        when: "Listing the eventdefinitions in the study"
        Response response = listEventDefinitionsInStudy.list(request)

        then: "A response is returned"
        response
        and: "The response contains no eventdefinitions"
        response.eventDefinitionsInStudy.empty
    }
}
