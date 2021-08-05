package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.DomainTime
import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.command.SubmitReviewedEvent
import com.custodix.insite.local.ehr2edc.command.SubmitReviewedEvent.Request
import com.custodix.insite.local.ehr2edc.metadata.model.Question
import com.custodix.insite.local.ehr2edc.populator.*
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographicObjectMother
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceLabValueObjectMother
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceMedicationObjectMother
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceVitalSignObjectMother
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEventObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.*
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito
import org.mockito.Captor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Title
import spock.lang.Unroll

import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.stream.IntStream

import static com.custodix.insite.local.ehr2edc.populator.PopulatedFormObjectMother.aDefaultForm
import static com.custodix.insite.local.ehr2edc.populator.PopulatedFormObjectMother.aDefaultFormBuilder
import static com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroupObjectMother.aDefaultItemGroup
import static com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroupObjectMother.aDefaultItemGroupBuilder
import static com.custodix.insite.local.ehr2edc.populator.PopulatedItemObjectMother.aDefaultPopulatedItemBuilder
import static com.custodix.insite.local.ehr2edc.populator.PopulatedItemObjectMother.aPopulatedItemWithMeasurementUnitReference
import static java.util.Arrays.asList
import static java.util.Collections.emptyList
import static java.util.Collections.singletonList
import static java.util.stream.Collectors.toList
import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals
import static org.mockito.ArgumentMatchers.any
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.reset

@Title("Submit Reviewed PopulatedEvent containing populated forms")
class SubmitReviewedEventSpec extends AbstractSpecification {
    private static final String INSTANCE_SUFFIX = "_instance"
    private static final String ID_SUFFIX = "_id"
    private static final FormId FORM_ID = FormId.of("form-id")
    private static final ItemGroupId ITEM_GROUP_ID = ItemGroupId.of("group-id")
    private static final ItemId ITEM_ID = ItemId.of("item-id")
    public static final Instant SUBMITTED_DATE = Instant.parse("2020-01-07T08:00:00Z")
    private static final String SUBMITTED_XML = "SubmittedXml"

    @Autowired
    SubmitReviewedEvent submitReviewedEvent

    @Captor
    ArgumentCaptor argCaptor

    ObjectMapper equalsHelper = equalsObjectMapper()

    def setup() {
        testTimeService.timeTravelTo(SUBMITTED_DATE)
        reset(edcStudyGateway)
        given(edcStudyGateway.supports(any())).willReturn(true)
    }

    def "Submit a reviewed event with a single form"() {
        given: "A study"
        def study = generateStudyWithEDCConnection()
        and: "A subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "The subject has a populated event with a single form"
        def form = aDefaultForm()
        def event = createEventWithForms(study, subject, form)
        and: "I am logged in as an investigator of this study"
        def userId = authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit the form of the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms([buildForm(form)])
                .build()
        submitReviewedEvent.submit(request)

        then: "the correct SubmittedEvent is posted to the EDC gateway"
        SubmittedEvent expectedSubmittedEvent = getExpectedEvent(event, userId)
        SubmittedEvent actualSubmittedEvent = capturedSubmittedEvent()
        objectEquals(expectedSubmittedEvent, actualSubmittedEvent)
        and: "a submission context is created"
        with(reviewContextMongoRepository.findAll()[0]) {
            reviewDate == DomainTime.now()
            objectEquals(
                    populatedForms*.toForm().sort(),
                    asList(form).sort())
            it.submittedXml == SUBMITTED_XML
            objectEquals(it.toSubmittedEvent(), actualSubmittedEvent)
            reviewerUserId == userId.id
        }
    }

    def "Submit a reviewed event should store the population time"(String populationTime) {
        given: "A study"
        def study = generateStudyWithEDCConnection()
        and: "A subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "The subject has a populated event with population time #populationTime"
        def populationTimeInstant = ZonedDateTime.parse(populationTime).toInstant()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder(study.studyId, subject.subjectId).withPopulationTime(populationTimeInstant).build()
        eventRepository.save(event)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit the form of the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "submitted event is stored with population time"
        def submittedEvent = submittedEventRepository.findMostRecentSubmittedEvent(subject.subjectId, event.eventDefinitionId)
        submittedEvent.get().populationTime == populationTimeInstant
        where:
        populationTime         | _
        "2018-09-16T08:00:00Z" | _
    }

    def "Submit a reviewed event should store the reference date "(String referenceDate) {
        given: "A study"
        def study = generateStudyWithEDCConnection()
        and: "A subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "The subject has a populated event with reference date #referenceDate"
        def referenceDateLocalDate = LocalDate.parse(referenceDate)
        def event = PopulatedEventObjectMother.aDefaultEventBuilder(study.studyId, subject.subjectId).withReferenceDate(referenceDateLocalDate).build()
        eventRepository.save(event)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit the form of the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "submitted event is stored with reference date"
        def submittedEvent = submittedEventRepository.findMostRecentSubmittedEvent(subject.subjectId, event.eventDefinitionId)
        submittedEvent.get().referenceDate == referenceDateLocalDate
        where:
        referenceDate | _
        "2018-09-16"  | _
    }

    def "Submit a reviewed event with a form containing an item with an outputted measurement unit reference"() {
        given: "A study"
        def study = generateStudyWithEDCConnection()
        and: "A subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "The subject has a populated event with a form that contains an item with a non-readonly measurement unit reference"
        def eventDefinitionId = EventDefinitionId.of("evtDefId")
        def form = aDefaultFormBuilder()
                .withItemGroups([aDefaultItemGroupBuilder().withItems([aPopulatedItemWithMeasurementUnitReference(false)]).build()])
                .build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder(study.studyId, subject.subjectId, eventDefinitionId).withForms(singletonList(form)).build()
        eventRepository.save(event)
        and: "I am logged in as an investigator of this study"
        def userId = authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit the form of the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(asList(buildForm(form)))
                .build()
        submitReviewedEvent.submit(request)

        then: "the correct SubmittedEvent is posted to the EDC gateway"
        SubmittedEvent expectedSubmittedEvent = getExpectedEvent(event, userId)
        SubmittedEvent actualSubmittedEvent = capturedSubmittedEvent()
        objectEquals(expectedSubmittedEvent, actualSubmittedEvent)
    }

    def "Submit a reviewed event with a form containing an item with a non outputted measurement unit reference"() {
        given: "A study"
        def study = generateStudyWithEDCConnection()
        and: "A subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "The subject has a populated event with a form that contains an item with a readonly measurement unit reference"
        def eventDefinitionId = EventDefinitionId.of("evtDefId")
        def form = aDefaultFormBuilder()
                .withItemGroups([aDefaultItemGroupBuilder().withItems([aPopulatedItemWithMeasurementUnitReference(true)]).build()])
                .build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder(study.studyId, subject.subjectId, eventDefinitionId)
                .withForms(singletonList(form)).build()
        eventRepository.save(event)
        and: "I am logged in as an investigator of this study"
        def userId = authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit the form of the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(asList(buildForm(form)))
                .build()
        submitReviewedEvent.submit(request)

        then: "the correct SubmittedEvent is posted to the EDC gateway"
        SubmittedEvent expectedSubmittedEvent = getExpectedEvent(event, userId)
        SubmittedEvent actualSubmittedEvent = capturedSubmittedEvent()
        objectEquals(expectedSubmittedEvent, actualSubmittedEvent)
    }

    def "Submit a reviewed event with a populated form that references a local laboratory"() {
        given: "A study"
        def study = generateStudyWithEDCConnection()
        and: "A subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "The subject has a populated event with a form that references a local laboratory"
        def eventDefinitionId = EventDefinitionId.of("evtDefId")
        def form = aDefaultFormBuilder()
                .withItemGroups([aDefaultItemGroup()])
                .withLocalLab(LabName.of("My Lab Name"))
                .build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder(study.studyId, subject.subjectId, eventDefinitionId)
                .withForms(singletonList(form)).build()
        eventRepository.save(event)
        and: "I am logged in as an investigator of this study"
        def userId = authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit the form of the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(asList(buildForm(form)))
                .build()
        submitReviewedEvent.submit(request)

        then: "correct odm xml gets posted"
        SubmittedEvent expectedSubmittedEvent = getExpectedEvent(event, userId)
        SubmittedEvent actualSubmittedEvent = capturedSubmittedEvent()
        objectEquals(expectedSubmittedEvent, actualSubmittedEvent)
    }

    def "Submit a reviewed event with a reviewed form that references a local laboratory"() {
        given: "A study"
        def study = generateStudyWithEDCConnection()
        and: "A subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "The subject has a populated event with a form that references a local laboratory"
        def eventDefinitionId = EventDefinitionId.of("evtDefId")
        def form = aDefaultFormBuilder()
                .withItemGroups([aDefaultItemGroup()])
                .withLocalLab(LabName.of("Some Lab Name"))
                .build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder(study.studyId, subject.subjectId, eventDefinitionId)
                .withForms(singletonList(form)).build()
        eventRepository.save(event)
        and: "I am logged in as an investigator of this study"
        def userId = authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)
        and: "a reviewed form with a reviewed local lab"
        def reviewedForm = buildForm(form, "My Lab Name")

        when: "I submit the form of the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(asList(reviewedForm))
                .build()
        submitReviewedEvent.submit(request)

        then: "correct odm xml gets posted"
        SubmittedEvent expectedSubmittedEvent = getExpectedEventWithLabReference(event, userId, "My Lab Name")
        SubmittedEvent actualSubmittedEvent = capturedSubmittedEvent()
        objectEquals(expectedSubmittedEvent, actualSubmittedEvent)
    }

    def "Submit a reviewed event with a valid Request with form data and a subselection"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "an event for a subject with multiple forms, item groups and items"
        def events = populateEvents(study)
        def event = events.get(0)
        def forms = event.getPopulatedForms()
        and: "I am logged in as an investigator of this study"
        def userId = authenticateAsInvestigatorForStudy(study)
        and: "the EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit a subselection of forms, item groups and items in the event"
        def request = buildRequest(event.instanceId, buildSubsetFormsReviewed(forms))
        submitReviewedEvent.submit(request)

        then: "correct odm xml gets posted"
        SubmittedEvent expectedSubmittedEvent = getExpectedEventFromSubSelection(event, userId)
        SubmittedEvent actualSubmittedEvent = capturedSubmittedEvent()
        objectEquals(expectedSubmittedEvent, actualSubmittedEvent)
    }

    def "Submit a reviewed event save the read only items are copied over correctly"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event with an item that is read only"
        def readOnlyItem = aDefaultPopulatedItemBuilder()
                .withReadOnly(true)
                .build()
        def event = createEventWithItem(subject.subjectId, study.studyId, readOnlyItem)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "submitting the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "the item is not submitted to the EDC"
        def submittedEvent = submittedEventRepository.findMostRecentSubmittedEvent(subject.subjectId, event.eventDefinitionId).get()
        def item = getItem(submittedEvent)
        !item.submittedToEDC
    }

    def "Submit a reviewed event with multiple forms"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "an event with multiple forms for a subject registered in the study"
        def events = populateEvents(study)
        def event = events.get(0)
        def forms = event.getPopulatedForms()
        and: "I am logged in as an investigator of this study"
        def userId = authenticateAsInvestigatorForStudy(study)
        and: "the EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit all forms of the event"
        Request request = buildRequest(event.instanceId, buildAllFormsReviewed(forms))
        submitReviewedEvent.submit(request)

        then: "correct odm xml gets posted to EDC"
        SubmittedEvent expectedSubmittedEvent = getExpectedEvent(event, userId)
        SubmittedEvent actualSubmittedEvent = capturedSubmittedEvent()
        objectEquals(expectedSubmittedEvent, actualSubmittedEvent)
        and: "a submission context is created"
        with(reviewContextMongoRepository.findAll()[0]) {
            reviewDate == DomainTime.now()
            objectEquals(populatedForms*.toForm(), forms)
            it.submittedXml == submittedXml
            objectEquals(it.toSubmittedEvent(), capturedSubmittedEvent())
            reviewerUserId == userId.id
        }
    }

    def "Submit a reviewed event which is nested in a parent folder"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event which is nested in a parent folder"
        def eventDefinitionId = EventDefinitionId.of("evtDefId")
        def eventParentId = "pid"
        def event = PopulatedEventObjectMother.generateEvent(study.studyId, eventDefinitionId, eventParentId, subject.subjectId)
        eventRepository.save(event)
        and: "I am logged in as an investigator of this study"
        def userId = authenticateAsInvestigatorForStudy(study)
        and: "the EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "Submitting the reviewed event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "correct odm xml gets posted to EDC"
        SubmittedEvent expectedSubmittedEvent = getExpectedEvent(event, userId)
        SubmittedEvent actualSubmittedEvent = capturedSubmittedEvent()
        objectEquals(expectedSubmittedEvent, actualSubmittedEvent)
    }

    def "Submit a reviewed event saves the reviewed provenance data point for demographic correctly"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event with an item containing a demographic data point"
        def demographicDataPoint = ProvenanceDemographicObjectMother.aDefaultProvenanceDemographic()
        def demographicItem = aDefaultPopulatedItemBuilder().withDataPoint(demographicDataPoint).build()
        def event = createEventWithItem(subject.subjectId, study.studyId, demographicItem)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "submitting the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "the demographic data point is saved in the submission context"
        def submittedEvent = submittedEventRepository.findMostRecentSubmittedEvent(subject.subjectId, event.eventDefinitionId).get()
        def submittedItem = getItem(submittedEvent)
        def submittedDemographicDataPoint = submittedItem.getDataPoint()
        submittedDemographicDataPoint.value == demographicDataPoint.value
        submittedDemographicDataPoint.demographicType == demographicDataPoint.demographicType
    }

    def "Submit a reviewed event saves the reviewed provenance data point for lab value correctly"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event with an item containing a lab value data point"
        def labValueDataPoint = ProvenanceLabValueObjectMother.insulinProvenance()
        def labValueItem = aDefaultPopulatedItemBuilder().withDataPoint(labValueDataPoint).build()
        def event = createEventWithItem(subject.subjectId, study.studyId, labValueItem)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "submitting the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "the lab value data point is saved in the submission context"
        def submittedEvent = submittedEventRepository.findMostRecentSubmittedEvent(subject.subjectId, event.eventDefinitionId).get()
        def submittedItem = getItem(submittedEvent)
        def submittedLabValueDataPoint = submittedItem.getDataPoint()
        submittedLabValueDataPoint.labConcept.component == labValueDataPoint.labConcept.component
        submittedLabValueDataPoint.labConcept.concept.code == labValueDataPoint.labConcept.concept.code
        submittedLabValueDataPoint.labConcept.fastingStatus.name() == labValueDataPoint.labConcept.fastingStatus.name()
        submittedLabValueDataPoint.labConcept.method == labValueDataPoint.labConcept.method
        submittedLabValueDataPoint.labConcept.specimen == labValueDataPoint.labConcept.specimen
        submittedLabValueDataPoint.startDate == labValueDataPoint.startDate
        submittedLabValueDataPoint.endDate == labValueDataPoint.endDate
        submittedLabValueDataPoint.qualitativeResult.originalInterpretation == labValueDataPoint.qualitativeResult.originalInterpretation
        submittedLabValueDataPoint.qualitativeResult.parsedInterpretation == labValueDataPoint.qualitativeResult.parsedInterpretation
        submittedLabValueDataPoint.quantitativeResult.value == labValueDataPoint.quantitativeResult.value
        submittedLabValueDataPoint.quantitativeResult.lowerLimit == labValueDataPoint.quantitativeResult.lowerLimit
        submittedLabValueDataPoint.quantitativeResult.upperLimit == labValueDataPoint.quantitativeResult.upperLimit
        submittedLabValueDataPoint.quantitativeResult.unit == labValueDataPoint.quantitativeResult.unit
        submittedLabValueDataPoint.vendor == labValueDataPoint.vendor
    }

    def "Submit a reviewed event saves the reviewed provenance data point for medication correctly"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event with an item containing a medication data point"
        def medicationDataPoint = ProvenanceMedicationObjectMother.omeprazole()
        def medicationItem = aDefaultPopulatedItemBuilder().withDataPoint(medicationDataPoint).build()
        def event = createEventWithItem(subject.subjectId, study.studyId, medicationItem)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "submitting the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "the medication data point is saved in the submission context"
        def submittedEvent = submittedEventRepository.findMostRecentSubmittedEvent(subject.subjectId, event.eventDefinitionId).get()
        def submittedItem = getItem(submittedEvent)
        def submittedMedicationDataPoint = submittedItem.getDataPoint()
        submittedMedicationDataPoint.concept.concept.code == medicationDataPoint.concept.concept.code
        submittedMedicationDataPoint.concept.name == medicationDataPoint.concept.name
        submittedMedicationDataPoint.startDate == medicationDataPoint.startDate
        submittedMedicationDataPoint.endDate == medicationDataPoint.endDate
        submittedMedicationDataPoint.dosage.value == medicationDataPoint.dosage.value
        submittedMedicationDataPoint.dosage.unit == medicationDataPoint.dosage.unit
        submittedMedicationDataPoint.administrationRoute == medicationDataPoint.administrationRoute
        submittedMedicationDataPoint.administrationRoute == medicationDataPoint.administrationRoute
        submittedMedicationDataPoint.doseForm == medicationDataPoint.doseForm
        submittedMedicationDataPoint.doseForm == medicationDataPoint.doseForm
        submittedMedicationDataPoint.dosingFrequency == medicationDataPoint.dosingFrequency
    }

    def "Submit a reviewed event saves the reviewed provenance data point for vital sign correctly"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event with an item containing a vital sign data point"
        def vitalSignDataPoint = ProvenanceVitalSignObjectMother.diastolicBloodPressureProvenance()
        def vitalSignItem = aDefaultPopulatedItemBuilder().withDataPoint(vitalSignDataPoint).build()
        def event = createEventWithItem(subject.subjectId, study.studyId, vitalSignItem)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "submitting the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "the vital sign data point is saved in the submission context"
        def submittedEvent = submittedEventRepository.findMostRecentSubmittedEvent(subject.subjectId, event.eventDefinitionId).get()
        def submittedItem = getItem(submittedEvent)
        def submittedVitalSignDataPoint = submittedItem.getDataPoint()
        submittedVitalSignDataPoint.concept.concept.code == vitalSignDataPoint.concept.concept.code
        submittedVitalSignDataPoint.concept.component == vitalSignDataPoint.concept.component
        submittedVitalSignDataPoint.concept.location == vitalSignDataPoint.concept.location
        submittedVitalSignDataPoint.concept.laterality == vitalSignDataPoint.concept.laterality
        submittedVitalSignDataPoint.concept.position == vitalSignDataPoint.concept.position
        submittedVitalSignDataPoint.effectiveDateTime == vitalSignDataPoint.effectiveDateTime
        submittedVitalSignDataPoint.measurement.unit == vitalSignDataPoint.measurement.unit
        submittedVitalSignDataPoint.measurement.upperLimit == vitalSignDataPoint.measurement.upperLimit
        submittedVitalSignDataPoint.measurement.value == vitalSignDataPoint.measurement.value
        submittedVitalSignDataPoint.measurement.lowerLimit == vitalSignDataPoint.measurement.lowerLimit
    }

    def "Submit a reviewed event with item without data point correctly"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event with an item without data point"
        def item = aDefaultPopulatedItemBuilder().withDataPoint(null).build()
        def event = createEventWithItem(subject.subjectId, study.studyId, item)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "submitting the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "the item is saved in the submission context without data point"
        def submittedEvent = submittedEventRepository.findMostRecentSubmittedEvent(subject.subjectId, event.eventDefinitionId).get()
        def submittedItem = getItem(submittedEvent)
        submittedItem.getDataPoint() == null
    }

    def "Submit a reviewed event with a form having item groups with different id's and containing the same items"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event with a form containing 2 item groups with different id's and containing the same items"
        def form = createFormWithItemGroupsContainingSameItems("itemGroup1", "itemGroup2", "formId")
        PopulatedEvent event = createEventWithForms(study, subject, form)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit both item groups"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "no error is thrown"
        notThrown(UseCaseConstraintViolationException)
    }

    def "Submit a reviewed event with a form having item groups with same id's and containing the same items"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event with a form containing 2 item groups with same id's and containing the same items"
        def form = createFormWithItemGroupsContainingSameItems("itemGroup1", "itemGroup1", "formId")
        PopulatedEvent event = createEventWithForms(study, subject, form)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit both item groups"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "an error is thrown indicating duplicate key with item id 'item_value_1' and 'item_value_2' in form"
        def useCaseConstraintViolationException = thrown(UseCaseConstraintViolationException)
        useCaseConstraintViolationException.constraintViolations.size() == 2
        useCaseConstraintViolationException.constraintViolations.each {
            assert it.field.startsWith("itemGroup1_instance_")
            assert it.message == "Error in form with id \"formId\": ItemGroup with id \"itemGroup1\", repeat key \"item_value_1,item_value_2\": " +
                    "must be unique, found 2 occurrences"
        }
    }

    def "Submit a reviewed event with a form having item groups with same id's and containing the same keyless items"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event with a form containing 2 item groups with same id's and containing the same keyless items"
        def form = createFormWithItemGroupsContainingSameKeylessItems("itemGroup1", "itemGroup1", "formId")
        PopulatedEvent event = createEventWithForms(study, subject, form)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit both item groups"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "no error is thrown"
        notThrown(UseCaseConstraintViolationException)
    }

    def "Submit a reviewed event with multiple forms each having item groups with different id's and containing the same items"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event with 2 forms each containing 2 item groups with different id's and containing the same items"
        def form1 = createFormWithItemGroupsContainingSameItems("itemGroup1_1", "itemGroup1_2", "formId1")
        def form2 = createFormWithItemGroupsContainingSameItems("itemGroup2_1", "itemGroup2_2", "formId2")
        PopulatedEvent event = createEventWithForms(study, subject, form1, form2)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit both forms"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "no error is thrown"
        notThrown(UseCaseConstraintViolationException)
    }

    def "Submit a reviewed event with multiple forms each having item groups with same id's and containing the same items"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event with 2 forms each containing 2 item groups with same id's and containing the same items"
        def form1 = createFormWithItemGroupsContainingSameItems("itemGroup1_1", "itemGroup1_1", "formId1")
        def form2 = createFormWithItemGroupsContainingSameItems("itemGroup2_1", "itemGroup2_1", "formId2")
        PopulatedEvent event = createEventWithForms(study, subject, form1, form2)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit both forms"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "An error indicating 2 forms with duplicate item key"
        def useCaseConstraintViolationException = thrown(UseCaseConstraintViolationException)
        useCaseConstraintViolationException.constraintViolations.size() == 4
        and: "indicate itemgroups with duplicate key with item id 'item_value_1' and 'item_value_2' for form1 correctly"
        def form1_constraint = "Error in form with id \"formId1\": ItemGroup with id \"itemGroup1_1\", repeat key \"item_value_1,item_value_2\": must be unique, found 2 occurrences"
        useCaseConstraintViolationException.constraintViolations.findAll {
            it.field.startsWith("itemGroup1_1_instance") && it.message == form1_constraint
        }.size == 2
        and: "indicate itemgroups with duplicate key with item id 'item_value_1' and 'item_value_2' for form2 correctly"
        def form2_constraint = "Error in form with id \"formId2\": ItemGroup with id \"itemGroup2_1\", repeat key \"item_value_1,item_value_2\": must be unique, found 2 occurrences"
        useCaseConstraintViolationException.constraintViolations.findAll {
            it.field.startsWith("itemGroup2_1_instance") && it.message == form2_constraint
        }.size == 2
    }

    def "Submit a reviewed event when the edc returns an error nothing should be saved"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event "
        def event = createEvent(subject.subjectId, study.studyId)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "the EDC service responds with an error"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willThrow(new SystemException("This is a test exception"))

        when: "submitting the event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "no submission context is persisted"
        thrown(SystemException)
        def submittedEvent = submittedEventRepository.findMostRecentSubmittedEvent(subject.subjectId, event.eventDefinitionId)
        !submittedEvent.isPresent()
        reviewContextMongoRepository.findAll().isEmpty()
    }

    def "Submit a reviewed event with a disabled connection"() {
        given: "a study with a disabled write ODM connection"
        def study = generateKnownStudyWithoutEDCConnection(USER_ID_KNOWN)
        addSubmitEventEDCConnection(study, false)
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event"
        def event = createEvent(subject.subjectId, study.studyId)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "The EDC service responds successfully"
        given(edcStudyGateway.submitReviewedEvent(any(ExternalEDCConnection.class), any(SubmittedEvent.class), any(Study.class))).willReturn(SUBMITTED_XML)

        when: "I submit the reviewed event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "An error indicating the connection is disabled is thrown"
        def ex = thrown(UserException)
        ex.message == String.format("Disabled study connection for study '%s' and type '%s'", study.studyId, StudyConnectionType.SUBMIT_EVENT)
    }

    def "Submit a reviewed event as an unauthenticated user"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event"
        def event = createEvent(subject.subjectId, study.studyId)
        and: "I am unauthenticated"
        withoutAuthenticatedUser()

        when: "I submit the reviewed event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "An error indicating the user has no permission to perform the request"
        def ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
    }

    def "Submit a reviewed event as a user who is not an assigned investigator"() {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event"
        def event = createEvent(subject.subjectId, study.studyId)
        and: "I am logged in as a user who is not an assigned investigator for the study"
        withCurrentUserHavingId(USER_ID_OTHER)

        when: "I submit the reviewed event"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(buildAllForms(event))
                .build()
        submitReviewedEvent.submit(request)

        then: "An error indicating the user has no permission to perform the request"
        def ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
    }

    def "Submit a reviewed event with an empty Request"() {
        given: "An empty request"
        def request = Request.newBuilder().build()

        when: "Submit an event without selection"
        submitReviewedEvent.submit(request)

        then: "Access is denied"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
    }

    def "Submit a reviewed event with an empty selection"() {
        given: "A study"
        def study = generateStudyWithEDCConnection()
        and: "A subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "The subject has a populated event with a single form"
        def form = aDefaultForm()
        def event = createEventWithForms(study, subject, form)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)
        and: "A request with an empty selection"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(emptyList())
                .build()

        when: "I submit that request"
        submitReviewedEvent.submit(request)

        then: "a constraint violation exception is thrown"
        UseCaseConstraintViolationException ex = thrown(UseCaseConstraintViolationException)
        ex.message == "[reviewedForms: must not be empty]"
    }

    @Unroll
    def "Submit a reviewed event with an invalid eventId request parameter"(EventId eventId) {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        generateKnownSubject(study.studyId)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)

        when: "Submit an event with an invalid request"
        def request = Request.newBuilder()
                .withEventId(eventId)
                .withReviewedForms(emptyList())
                .build()
        submitReviewedEvent.submit(request)

        then: "Indicate the user has no permission to perform the request"
        def exception = thrown(AccessDeniedException)
        exception.message == "User is not an assigned Investigator"

        where:
        eventId << [EventId.of(null), null]
    }

    @Unroll
    def "Submit a reviewed event with an invalid reviewedForms request parameter"(List<SubmitReviewedEvent.ReviewedForm> reviewedForms, String errorField, String errorMessage) {
        given: "a study"
        def study = generateStudyWithEDCConnection()
        and: "a subject registered in the study"
        def subject = generateKnownSubject(study.studyId)
        and: "the subject has a populated event"
        def event = createEvent(subject.subjectId, study.studyId)
        and: "I am logged in as an investigator of this study"
        authenticateAsInvestigatorForStudy(study)

        when: "Submit an event with an invalid request"
        def request = Request.newBuilder()
                .withEventId(event.instanceId)
                .withReviewedForms(reviewedForms)
                .build()
        submitReviewedEvent.submit(request)

        then: "A validation error is thrown"
        def exception = thrown(UseCaseConstraintViolationException)
        exception.constraintViolations.size() == 1
        exception.constraintViolations[0].field == errorField
        exception.constraintViolations[0].message == errorMessage

        where:
        reviewedForms                                                                            | errorField                                      | errorMessage
        null                                                                                     | "reviewedForms"                                 | "must not be empty"
        []                                                                                       | "reviewedForms"                                 | "must not be empty"
        [null]                                                                                   | "reviewedForms.<list element>"                  | "must not be null"
        [aForm(FORM_ID, null)]                                                                   | "reviewedForms.itemGroups"                      | "must not be empty"
        [aForm(FORM_ID, [])]                                                                     | "reviewedForms.itemGroups"                      | "must not be empty"
        [aForm(FORM_ID, [null])]                                                                 | "reviewedForms.itemGroups.<list element>"       | "must not be null"
        [aForm(null, [anItemGroup(ITEM_GROUP_ID, [anItem(ITEM_ID)])])]                           | "reviewedForms.id"                              | "must not be null"
        [aForm(FORM_ID, [anItemGroup(ITEM_GROUP_ID, null)])]                                     | "reviewedForms.itemGroups.items"                | "must not be empty"
        [aForm(FORM_ID, [anItemGroup(ITEM_GROUP_ID, [])])]                                       | "reviewedForms.itemGroups.items"                | "must not be empty"
        [aForm(FORM_ID, [anItemGroup(ITEM_GROUP_ID, [null])])]                                   | "reviewedForms.itemGroups.items.<list element>" | "must not be null"
        [aForm(FORM_ID, [anItemGroup(null, [anItem(ITEM_ID)])])]                                 | "reviewedForms.itemGroups.id"                   | "must not be null"
        [aForm(FORM_ID, [anItemGroup(ITEM_GROUP_ID, [anItem(null)])])]                           | "reviewedForms.itemGroups.items.id"             | "must not be null"
        [aForm(FORM_ID, [anItemGroup(ITEM_GROUP_ID, [anItem(ITEM_ID)])], LabName.of(null))]      | "reviewedForms.labName.name"                    | "must not be blank"
        [aForm(FORM_ID, [anItemGroup(ITEM_GROUP_ID, [anItem(ITEM_ID)])], LabName.of(""))]        | "reviewedForms.labName.name"                    | "must not be blank"
        [aForm(FORM_ID, [anItemGroup(ITEM_GROUP_ID, [anItem(ITEM_ID)])], LabName.of("a" * 100))] | "reviewedForms.labName.name"                    | "size must be between 0 and 50"
    }

    private static SubmitReviewedEvent.ReviewedForm aForm(FormId id, List<SubmitReviewedEvent.ItemGroup> itemGroups) {
        SubmitReviewedEvent.ReviewedForm.newBuilder()
                .withId(id)
                .withItemGroups(itemGroups)
                .build()
    }

    private static SubmitReviewedEvent.ReviewedForm aForm(FormId id, List<SubmitReviewedEvent.ItemGroup> itemGroups, LabName labName) {
        SubmitReviewedEvent.ReviewedForm.newBuilder()
                .withId(id)
                .withLabName(labName)
                .withItemGroups(itemGroups)
                .build()
    }

    private static SubmitReviewedEvent.ItemGroup anItemGroup(ItemGroupId id, List<SubmitReviewedEvent.Item> items) {
        SubmitReviewedEvent.ItemGroup.newBuilder()
                .withId(id)
                .withItems(items)
                .build()
    }

    private static SubmitReviewedEvent.Item anItem(ItemId id) {
        SubmitReviewedEvent.Item.newBuilder()
                .withId(id)
                .build()
    }

    private static getItem(SubmittedEvent submittedEvent) {
        submittedEvent.getSubmittedForms().get(0).getSubmittedItemGroups().get(0).getSubmittedItems().get(0)
    }

    Request buildRequest(EventId eventId, List<SubmitReviewedEvent.ReviewedForm> reviewedForms) {
        Request.newBuilder()
                .withEventId(eventId)
                .withReviewedForms(reviewedForms)
                .build()
    }

    List<SubmitReviewedEvent.ReviewedForm> buildAllForms(PopulatedEvent event) {
        return buildAllFormsReviewed(event.getPopulatedForms())
    }

    List<SubmitReviewedEvent.ReviewedForm> buildAllFormsReviewed(List<PopulatedForm> forms) {
        forms.stream()
                .map({ f -> buildForm(f) })
                .collect(toList())
    }

    SubmitReviewedEvent.ReviewedForm buildForm(PopulatedForm form, String... reviewedLocalLabs) {
        def groups = form.itemGroups.stream().map({ group -> buildGroup(group) }).collect(toList())
        def builder = SubmitReviewedEvent.ReviewedForm.newBuilder().withId(form.instanceId).withItemGroups(groups)
        reviewedLocalLabs.each { builder.withLabName(LabName.of(it)) }
        builder.build()
    }

    SubmitReviewedEvent.ItemGroup buildGroup(PopulatedItemGroup group) {
        def items = group.items.stream()
                .map({ item -> buildItem(item) })
                .collect(toList())
        SubmitReviewedEvent.ItemGroup.newBuilder()
                .withId(group.instanceId)
                .withItems(items)
                .build()
    }

    SubmitReviewedEvent.Item buildItem(PopulatedItem item) {
        SubmitReviewedEvent.Item.newBuilder()
                .withId(item.instanceId)
                .build()
    }

    List<SubmitReviewedEvent.ReviewedForm> buildSubsetFormsReviewed(List<PopulatedForm> forms) {
        IntStream.range(0, forms.size())
                .filter({ i -> i % 2 == 0 })
                .mapToObj({ i -> forms.get(i) })
                .map({ f -> buildFormWithFilteredGroups(f) })
                .collect(toList())
    }

    SubmitReviewedEvent.ReviewedForm buildFormWithFilteredGroups(PopulatedForm form) {
        def groups = form.itemGroups.stream()
                .map({ group -> buildGroupWithFilteredItems(group) })
                .collect(toList())
        SubmitReviewedEvent.ReviewedForm.newBuilder()
                .withId(form.instanceId)
                .withItemGroups(groups)
                .build()
    }

    SubmitReviewedEvent.ItemGroup buildGroupWithFilteredItems(PopulatedItemGroup group) {
        def items = IntStream.range(0, group.items.size())
                .filter({ i -> i % 2 == 1 })
                .mapToObj({ i -> group.items.get(i) })
                .map({ item -> buildItem(item) })
                .collect(toList())
        SubmitReviewedEvent.ItemGroup.newBuilder()
                .withId(group.instanceId)
                .withItems(items)
                .build()
    }

    private static ObjectMapper equalsObjectMapper() {
        SimpleModule simpleModule = new SimpleModule() {
            @Override
            String getModuleName() {
                return "time-as-timestamp"
            }
        }
        simpleModule.addSerializer(Question.class, new JsonSerializer<Question>() {
            @Override
            void serialize(Question question, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(question.getTranslatedText(Locale.forLanguageTag("")).orElse(""))
            }
        })

        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(simpleModule)
                .registerModule(new Jdk8Module())
    }

    private static createItemGroup(String itemGroupId, List<PopulatedItem> items) {
        aDefaultItemGroupBuilder()
                .withDefinition(PopulatedItemGroupObjectMother.aDefaultItemGroupDefinitionBuilder().withId(ItemGroupDefinitionId.of(itemGroupId)).build())
                .withInstanceId(ItemGroupId.of(createInstanceId(itemGroupId)))
                .withItems(items).build()
    }

    private static createItemAsKey(String itemValue) {
        aDefaultPopulatedItemBuilder()
                .withId(createId(itemValue))
                .withValue(new LabeledValue(itemValue))
                .withKey(true)
                .withInstanceId(ItemId.of(createInstanceId(itemValue))).build()
    }


    private static createKeylessItem(String itemValue) {
        aDefaultPopulatedItemBuilder()
                .withId(createId(itemValue))
                .withValue(new LabeledValue(itemValue))
                .withKey(false)
                .withInstanceId(ItemId.of(createInstanceId(itemValue)))
                .build()
    }

    private static createInstanceId(String itemValue) {
        itemValue + INSTANCE_SUFFIX + "_" + UUID.randomUUID().toString()
    }

    private static createId(String itemValue) {
        ItemDefinitionId.of(itemValue + ID_SUFFIX)
    }

    private createEvent(SubjectId subjectId, StudyId studyId) {
        def event = PopulatedEventObjectMother.aDefaultEventBuilder(studyId, subjectId)
                .build()
        eventRepository.save(event)
        return event
    }

    def createEventWithItem(SubjectId subjectId, StudyId studyId, PopulatedItem item) {
        def event = PopulatedEventObjectMother.aDefaultEventBuilder(studyId, subjectId)
                .withForms([aDefaultFormBuilder()
                                    .withItemGroups([aDefaultItemGroupBuilder()
                                                             .withItems([item])
                                                             .build()])
                                    .build()])
                .build()
        eventRepository.save(event)
        return event
    }

    def createEventWithForms(StudySnapshot study, SubjectSnapshot subject, PopulatedForm... forms) {
        def event = PopulatedEventObjectMother.aDefaultEventBuilder(study.studyId, subject.subjectId)
                .withForms(asList(forms))
                .build()
        eventRepository.save(event)
        return event
    }

    def createFormWithItemGroupsContainingSameItems(String group1Id, String group2Id, String formId) {
        def itemKey1 = createItemAsKey("item_value_1")
        def itemKey2 = createItemAsKey("item_value_2")
        def itemGroup1 = createItemGroup(group1Id, asList(itemKey1, itemKey2))
        def itemGroup2 = createItemGroup(group2Id, asList(itemKey1, itemKey2))
        return createForm([itemGroup1, itemGroup2], formId)
    }

    def createFormWithItemGroupsContainingSameKeylessItems(String group1Id, String group2Id, String formId) {
        def itemKey1 = createKeylessItem("item_value_1")
        def itemKey2 = createKeylessItem("item_value_2")
        def itemGroup1 = createItemGroup(group1Id, asList(itemKey1, itemKey2))
        def itemGroup2 = createItemGroup(group2Id, asList(itemKey1, itemKey2))
        return createForm([itemGroup1, itemGroup2], formId)
    }

    def createForm(List<PopulatedItemGroup> itemGroups, String formId) {
        return aDefaultFormBuilder()
                .withItemGroups(itemGroups)
                .withInstanceId(FormId.of(createInstanceId(formId)))
                .withFormDefinitionId(FormDefinitionId.of(formId))
                .build()
    }

    private SubmittedEvent capturedSubmittedEvent() {
        BDDMockito.verify(edcStudyGateway).submitReviewedEvent(any(ExternalEDCConnection.class), (SubmittedEvent) argCaptor.capture(), any(Study.class))
        return argCaptor.getValue() as SubmittedEvent
    }

    private boolean objectEquals(Object expected, Object actual) {
        assertJsonEquals(equalsHelper.writeValueAsString(expected), equalsHelper.writeValueAsString(actual))
        return true
    }

    private StudySnapshot generateStudyWithEDCConnection() {
        def study = generateKnownStudyWithoutEDCConnection(USER_ID_KNOWN)
        addSubmitEventEDCConnection(study)
        return study
    }

    private authenticateAsInvestigatorForStudy(StudySnapshot study) {
        def userId = study.investigators[0].userId
        withCurrentUserHavingId(userId)
        return userId
    }

    private SubmittedEvent getExpectedEvent(PopulatedEvent populatedEvent, UserIdentifier userId) {
        return SubmittedEventObjectMother.aSubmittedEvent(populatedEvent, userId)
    }

    private SubmittedEvent getExpectedEventWithLabReference(PopulatedEvent populatedEvent, UserIdentifier userId, String lab) {
        SubmittedEvent expectedSubmittedEvent = getExpectedEvent(populatedEvent, userId)
        ReflectionTestUtils.setField(expectedSubmittedEvent.submittedForms[0], "localLab", LabName.of(lab))
        return expectedSubmittedEvent
    }

    private SubmittedEvent getExpectedEventFromSubSelection(PopulatedEvent populatedEvent, UserIdentifier userId) {
        SubmittedEvent expectedSubmittedEvent = getExpectedEvent(populatedEvent, userId)
        def forms = [expectedSubmittedEvent.submittedForms[0], expectedSubmittedEvent.submittedForms[2], expectedSubmittedEvent.submittedForms[4]]
        ReflectionTestUtils.setField(expectedSubmittedEvent, "submittedForms", forms)
        def form1Items = [expectedSubmittedEvent.submittedForms[0].submittedItemGroups[0].submittedItems[1]]
        def form2Items = [expectedSubmittedEvent.submittedForms[1].submittedItemGroups[0].submittedItems[1]]
        def form3Items = [expectedSubmittedEvent.submittedForms[2].submittedItemGroups[0].submittedItems[1]]
        ReflectionTestUtils.setField(expectedSubmittedEvent.submittedForms[0].submittedItemGroups[0], "submittedItems", form1Items)
        ReflectionTestUtils.setField(expectedSubmittedEvent.submittedForms[1].submittedItemGroups[0], "submittedItems", form2Items)
        ReflectionTestUtils.setField(expectedSubmittedEvent.submittedForms[2].submittedItemGroups[0], "submittedItems", form3Items)
        return expectedSubmittedEvent
    }
}
