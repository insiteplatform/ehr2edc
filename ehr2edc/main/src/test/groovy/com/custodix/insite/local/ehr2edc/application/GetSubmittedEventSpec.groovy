package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.GetUsersController
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventObjectMother
import com.custodix.insite.local.ehr2edc.provenance.model.DemographicType
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographic
import com.custodix.insite.local.ehr2edc.query.GetSubmittedEvent
import com.custodix.insite.local.ehr2edc.submitted.*
import com.custodix.insite.local.ehr2edc.vocabulary.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Unroll

import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime

import static com.custodix.insite.local.ehr2edc.submitted.SubmittedQuestionObjectMother.aDefaultSubmittedQuestionBuilder
import static java.util.Collections.singletonMap

class GetSubmittedEventSpec extends AbstractSpecification {
    private static final EventDefinitionId EVENT_DEFINITION_ID = EventDefinitionId.of("event-1")

    @Autowired
    private GetSubmittedEvent getSubmittedEvent
    @Autowired
    private TestGetUsersController userRepository

    @Override
    setup() {
        def user1 = GetUsersController.User.newBuilder().withId(123).withName("123").build()
        def user2 = GetUsersController.User.newBuilder().withId(123).withName("populator").build()
        userRepository.addUsers([user1, user2])
    }

    def "The use case throws an authorization error for an invalid submittedEventId"(SubmittedEventId submittedEventId) {
        when: "I try to ask for the submitted event with an invalid submittedEventId"
        def request = GetSubmittedEvent.Request.newBuilder()
                .withSubmittedEventId(submittedEventId)
                .build()
        getSubmittedEvent.get(request)

        then: "The request throws an exception, indicating that the user is not an investigator for the study where the submitted event is part of"
        AccessDeniedException exception = thrown(AccessDeniedException)
        exception.message == "User is not an assigned Investigator"

        where:
        submittedEventId << [SubmittedEventId.of("999"), null]
    }

    @Unroll
    def "The use case returns the submitted event"(Instant reviewTime,
                                                   String formName,
                                                   String formId,
                                                   String itemGroupName,
                                                   String itemGroupId,
                                                   String itemDisplayName,
                                                   String itemInstanceId,
                                                   String itemUnit,
                                                   String itemValue,
                                                   SubmittedLabeledValue value,
                                                   SubmittedMeasurementUnitReference unit,
                                                   String expectedValue,
                                                   String expectedValueLabel,
                                                   Boolean submittedToEDC,
                                                   ProvenanceDataPoint provenanceDataPoint
    ) {
        given: "A known study with a subject"
        def study = generateKnownStudy(USER_ID_KNOWN)
        def subjectId = study.subjects[0].subjectId
        and: "item with name #itemGroupName"
        def item = SubmittedItemObjectMother.aDefaultSubmittedItemBuilder()
                .withInstanceId(SubmittedItemId.of(itemInstanceId))
                .withLabel(SubmittedItemLabelObjectMother.aDefaultSubmittedItemLabelBuilder()
                        .withQuestion(aDefaultSubmittedQuestionBuilder()
                                .withTranslations(singletonMap(Locale.ENGLISH, itemDisplayName))
                                .build())
                        .build())
                .withValue(value)
                .withSubmittedMeasurementUnitReference(unit)
                .withSubmittedToEDC(submittedToEDC)
                .withDataPoint(provenanceDataPoint)
                .build()

        and: "item group with name #itemGroupName"
        def itemGroup = SubmittedItemGroupObjectMother.aDefaultSubmittedItemGroupBuilder()
                .withName(itemGroupName)
                .withId(ItemGroupDefinitionId.of(itemGroupId))
                .withSubmittedItems(Collections.singletonList(item))
                .build()

        and: "a submitted form with name #formName"
        def submittedForm = SubmittedFormObjectMother.aDefaultSubmittedFormBuilder()
                .withName(formName)
                .withFormDefinitionId(FormDefinitionId.of(formId))
                .withSubmittedItemGroups(Collections.singletonList(itemGroup))
                .build()
        and: "a populated event"
        def event = PopulatedEventObjectMother.generateEvent(study.studyId, EVENT_DEFINITION_ID, subjectId)
        eventRepository.save(event)
        and: "a submitted event containing a submitted form with subject id 'subject-1' and 'event-1'"
        SubmittedEvent submittedEvent = SubmittedEventObjectMother.aDefaultSubmittedEventBuilder()
                .withSubmittedDate(reviewTime)
                .withStudyId(study.studyId)
                .withSubjectId(subjectId)
                .withEventDefinitionId(EVENT_DEFINITION_ID)
                .withSubmittedForms(Collections.singletonList(submittedForm))
                .withPopulatedEventId(event.instanceId)
                .withPopulationTime(event.getPopulationTime())
                .withReferenceDate(event.getReferenceDate())
                .withPopulator(event.getPopulator().orElse(null))
                .build()

        submittedEventRepository.save(submittedEvent);

        when: "I try to request the submitted event"
        def request = GetSubmittedEvent.Request.newBuilder()
                .withSubmittedEventId(submittedEvent.id)
                .build()
        def response = getSubmittedEvent.get(request)

        then: "I receive 1 form"
        response.reviewTime == reviewTime
        response.reviewedForms.size() == 1
        response.reviewer == UserIdentifier.of("UserId")
        response.populationTime == Instant.parse("2019-07-01T08:00:00Z")
        response.referenceDate == LocalDate.of(2016, 6, 9)
        response.populator == "populator"
        and: "reviewed form has the name #formName and id #formId"
        response.reviewedForms[0].name == formName
        response.reviewedForms[0].id.id == formId
        and: "reviewed form has 1 item group"
        response.reviewedForms[0].itemGroups.size() == 1
        and: "item group name is #itemGroupName and id #itemGroupId"
        response.reviewedForms[0].itemGroups[0].name == itemGroupName
        response.reviewedForms[0].itemGroups[0].id.id == itemGroupId
        and: "item group has 1 item"
        response.reviewedForms[0].itemGroups[0].items.size() == 1
        and: "item display name  is #itemDisplayName and id #itemInstanceId"
        response.reviewedForms[0].itemGroups[0].items[0].id.id == itemInstanceId
        response.reviewedForms[0].itemGroups[0].items[0].name == itemDisplayName
        response.reviewedForms[0].itemGroups[0].items[0].unit == itemUnit
        response.reviewedForms[0].itemGroups[0].items[0].value == expectedValue
        response.reviewedForms[0].itemGroups[0].items[0].submittedToEdc == submittedToEDC
        response.reviewedForms[0].itemGroups[0].items[0].valueLabel == expectedValueLabel

        where:
        reviewTime    | formName            | formId            | itemGroupName            | itemGroupId            | itemDisplayName          | itemInstanceId          | itemUnit | itemValue            | value                                                                                                                                                                                          | unit                                                                | expectedValue        | expectedValueLabel | submittedToEDC | provenanceDataPoint
        Instant.now() | "submittedFormName" | "submittedFormId" | "submittedItemGroupName" | "submittedItemGroupId" | "This is a display name" | "submittedItemInstance" | "kg"     | "submittedItemValue" | SubmittedLabeledValue.newBuilder().withValue("this my item value").build()                                                                                                                     | SubmittedMeasurementUnitReference.newBuilder().withId("kg").build() | "this my item value" | null               | true           | ProvenanceDemographic.newBuilder().withValue("provenanceDemographic-value").withDemographicType(DemographicType.VITAL_STATUS).build()
        Instant.now() | "submittedFormName" | "submittedFormId" | "submittedItemGroupName" | "submittedItemGroupId" | "This is a display name" | "submittedItemInstance" | null     | "submittedItemValue" | SubmittedLabeledValue.newBuilder().withValue("this my item value").build()                                                                                                                     | null                                                                | "this my item value" | null               | true           | ProvenanceDemographic.newBuilder().withValue("provenanceDemographic-value").withDemographicType(DemographicType.VITAL_STATUS).build()
        Instant.now() | "submittedFormName" | "submittedFormId" | "submittedItemGroupName" | "submittedItemGroupId" | "This is a display name" | "submittedItemInstance" | null     | "submittedItemValue" | SubmittedLabeledValue.newBuilder().withValue("this my item value").withLabels(Arrays.asList(SubmittedLabel.newBuilder().withLocale(Locale.ENGLISH).withText("English label").build())).build() | null                                                                | "this my item value" | "English label"    | false          | ProvenanceDemographic.newBuilder().withValue("provenanceDemographic-value").withDemographicType(DemographicType.VITAL_STATUS).build()
        Instant.now() | "submittedFormName" | "submittedFormId" | "submittedItemGroupName" | "submittedItemGroupId" | "This is a display name" | "submittedItemInstance" | null     | "submittedItemValue" | SubmittedLabeledValue.newBuilder().withValue("this my item value").withLabels(Arrays.asList(SubmittedLabel.newBuilder().withLocale(Locale.FRENCH).withText("French label").build())).build()   | null                                                                | "this my item value" | null               | false          | ProvenanceDemographic.newBuilder().withValue("provenanceDemographic-value").withDemographicType(DemographicType.VITAL_STATUS).build()
    }

    @Unroll
    def "The use case returns reference date from populated event if submitted event has no reference date"(String referenceDate) {
        given: "A known study with a subject"
        def study = generateKnownStudy(USER_ID_KNOWN)
        def subjectId = study.subjects[0].subjectId
        and: "a reference date #referenceDate"
        def referenceDateLocalDate = LocalDate.parse(referenceDate)
        and: "save populated event with reference date #referenceDate"
        def event = PopulatedEventObjectMother.aDefaultEventBuilder(study.studyId, subjectId).withReferenceDate(referenceDateLocalDate).build()
        eventRepository.save(event)
        and: "a submitted event without reference date"
        SubmittedEvent submittedEvent = SubmittedEventObjectMother.aDefaultSubmittedEventBuilder()
                .withStudyId(study.studyId)
                .withSubjectId(subjectId)
                .withPopulatedEventId(event.instanceId)
                .withReferenceDate(null)
                .build()

        submittedEventRepository.save(submittedEvent);

        when: "Get submitted event"
        def request = GetSubmittedEvent.Request.newBuilder()
                .withSubmittedEventId(submittedEvent.id)
                .build()
        def response = getSubmittedEvent.get(request)

        then: "reference date  is #referenceDate"
        response.referenceDate == referenceDateLocalDate

        where:
        referenceDate | _
        "2018-09-16"  | _
    }

    @Unroll
    def "The use case returns population time from populated event if submitted event has no population time"(String populationTime) {
        given: "A known study with a subject"
        def study = generateKnownStudy(USER_ID_KNOWN)
        def subjectId = study.subjects[0].subjectId
        and: "a population time #populationTime"
        def populationTimeInstant = ZonedDateTime.parse(populationTime).toInstant()
        and: "save populated event with population time #populationTime"
        def event = PopulatedEventObjectMother.aDefaultEventBuilder(study.studyId, subjectId).withPopulationTime(populationTimeInstant).build()
        eventRepository.save(event)
        and: "a submitted event without population time"
        SubmittedEvent submittedEvent = SubmittedEventObjectMother.aDefaultSubmittedEventBuilder()
                .withStudyId(study.studyId)
                .withSubjectId(subjectId)
                .withPopulatedEventId(event.instanceId)
                .withPopulationTime(null)
                .build()

        submittedEventRepository.save(submittedEvent)

        when: "Get submitted event"
        def request = GetSubmittedEvent.Request.newBuilder()
                .withSubmittedEventId(submittedEvent.id)
                .build()
        def response = getSubmittedEvent.get(request)

        then: "population time is #populationTime"
        response.populationTime == populationTimeInstant

        where:
        populationTime         | _
        "2018-09-16T08:00:00Z" | _
    }

    @Unroll
    def "The use case returns no populator if submitted event has no populator"() {
        given: "A known study with a subject"
        def study = generateKnownStudy(USER_ID_KNOWN)
        def subjectId = study.subjects[0].subjectId
        and: "save populated event"
        def event = PopulatedEventObjectMother.aDefaultEventBuilder(study.studyId, subjectId).build()
        eventRepository.save(event)
        and: "a submitted event with no populator"
        SubmittedEvent submittedEvent = SubmittedEventObjectMother.aDefaultSubmittedEventBuilder()
                .withStudyId(study.studyId)
                .withSubjectId(subjectId)
                .withPopulatedEventId(event.instanceId)
                .withPopulator(null)
                .build()

        submittedEventRepository.save(submittedEvent)

        when: "Get submitted event"
        def request = GetSubmittedEvent.Request.newBuilder()
                .withSubmittedEventId(submittedEvent.id)
                .build()
        def response = getSubmittedEvent.get(request)

        then: "The submitted event has no populator"
        response.populator == null
    }

    @Unroll
    def "The use case returns the latest submitted forms with submitted item correctly"(
            String instanceId,
            SubmittedMeasurementUnitReference unit,
            String expectedUnit,
            String value,
            SubmittedItemLabel label,
            String expectedName,
            ProvenanceDataPoint provenanceDataPoint
    ) {

        given: "A known study with a subject"
        def study = generateKnownStudy(USER_ID_KNOWN)
        def subjectId = study.subjects[0].subjectId
        and: "item with label #label, instance id #instanceId, unit #unit, value #value, datatpoint, #provenanceDataPoint"
        def item = SubmittedItemObjectMother.aDefaultSubmittedItemBuilder()
                .withInstanceId(SubmittedItemId.of(instanceId))
                .withLabel(label)
                .withValue(SubmittedLabeledValueObjectMother.aDefaultSubmittedLabeledValueBuilder().withValue(value).build())
                .withSubmittedMeasurementUnitReference(unit)
                .withDataPoint(provenanceDataPoint)
                .build()

        and: "item group"
        def itemGroup = SubmittedItemGroupObjectMother.aDefaultSubmittedItemGroupBuilder()
                .withSubmittedItems(Collections.singletonList(item))
                .build()

        and: "a submitted form"
        def submittedForm = SubmittedFormObjectMother.aDefaultSubmittedFormBuilder()
                .withSubmittedItemGroups(Collections.singletonList(itemGroup))
                .build()
        and: "A populated event"
        def event = PopulatedEventObjectMother.generateEvent(StudyId.of("test"), EVENT_DEFINITION_ID, subjectId)
        eventRepository.save(event)
        and: "Submitted event containing a submitted form with subject id 'subject-1' and 'event-1'"
        SubmittedEvent submittedEvent = SubmittedEventObjectMother.aDefaultSubmittedEventBuilder()
                .withStudyId(study.studyId)
                .withSubjectId(subjectId)
                .withEventDefinitionId(EVENT_DEFINITION_ID)
                .withSubmittedForms(Collections.singletonList(submittedForm))
                .withPopulatedEventId(event.instanceId)
                .build()

        submittedEventRepository.save(submittedEvent);

        when: "I try to request the submitted event"
        def request = GetSubmittedEvent.Request.newBuilder()
                .withSubmittedEventId(submittedEvent.id)
                .build()
        def response = getSubmittedEvent.get(request)

        then: "I receive 1 form"
        response.reviewedForms.size() == 1
        and: "reviewed form has 1 item group"
        response.reviewedForms[0].itemGroups.size() == 1
        and: "item group has 1 item"
        response.reviewedForms[0].itemGroups[0].items.size() == 1
        and: "item with name #expectedName, id #id, unit #expectedUnit, value #value"
        response.reviewedForms[0].itemGroups[0].items[0].id.id == instanceId
        response.reviewedForms[0].itemGroups[0].items[0].unit == expectedUnit
        response.reviewedForms[0].itemGroups[0].items[0].value == value
        response.reviewedForms[0].itemGroups[0].items[0].name == expectedName

        where:
        instanceId              | unit                                                             | expectedUnit | value | label | expectedName | provenanceDataPoint
        "submittedItemInstance" |
                SubmittedMeasurementUnitReference.newBuilder().withId("submittedItemUnit").build() |
                "submittedItemUnit"                                                                               |
                "submittedItemValue"                                                                                      |
                SubmittedItemLabel.newBuilder().withName("label").build()                                                         |
                "label"                                                                                                                          |
                ProvenanceDemographic.newBuilder().withValue("provenanceDemographic-value").withDemographicType(DemographicType.VITAL_STATUS).build()

        "submittedItemInstance" |
                null                                                                               |
                null                                                                                              |
                "submittedItemValue"                                                                                      |
                SubmittedItemLabel.newBuilder().withName("label").build()                                                         |
                "label"                                                                                                                          |
                ProvenanceDemographic.newBuilder().withValue("provenanceDemographic-value").withDemographicType(DemographicType.VITAL_STATUS).build()

        "submittedItemInstance" |
                SubmittedMeasurementUnitReference.newBuilder().withId("submittedItemUnit").build() |
                "submittedItemUnit"                                                                               |
                "submittedItemValue"                                                                                      |
                SubmittedItemLabel.newBuilder()
                        .withName("label")
                        .withQuestion(SubmittedQuestion.newBuilder()
                                .withTranslations(singletonMap(Locale.ENGLISH, "This my question in English"))
                                .build()).build()                                                                                 |
                "This my question in English"                                                                                                    | ProvenanceDemographic.newBuilder().withValue("provenanceDemographic-value").withDemographicType(DemographicType.VITAL_STATUS).build()

        "submittedItemInstance" |
                SubmittedMeasurementUnitReference.newBuilder().withId("submittedItemUnit").build() |
                "submittedItemUnit"                                                                               |
                "submittedItemValue"                                                                                      |
                SubmittedItemLabel.newBuilder()
                        .withName("label")
                        .withQuestion(SubmittedQuestion.newBuilder()
                                .withTranslations(singletonMap(Locale.FRENCH, "This my question in English"))
                                .build()).build()                                                                                 |
                "label"                                                                                                                          |
                ProvenanceDemographic.newBuilder().withValue("provenanceDemographic-value").withDemographicType(DemographicType.VITAL_STATUS).build()

        "submittedItemInstance" |
                SubmittedMeasurementUnitReference.newBuilder().withId("submittedItemUnit").build() |
                "submittedItemUnit"                                                                               |
                "submittedItemValue"                                                                                      |
                null                                                                                                              |
                null                                                                                                                             |
                ProvenanceDemographic.newBuilder().withValue("provenanceDemographic-value").withDemographicType(DemographicType.VITAL_STATUS).build()

        "submittedItemInstance" |
                SubmittedMeasurementUnitReference.newBuilder().withId("submittedItemUnit").build() |
                "submittedItemUnit"                                                                               |
                "submittedItemValue"                                                                                      |
                SubmittedItemLabel.newBuilder().withName("label").build()                                                         |
                "label"                                                                                                                          |
                null
    }

    def "The use case fails when a user is an unassigned invesigator"(
            String instanceId,
            SubmittedMeasurementUnitReference unit,
            String expectedUnit,
            String value,
            SubmittedItemLabel label,
            String expectedName,
            ProvenanceDataPoint provenanceDataPoint
    ) {

        given: "A known study with a subject"
        def study = generateKnownStudy(USER_ID_OTHER)
        def subjectId = study.subjects[0].subjectId
        and: "item with label #label, instance id #instanceId, unit #unit, value #value, datatpoint, #provenanceDataPoint"
        def item = SubmittedItemObjectMother.aDefaultSubmittedItemBuilder()
                .withInstanceId(SubmittedItemId.of(instanceId))
                .withLabel(label)
                .withValue(SubmittedLabeledValueObjectMother.aDefaultSubmittedLabeledValueBuilder().withValue(value).build())
                .withSubmittedMeasurementUnitReference(unit)
                .withDataPoint(provenanceDataPoint)
                .build()

        and: "item group"
        def itemGroup = SubmittedItemGroupObjectMother.aDefaultSubmittedItemGroupBuilder()
                .withSubmittedItems(Collections.singletonList(item))
                .build()

        and: "a submitted form"
        def submittedForm = SubmittedFormObjectMother.aDefaultSubmittedFormBuilder()
                .withSubmittedItemGroups(Collections.singletonList(itemGroup))
                .build()
        and: "a populated event"
        def event = PopulatedEventObjectMother.generateEvent(StudyId.of("test"), EVENT_DEFINITION_ID, subjectId)
        eventRepository.save(event)
        and: "Submitted event containing a submitted form with subject id 'subject-1' and 'event-1'"
        SubmittedEvent submittedEvent = SubmittedEventObjectMother.aDefaultSubmittedEventBuilder()
                .withStudyId(study.studyId)
                .withSubjectId(subjectId)
                .withEventDefinitionId(EVENT_DEFINITION_ID)
                .withSubmittedForms(Collections.singletonList(submittedForm))
                .withPopulatedEventId(event.instanceId)
                .build()

        submittedEventRepository.save(submittedEvent);

        when: "I try to request the submitted event"
        def request = GetSubmittedEvent.Request.newBuilder()
                .withSubmittedEventId(submittedEvent.id)
                .build()
        def response = getSubmittedEvent.get(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"


        where:
        instanceId = "submittedItemInstance"
        unit = SubmittedMeasurementUnitReference.newBuilder().withId("submittedItemUnit").build()
        expectedUnit = "submittedItemUnit"
        value = "submittedItemValue"
        label = SubmittedItemLabel.newBuilder().withName("label").build()
        expectedName = "label"
        provenanceDataPoint = null
    }

    def "The use case fails when a user is not authenticated"(
            String instanceId,
            SubmittedMeasurementUnitReference unit,
            String expectedUnit,
            String value,
            SubmittedItemLabel label,
            String expectedName,
            ProvenanceDataPoint provenanceDataPoint
    ) {

        given: "A known study with a subject"
        def study = generateKnownStudy(USER_ID_KNOWN)
        def subjectId = study.subjects[0].subjectId
        and: "item with label #label, instance id #instanceId, unit #unit, value #value, datatpoint, #provenanceDataPoint"
        def item = SubmittedItemObjectMother.aDefaultSubmittedItemBuilder()
                .withInstanceId(SubmittedItemId.of(instanceId))
                .withLabel(label)
                .withValue(SubmittedLabeledValueObjectMother.aDefaultSubmittedLabeledValueBuilder().withValue(value).build())
                .withSubmittedMeasurementUnitReference(unit)
                .withDataPoint(provenanceDataPoint)
                .build()

        and: "item group"
        def itemGroup = SubmittedItemGroupObjectMother.aDefaultSubmittedItemGroupBuilder()
                .withSubmittedItems(Collections.singletonList(item))
                .build()

        and: "a submitted form"
        def submittedForm = SubmittedFormObjectMother.aDefaultSubmittedFormBuilder()
                .withSubmittedItemGroups(Collections.singletonList(itemGroup))
                .build()
        and: "a populated event"
        def event = PopulatedEventObjectMother.generateEvent(StudyId.of("test"), EVENT_DEFINITION_ID, subjectId)
        eventRepository.save(event)
        and: "Submitted event containing a submitted form with subject id 'subject-1' and 'event-1'"
        SubmittedEvent submittedEvent = SubmittedEventObjectMother.aDefaultSubmittedEventBuilder()
                .withStudyId(study.studyId)
                .withSubjectId(subjectId)
                .withEventDefinitionId(EVENT_DEFINITION_ID)
                .withSubmittedForms(Collections.singletonList(submittedForm))
                .withPopulatedEventId(event.instanceId)
                .build()

        submittedEventRepository.save(submittedEvent);
        and: "No user is authenticated"
        withoutAuthenticatedUser()

        when: "I try to request the submitted event"
        def request = GetSubmittedEvent.Request.newBuilder()
                .withSubmittedEventId(submittedEvent.id)
                .build()
        def response = getSubmittedEvent.get(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"


        where:
        instanceId              | unit                                                             | expectedUnit | value | label | expectedName | provenanceDataPoint
        "submittedItemInstance" |
                SubmittedMeasurementUnitReference.newBuilder().withId("submittedItemUnit").build() |
                "submittedItemUnit"                                                                               |
                "submittedItemValue"                                                                                      |
                SubmittedItemLabel.newBuilder().withName("label").build()                                                         |
                "label"                                                                                                                          |
                null
    }
}