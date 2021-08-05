package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.mongo.app.audit.ReviewContextDocumentObjectMother
import com.custodix.insite.local.ehr2edc.mongo.app.review.ReviewedEventMongoSnapshotObjectMother
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent
import com.custodix.insite.local.ehr2edc.query.GetEvent
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshot
import com.custodix.insite.local.ehr2edc.snapshots.FormDefinitionSnapshotObjectMother
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshotObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Unroll

import java.time.Instant
import java.util.stream.Collectors

import static com.custodix.insite.local.ehr2edc.populator.PopulatedEventObjectMother.aDefaultEventBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshotObjectMother.aDefaultEventDefinitionSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.MetadataDefinitionSnapshotObjectMother.aDefaultMetaDataDefinitionSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder
import static java.util.Collections.emptyList
import static java.util.Collections.singletonList

class GetEventSpec extends AbstractSpecification {

    @Autowired
    GetEvent getEvent

    @Unroll
    def "Getting an unknown event"(
            SubjectId subjectId,
            EventId eventId,
            FormDefinitionId formDefinitionId1,
            FormDefinitionId formDefinitionId2) {
        given:
        "a study with subject id #subjectId, " +
                "formDefinitionId #formDefinitionId1 and formDefinitionId #formDefinitionId2"
        createStudy(subjectId, EventDefinitionId.of(eventId.id), formDefinitionId1, formDefinitionId2)

        when: "getting an unknown event"
        GetEvent.Request request = GetEvent.Request.newBuilder()
                .withEventId(EventId.of("unknown"))
                .withSubjectId(subjectId)
                .build()
        getEvent.getEvent(request)

        then: "an exception is thrown"
        def exception = thrown UserException
        exception.message == "Unknown EventId[id=unknown]"

        where:
        subjectId               | eventId               | formDefinitionId1             | formDefinitionId2
        SubjectId.of("123-123") | EventId.of("123-123") | FormDefinitionId.of("FORM-1") | FormDefinitionId.of("FORM-2")
    }

    def "Getting an event"() {
        given: "A study with an event"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        def events = populateEvents(study)
        def eventDefinitionId = EventDefinitionId.of("eventDefinitionId")
        def subjectId = study.subjects[0].subjectId

        and: "The event was submitted on 01/01/2018"
        def firstSubmissionDate = Instant.parse("2018-01-01T00:00:00.00Z")
        createReviewContext(firstSubmissionDate, eventDefinitionId.getId(), subjectId.getId())
        and: "The event was submitted on 01/02/2018"
        def lastSubmissionDate = Instant.parse("2018-02-01T00:00:00.00Z")
        createReviewContext(lastSubmissionDate, eventDefinitionId.getId(), subjectId.getId())

        when: "I get the event"
        GetEvent.Request request = GetEvent.Request.newBuilder()
                .withEventId(events.get(0).getInstanceId())
                .withSubjectId(study.subjects[0].subjectId)
                .build()
        def response = getEvent.getEvent(request)

        then: "I receive a non empty response"
        !response.event.forms.empty
        response.event.forms.size() == 5
        and: "The last submission date"
        response.event.lastSubmissionTime != null
        response.event.lastSubmissionTime == lastSubmissionDate
    }

    def "I cannot get an event as an unauthenticated user"() {
        given: "A study with an event"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        def events = populateEvents(study)

        and: "An unauthenticated user"
        withoutAuthenticatedUser()

        when: "I get the event"
        GetEvent.Request request = GetEvent.Request.newBuilder()
                .withEventId(events.get(0).getInstanceId())
                .withSubjectId(study.subjects[0].subjectId)
                .build()
        getEvent.getEvent(request)

        then: "Acces is denied"
        thrown(AccessDeniedException)
    }

    def "I cannot get an event as a user who is not an assigned investigator"() {
        given: "A study with an event"
        StudySnapshot study = generateKnownStudy(USER_ID_OTHER)
        def events = populateEvents(study)

        when: "I get the event"
        GetEvent.Request request = GetEvent.Request.newBuilder()
                .withEventId(events.get(0).getInstanceId())
                .withSubjectId(study.subjects[0].subjectId)
                .build()
        getEvent.getEvent(request)

        then: "Acces is denied"
        thrown(AccessDeniedException)
    }

    @Unroll
    def "Getting an event with name"(StudyId studyId, SubjectId subjectId, EventDefinitionId eventDefinitionId, String eventName) {
        given: "An event definition with name #eventName"
        def eventDefinition = aDefaultEventDefinitionSnapshotBuilder().withId(eventDefinitionId).withName(eventName).build()

        and: "A study with study id  #studyId and subject id #subjectId and event definition id #eventDefinitionId"
        createStudy(studyId, subjectId, eventDefinition)

        and: "A event"
        def event = createEvent(studyId, subjectId, eventDefinitionId, eventName)

        when: "I get the event"
        GetEvent.Request request = GetEvent.Request.newBuilder()
                .withEventId(event.getInstanceId())
                .withSubjectId(subjectId)
                .build()
        def response = getEvent.getEvent(request)

        then: "The event name is #eventName"
        response.event.name == eventName

        where:
        studyId               | subjectId               | eventDefinitionId               | eventName
        StudyId.of("567-567") | SubjectId.of("123-123") | EventDefinitionId.of("123-123") | "Demographics"
    }

    @Unroll
    def "Getting an event with event id as event name when the event name is null"(StudyId studyId, SubjectId subjectId, EventDefinitionId eventDefinitionId, String eventName) {
        given: "An event definition with name #eventName"
        def eventDefinition = aDefaultEventDefinitionSnapshotBuilder().withId(eventDefinitionId).withName(eventName).build()

        and: "A study with study id  #studyId and subject id #subjectId and event definition id #eventDefinitionId"
        createStudy(studyId, subjectId, eventDefinition)

        and: "A event"
        def event = createEvent(studyId, subjectId, eventDefinitionId, eventName)

        when: "I get the event"
        GetEvent.Request request = GetEvent.Request.newBuilder()
                .withEventId(event.getInstanceId())
                .withSubjectId(subjectId)
                .build()
        def response = getEvent.getEvent(request)

        then: "The event name is #eventName"
        response.event.name == event.getInstanceId().getId()

        where:
        studyId               | subjectId               | eventDefinitionId               | eventName
        StudyId.of("567-567") | SubjectId.of("123-123") | EventDefinitionId.of("123-123") | null
    }

    private PopulatedEvent createEvent(final StudyId studyId, final SubjectId subjectId, final EventDefinitionId eventDefinitionId, final String eventName) {
        def event = aDefaultEventBuilder(studyId, subjectId, eventDefinitionId)
                .withName(eventName)
                .build()
        eventRepository.save(event)
        event
    }

    private void createStudy(SubjectId subjectId, EventDefinitionId eventDefinitionId, FormDefinitionId formDefinitionId1, FormDefinitionId formDefinitionId2) {
        def studySnapshot = StudySnapshotObjectMother.aDefaultStudySnapshotBuilder(UserIdentifier.of("42"))
                .withSubjects(singletonList(aDefaultSubjectSnapshotBuilder().withSubjectId(subjectId).build()))
                .withMetadata(aDefaultMetaDataDefinitionSnapshotBuilder().withEventDefinitions(singletonList(
                        createEventDefinition(eventDefinitionId, formDefinitionId1, formDefinitionId2)))
                        .build())
                .build()

        studyRepository.save(Study.restoreSnapshot(studySnapshot))
    }

    private void createStudy(StudyId studyId, SubjectId subjectId, EventDefinitionSnapshot eventDefinition) {
        def studySnapshot = StudySnapshotObjectMother.aDefaultStudySnapshotBuilder(UserIdentifier.of("42"))
                .withStudyId(studyId)
                .withSubjects(singletonList(aDefaultSubjectSnapshotBuilder()
                        .withSubjectId(subjectId)
                        .build()))
                .withMetadata(aDefaultMetaDataDefinitionSnapshotBuilder()
                        .withEventDefinitions(singletonList(eventDefinition))
                        .build())
                .build()

        studyRepository.save(Study.restoreSnapshot(studySnapshot))
    }

    private EventDefinitionSnapshot createEventDefinition(EventDefinitionId eventDefinitionId, FormDefinitionId... formDefinitionIds) {
        def formDefinitions = Arrays.stream(formDefinitionIds)
                .map { id ->
                    FormDefinitionSnapshotObjectMother.aDefaultFormDefinitionSnapshotBuilder()
                            .withId(id)
                            .build()
                }
                .collect(Collectors.toList())

        aDefaultEventDefinitionSnapshotBuilder()
                .withId(eventDefinitionId)
                .withFormDefinitions(formDefinitions).build()
    }

    def createForm(final StudyId studyId, final SubjectId subjectId, final EventDefinitionId eventDefinitionId, final FormDefinitionId formDefinitionId, final Instant populationDate) {
        def form = FormObjectMother.aDefaultFormBuilder()
                .withInstanceId(FormId.of(formDefinitionId.id))
                .withFormDefinitionId(formDefinitionId)
                .build()
        def event = aDefaultEventBuilder().withInstanceId(EventId.of(formDefinitionId.id))
                .withStudyId(studyId)
                .withSubjectId(subjectId)
                .withEventDefinitionId(eventDefinitionId)
                .withPopulationTime(populationDate)
                .withForms(singletonList(form)).build()
        eventRepository.save(event)

        return form
    }

    private createReviewContext(Instant reviewDate, String eventDefinitionId, String subjectId) {
        def context = ReviewContextDocumentObjectMother.aDefaultReviewContextDocumentBuilder()
                .withReviewDate(reviewDate)
                .withPopulatedForms(emptyList())
                .withReviewedEvent(ReviewedEventMongoSnapshotObjectMother.aDefaultReviewedEventMongoSnapshotBuilder()
                        .withEventDefinitionId(eventDefinitionId)
                        .withSubjectId(subjectId)
                        .build())
                .withSubmittedXml("")
                .withReviewerUserId("Test user")
                .build()
        reviewContextMongoRepository.save(context)
    }
}