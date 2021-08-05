package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.mongo.app.audit.ReviewContextDocument
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent
import com.custodix.insite.local.ehr2edc.query.GetSubmittedEventHistory
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.vocabulary.EventId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedEventId
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

import java.time.Duration
import java.time.Instant

import static com.custodix.insite.local.ehr2edc.mongo.app.audit.ReviewContextDocumentObjectMother.aDefaultReviewContextDocumentBuilder
import static com.custodix.insite.local.ehr2edc.mongo.app.review.ReviewedEventMongoSnapshotObjectMother.aDefaultReviewedEventMongoSnapshotBuilder

class GetSubmittedEventContentHistorySpec extends AbstractSpecification {
    private static final SubjectId SUBJECT_ID = SubjectId.of("subject-1")
    private static final EventId EVENT_ID = EventId.of("eventDefinitionId")
    public static final Instant NOW = Instant.now()

    @Autowired
    private GetSubmittedEventHistory getReviewedFormHistory

    def "Using an invalid request"(EventId eventId, String errorField, String errorMessage) {
        given: "A study with at least 1 populated event"
        def study = generateKnownStudy(USER_ID_KNOWN)
        populateEvents(study)
        def subjectId = study.subjects[0].subjectId
        when: "I request the reviewed forms"
        def request = GetSubmittedEventHistory.Request.newBuilder()
                .withSubjectId(subjectId)
                .withEventId(eventId)
                .build()
        getReviewedFormHistory.get(request)

        then: "I receive an empty list"
        UseCaseConstraintViolationException exception = thrown(UseCaseConstraintViolationException)
        def violations = exception.constraintViolations
        violations.size() == 1
        violations[0].field == errorField
        violations[0].message == errorMessage

        where:
        eventId        || errorField   | errorMessage
        null           || "eventId"    | "must not be null"
        EventId.of("") || "eventId.id" | "must not be blank"
    }

    def "Using a valid request as a non-investigator user"() {
        given: "I am logged in as a non-investigator"
        withCurrentUserHavingId(UserIdentifier.of("9999"))
        and: "A study with at least 1 populated event"
        def study = generateKnownStudy(USER_ID_KNOWN)
        def events = populateEvents(study)
        def subjectId = study.subjects[0].subjectId
        def event = events[0]
        and: "A reviewed event was sent once for the event"
        submitReviewedEvent(subjectId, event, "review-1", NOW)

        when: "I request the reviewed forms"
        def request = GetSubmittedEventHistory.Request.newBuilder()
                .withSubjectId(subjectId)
                .withEventId(event.instanceId)
                .build()
        getReviewedFormHistory.get(request)

        then: "An exception is thrown that the user doesn't have permission"
        thrown(AccessDeniedException)
    }
    def "Using a valid request as a non authenticated user"() {
        given: "I am not logged in"
        withoutAuthenticatedUser()
        and: "A study with at least 1 populated event"
        def study = generateKnownStudy(USER_ID_KNOWN)
        def events = populateEvents(study)
        def subjectId = study.subjects[0].subjectId
        def event = events[0]
        and: "A reviewed event was sent once for the event"
        submitReviewedEvent(subjectId, event, "review-1", NOW)

        when: "I request the reviewed forms"
        def request = GetSubmittedEventHistory.Request.newBuilder()
                .withSubjectId(subjectId)
                .withEventId(event.instanceId)
                .build()
        getReviewedFormHistory.get(request)

        then: "An exception is thrown that the user doesn't have permission"
        thrown(AccessDeniedException)
    }

    def "Using a valid request after the event was submitted once"() {
        given: "A study with at least 1 populated event"
        def study = generateKnownStudy(USER_ID_KNOWN)
        def events = populateEvents(study)
        def subjectId = study.subjects[0].subjectId
        def event = events[0]
        and: "A reviewed event was submitted once for the event"
        submitReviewedEvent(subjectId, event, "review-1", NOW)

        when: "I request the reviewed forms"
        def request = GetSubmittedEventHistory.Request.newBuilder()
                .withSubjectId(subjectId)
                .withEventId(event.instanceId)
                .build()
        def response = getReviewedFormHistory.get(request)

        then: "I receive an empty list"
        response.historyItems.size() == 1
        response.historyItems[0].reviewedEventId != null
        response.historyItems[0].reviewDateTime == NOW
    }

    def "Using a valid request after the event was submitted twice"() {
        given: "A study with at least 1 populated event"
        def study = generateKnownStudy(USER_ID_KNOWN)
        def events = populateEvents(study)
        def subjectId = study.subjects[0].subjectId
        def event = events[0]
        and: "A reviewed event was submitted twice for the event"
        submitReviewedEvent(subjectId, event, "review-1", NOW.minus(Duration.ofDays(5)))
        submitReviewedEvent(subjectId, event, "review-2", NOW)

        when: "I request the reviewed forms"
        def request = GetSubmittedEventHistory.Request.newBuilder()
                .withSubjectId(subjectId)
                .withEventId(event.instanceId)
                .build()
        def response = getReviewedFormHistory.get(request)

        then: "I receive an empty list"
        response.historyItems.size() == 2
        response.historyItems[0].reviewedEventId == SubmittedEventId.of("review-2")
        response.historyItems[0].reviewDateTime == NOW
        response.historyItems[0].reviewer == UserIdentifier.of("UserId")
        response.historyItems[1].reviewedEventId == SubmittedEventId.of("review-1")
        response.historyItems[1].reviewDateTime == NOW.minus(Duration.ofDays(5L))
        response.historyItems[1].reviewer == UserIdentifier.of("UserId")
    }

    private void submitReviewedEvent(SubjectId subjectId, PopulatedEvent event, String reviewContextId, Instant reviewTime) {
        def reviewedEvent = aDefaultReviewedEventMongoSnapshotBuilder()
                .withSubjectId(subjectId.id)
                .withEventDefinitionId(event.eventDefinitionId.id)
                .withReviewedForms(Collections.emptyList())
                .build()
        ReviewContextDocument reviewContextMongoSnapshot = aDefaultReviewContextDocumentBuilder()
                .withId(reviewContextId)
                .withReviewedEvent(reviewedEvent)
                .withReviewDate(reviewTime)
                .build()
        reviewContextMongoRepository.save(reviewContextMongoSnapshot)
    }
}