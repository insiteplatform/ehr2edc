package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.audit.PopulationContext
import com.custodix.insite.local.ehr2edc.audit.PopulationContextRepository
import com.custodix.insite.local.ehr2edc.command.PopulateEvent
import com.custodix.insite.local.ehr2edc.metadata.model.FormDefinition
import com.custodix.insite.local.ehr2edc.populator.*
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.gateway.DemographicDocumentGateway
import com.custodix.insite.local.ehr2edc.query.populator.QueryMappingFormPopulator
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshot
import com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshotObjectMother
import com.custodix.insite.local.ehr2edc.snapshots.FormDefinitionSnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.*
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.access.AccessDeniedException
import spock.lang.Title

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.populator.PopulatedItemObjectMother.aDefaultPopulatedItemBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.MetadataDefinitionSnapshotObjectMother.aMetaDataDefinitionSnapshotWithEventDefinitions
import static com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReferenceObjectMother.aRandomEdcSubjectReference
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.*

@Title("Extract Data Points")
class PopulateEventExtractDataPointsSpec extends AbstractSpecification {
    public static final SubjectId SUBJECT_ID = SubjectId.of("SUBJ-1")
    public static final List<String> DATAPOINTS = ["{}"]
    @Autowired
    PopulateEvent populateEvent
    @Autowired
    PopulatedEventRepository eventRepository
    @MockBean
    private QueryMappingFormPopulator formPopulator
    @MockBean
    private PopulationContextRepository populationContextRepository
    @MockBean
    private DemographicDocumentGateway dataPointGateway


    EventDefinitionSnapshot eventWithForm = EventDefinitionSnapshotObjectMother.eventDefinitionWithSingleForm()
    EventDefinitionSnapshot eventWithoutForms = EventDefinitionSnapshotObjectMother.eventDefinitionWithoutForms()
    FormDefinitionSnapshot form = eventWithForm.formDefinitionSnapshots.get(0)


    def setup() {
        eventMongoSnapshotRepository.deleteAll()
    }

    def withKnownStudy(UserIdentifier investigator) {
        StudyId studyId = StudyId.of("STUDY")
        generateKnownStudyWithMetaData(studyId, SUBJECT_ID, aMetaDataDefinitionSnapshotWithEventDefinitions(eventWithForm, eventWithoutForms), investigator)
        return studyId
    }

    def "Extract data points for an event with a form with one Item which could be populated"() {
        given: "A valid request for an event with formDefinitions"
        PopulateEvent.Request request = validRequest(withKnownStudy(USER_ID_KNOWN))
        and: "An item in the form could be populated"
        aItemFor(eventWithForm.id, this.form)

        when: "Extracting the data points"
        PopulateEvent.Response result = populateEvent.populate(request)

        then: "A result is returned"
        result
        and: "The result indicates 1 data point was populated"
        result.populatedDataPoints == 1
        and: "The populated event was saved"
        Optional<PopulatedEvent> event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        event.present
        and: "The population context was saved"
        assertPopulatedContextSaved(event.get().instanceId)
    }

    def "Extract data points without a request"() {
        given: "No request"
        PopulateEvent.Request request = null

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Indicate parameters are missing"
        thrown UseCaseConstraintViolationException
        validateNoEventsSend()
        assertNoPopulatedContextSaved()
    }

    def "Extract data points with an empty request"() {
        given: "An empty request"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder().build()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"
    }

    def "Extract data points without reference date"() {
        given: "A request without referenceDate"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withReferenceDate(null)
                .build()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Indicate referenceDate is missing"
        UseCaseConstraintViolationException ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field.endsWith("referenceDate")
        ex.constraintViolations.first().message == "must not be null"
        validateNoEventsSend()
        assertNoPopulatedContextSaved()
    }

    def "Extract data points with a future reference date"() {
        given: "A request without referenceDate"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withReferenceDate(LocalDate.MAX)
                .build()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Indicate referenceDate can not be in the future"
        UseCaseConstraintViolationException ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field.endsWith("referenceDate")
        ex.constraintViolations.first().message == "must be a date in the past or in the present"
        validateNoEventsSend()
        assertNoPopulatedContextSaved()
    }

    def "Extract data points without study id"() {
        given: "A request without study id"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withStudyId(null)
                .build()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"
    }

    def "Extract data points with invalid study id"() {
        given: "A request with invalid study id"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withStudyId(StudyId.of("     "))
                .build()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Indicate the user has no permission to perform the request"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
        validateNoEventsSend()
        assertNoPopulatedContextSaved()
    }

    def "Extract data points without event id"() {
        given: "A request with missing event id"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withEventDefinitionId(null)
                .build()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Indicate eventDefinitionId is missing"
        UseCaseConstraintViolationException ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field.endsWith("eventDefinitionId")
        ex.constraintViolations.first().message == "must not be null"
        validateNoEventsSend()
        assertNoPopulatedContextSaved()
    }

    def "Extract data points with invalid event id"() {
        given: "A request with invalid event id"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withEventDefinitionId(EventDefinitionId.of("   "))
                .build()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Indicate eventDefinitionId is invalid"
        UseCaseConstraintViolationException ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field.endsWith("eventDefinitionId.id")
        ex.constraintViolations.first().message == "must not be blank"
        validateNoEventsSend()
        assertNoPopulatedContextSaved()
    }

    def "Extract data points without subject id"() {
        given: "An empty request"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withSubjectId(null)
                .build()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Indicate subjectId is missing"
        UseCaseConstraintViolationException ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().field.endsWith("subjectId")
        ex.constraintViolations.first().message == "must not be null"
        validateNoEventsSend()
        assertNoPopulatedContextSaved()
    }

    def "Extract data points with invalid subject id"() {
        given: "An empty request"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withSubjectId(SubjectId.of(""))
                .build()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Indicate subjectId is invalid"
        UseCaseConstraintViolationException ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.stream().anyMatch {
            v -> v.field == "subjectId.id" && v.message == "must not be blank"
        }
        ex.constraintViolations.stream().anyMatch {
            v -> v.field == "subjectId.id" && v.message == "size must be between 1 and 200"
        }
        validateNoEventsSend()
        assertNoPopulatedContextSaved()
    }

    def "Extract data points with valid request"() {
        given: "A valid request"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withEventDefinitionId(eventWithoutForms.id)
                .build()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "There is no indication that parameters are missing"
        notThrown(UseCaseConstraintViolationException)
    }

    def "Extract data points with valid request and unauthenticated user should fail"() {
        given: "A valid request"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withEventDefinitionId(eventWithoutForms.id)
                .build()
        and: "An unauthenticated user"
        withoutAuthenticatedUser()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Access is denied"
        thrown(AccessDeniedException)
    }

    def "Extract data points with valid request and a user who is no investigator should fail"() {
        given: "A valid request"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_OTHER)))
                .withEventDefinitionId(eventWithoutForms.id)
                .build()
        and: "An unauthenticated user"
        withoutAuthenticatedUser()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Access is denied"
        thrown(AccessDeniedException)
    }

    def "Extract data points with valid request but for an inactive subject should fail"() {
        given: "A valid request"
        def studyId = withKnownStudy(USER_ID_KNOWN)
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(studyId))
                .withEventDefinitionId(eventWithoutForms.id)
                .build()
        and: "The subject is deregistered"
        addDeregisteredSubjectToStudy(studyId, generateKnownPatientId(), aRandomEdcSubjectReference(), SUBJECT_ID)

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Indicate the user cannot populate an inactive subject"
        def ex = thrown(UserException)
        ex.message == "Cannot populate data for inactive subject '" + SUBJECT_ID.id + "'."

    }

    def "Extract data points for an unknown study returns an exception"() {
        given: "A valid request for an unknown studyId"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withStudyId(StudyId.of("unknown"))
                .build()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Indicate the user has no permission to perform the request"
        def ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
        validateNoEventsSend()
        assertNoPopulatedContextSaved()
    }

    def "Extract data points for an unknown event returns an exception"() {
        given: "A valid request for an unknown eventDefinitionId"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withEventDefinitionId(EventDefinitionId.of("unknown"))
                .build()

        when: "Extracting the data points"
        populateEvent.populate(request)

        then: "Indicate no event was found"
        def ex = thrown(UserException)
        ex.message == "Unknown event definition with id 'unknown'"
        validateNoEventsSend()
        assertNoPopulatedContextSaved()
    }

    def "Extract data points for an event without forms"() {
        given: "A valid request for an event without formDefinitions"
        PopulateEvent.Request request = PopulateEvent.Request.newBuilder(validRequest(withKnownStudy(USER_ID_KNOWN)))
                .withEventDefinitionId(eventWithoutForms.id)
                .build()

        when: "Extracting the data points"
        PopulateEvent.Response result = populateEvent.populate(request)

        then: "A result is returned"
        result
        and: "The result indicates no data points were populated"
        result.populatedDataPoints == 0
        and: "No populated event was saved"
        def event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        event.present
        event.get().populatedForms.isEmpty()
    }

    def "Extract data points for an event with a form which could not be populated"() {
        given: "A valid request for an event with formDefinitions"
        PopulateEvent.Request request = validRequest(withKnownStudy(USER_ID_KNOWN))
        and: "The form's itemgroups cannot be populated"
        noItemGroupsFor(eventWithForm.id,form)

        when: "Extracting the data points"
        PopulateEvent.Response result = populateEvent.populate(request)

        then: "A result is returned"
        result
        and: "The result indicates no data points were populated"
        result.populatedDataPoints == 0
        and: "No populated event was saved"
        def event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        event.present
        event.get().populatedForms.isEmpty()
    }

    def "Extract data points for an event with a form with an itemgroup which could not be populated"() {
        given: "A valid request for an event with formDefinitions"
        PopulateEvent.Request request = validRequest(withKnownStudy(USER_ID_KNOWN))
        and: "The form's items within the itemgroups cannot be populated"
        noItemsFor(eventWithForm.id,form)

        when: "Extracting the data points"
        PopulateEvent.Response result = populateEvent.populate(request)

        then: "A result is returned"
        result
        and: "The result indicates no data points were populated"
        result.populatedDataPoints == 0
        and: "No populated event was saved"
        def event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        event.present
        event.get().populatedForms.isEmpty()
    }

    PopulateEvent.Request validRequest(StudyId studyId) {
        return PopulateEvent.Request.newBuilder()
                .withReferenceDate(LocalDate.of(2018, 12, 10))
                .withStudyId(studyId)
                .withEventDefinitionId(eventWithForm.id)
                .withSubjectId(SUBJECT_ID)
                .build()
    }

    private validateNoEventsSend() {
        testEventPublisher.poll() == null
    }

    def noItemGroupsFor(EventDefinitionId id, FormDefinitionSnapshot formDefinition) {
        when(formPopulator.populateForm(argThat(new PopulationSpecificationMatcher(id)),any(FormDefinition))).thenReturn(
                PopulatedForm.newBuilder()
                        .withFormDefinitionId(formDefinition.id)
                        .withItemGroups([])
                        .build())
    }

    def noItemsFor(EventDefinitionId id, FormDefinitionSnapshot formDefinition) {
        when(formPopulator.populateForm(argThat(new PopulationSpecificationMatcher(id)),any(FormDefinition))).thenReturn(
                PopulatedForm.newBuilder()
                        .withFormDefinitionId(formDefinition.id)
                        .withItemGroups([PopulatedItemGroup.newBuilder()
                                                 .withItems([])
                                                 .build()])
                        .build())
    }

    def aItemFor(EventDefinitionId id, FormDefinitionSnapshot formDefinition) {
        def anItem = aDefaultPopulatedItemBuilder().build()
        when(dataPointGateway.findAllForSubject(any())).thenReturn(DATAPOINTS)
        when(formPopulator.populateForm(argThat(new PopulationSpecificationMatcher(id)), any(FormDefinition))).thenReturn(
                PopulatedForm.newBuilder()
                        .withInstanceId(FormId.of(UUID.randomUUID().toString()))
                        .withFormDefinitionId(formDefinition.id)
                        .withItemGroups([PopulatedItemGroup.newBuilder()
                                                 .withInstanceId(ItemGroupId.of("groupInstance-1"))
                                                 .withDefinition(PopulatedItemGroup.Definition.newBuilder().withId(ItemGroupDefinitionId.of("itemGroupDefinitionId-1")).withName("itemGroupDefinitionName-1").build())
                                                 .withItems([])
                                                 .build(),
                                         PopulatedItemGroup.newBuilder()
                                                 .withInstanceId(ItemGroupId.of("groupInstance-2"))
                                                 .withDefinition(PopulatedItemGroup.Definition.newBuilder().withId(ItemGroupDefinitionId.of("itemGroupDefinitionId-2")).withName("itemGroupDefinitionName-2").build())
                                                 .withItems([anItem])
                                                 .build()])
                        .build())
    }

    private assertPopulatedContextSaved(EventId eventId) {
        ArgumentCaptor<PopulationContext> captor = ArgumentCaptor.forClass(PopulationContext.class)
        verify(populationContextRepository).save(captor.capture())
        def populationContext = captor.getValue()
        eventId == populationContext.eventId &&
                !populationContext.itemQueryMappingsJson.empty &&
                !populationContext.eventDefinitionJson.empty &&
                populationContext.datapointsJsons == DATAPOINTS
    }

    private void assertNoPopulatedContextSaved() {
        verify(populationContextRepository, never()).save(any())
    }

    private class PopulationSpecificationMatcher implements ArgumentMatcher<PopulationSpecification> {

        private EventDefinitionId given

        PopulationSpecificationMatcher(EventDefinitionId given) {
            this.given = given
        }

        @Override
        boolean matches(PopulationSpecification argument) {
            return given == argument.eventDefinition.id
        }
    }
}
