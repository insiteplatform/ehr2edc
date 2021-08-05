package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.populator.*
import com.custodix.insite.local.ehr2edc.provenance.model.*
import com.custodix.insite.local.ehr2edc.query.GetItemProvenance
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.vocabulary.EventId
import com.custodix.insite.local.ehr2edc.vocabulary.ItemId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.populator.PopulatedEventObjectMother.SUBJECT_ID
import static com.custodix.insite.local.ehr2edc.populator.PopulatedEventObjectMother.aDefaultEvent
import static com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographicObjectMother.MALE_VALUE
import static com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographicObjectMother.male
import static com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceLabValueObjectMother.*
import static com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceMedicationObjectMother.*
import static com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceVitalSignObjectMother.*
import static com.custodix.insite.local.ehr2edc.query.GetItemProvenance.Request
import static com.custodix.insite.local.ehr2edc.query.GetItemProvenance.Response
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId

class GetItemProvenanceSpec extends AbstractSpecification {

    @Autowired
    GetItemProvenance getItemProvenance

    def "An assigned investigator can get the provenance of a demographic item"() {
        given: "I am an assigned investigator"
        generateKnownSubject(aRandomStudyId(), SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "a demographic item"
        ProvenanceDemographic demographic = male()
        PopulatedItem item = PopulatedItemObjectMother.aDefaultPopulatedItemBuilder().withDataPoint(demographic).build()
        and: "an event for a subject containing a form containing an item group containing the item"
        def itemGroup = PopulatedItemGroupObjectMother.aDefaultItemGroupBuilder().withItems([item]).build()
        def form = PopulatedFormObjectMother.aDefaultFormBuilder().withItemGroups([itemGroup]).build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder().withForms([form]).build()
        eventRepository.save(event)

        when: "I get the provenance of the item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withEventId(event.instanceId)
                .withItemId(item.instanceId)
                .build()
        Response response = getItemProvenance.get(request)

        then: "the provenance is returned"
        response.groups == []
        def items = response.items
        items.size() == 2
        items.any { it.label == "demographic" && it.value == "GENDER" }
        items.any { it.label == "value" && it.value == MALE_VALUE }
    }

    def "An assigned investigator can get the provenance of a laboratory item"() {
        given: "I am an assigned investigator"
        generateKnownSubject(aRandomStudyId(), SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "a laboratory item"
        ProvenanceLabValue labValue = insulinProvenance()
        PopulatedItem item = PopulatedItemObjectMother.aDefaultPopulatedItemBuilder().withDataPoint(labValue).build()
        and: "an event for a subject containing a form containing an item group containing the item"
        def itemGroup = PopulatedItemGroupObjectMother.aDefaultItemGroupBuilder().withItems([item]).build()
        def form = PopulatedFormObjectMother.aDefaultFormBuilder().withItemGroups([itemGroup]).build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder().withForms([form]).build()
        eventRepository.save(event)

        when: "I get the provenance of the item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withEventId(event.instanceId)
                .withItemId(item.instanceId)
                .build()
        Response response = getItemProvenance.get(request)

        then: "the provenance is returned"
        def groups = response.groups
        groups.size() == 2
        and: "the provenance has a group for concept"
        def conceptGroup = groups.find { it.label == "concept" }
        def conceptItems = conceptGroup.items
        conceptItems.size() == 5
        conceptItems.any { it.label == "code" && it.value == INSULIN_CODE }
        conceptItems.any { it.label == "component" && it.value == INSULIN_COMPONENT }
        conceptItems.any { it.label == "method" && it.value == INSULIN_METHOD }
        conceptItems.any { it.label == "fasting status" && it.value == "FASTING" }
        conceptItems.any { it.label == "specimen" && it.value == INSULIN_SPECIMEN }
        and: "the provenance has a group for measurement"
        def measurementGroup = groups.find { it.label == "measurement" }
        def measurementItems = measurementGroup.items
        measurementItems.size() == 4
        measurementItems.any { it.label == "value" && it.value == INSULIN_VALUE.toString() }
        measurementItems.any { it.label == "unit" && it.value == INSULIN_UNIT }
        measurementItems.any { it.label == "normal range lower limit" && it.value == INSULIN_LLN.toString() }
        measurementItems.any { it.label == "normal range upper limit" && it.value == INSULIN_ULN.toString() }
        and: "the provenance has items outside of a group"
        def items = response.items
        items.size() == 3
        items.any { it.label == "start date" && it.value == INSULIN_START_DATE.toString() }
        items.any { it.label == "end date" && it.value == INSULIN_END_DATE.toString() }
        items.any { it.label == "vendor" && it.value == INSULIN_VENDOR }
    }

    def "An assigned investigator can get the provenance of a vital sign item"() {
        given: "I am an assigned investigator"
        generateKnownSubject(aRandomStudyId(), SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "a vital sign item"
        ProvenanceVitalSign vitalSign = diastolicBloodPressureProvenance()
        PopulatedItem item = PopulatedItemObjectMother.aDefaultPopulatedItemBuilder().withDataPoint(vitalSign).build()
        and: "an event for a subject containing a form containing an item group containing the item"
        def itemGroup = PopulatedItemGroupObjectMother.aDefaultItemGroupBuilder().withItems([item]).build()
        def form = PopulatedFormObjectMother.aDefaultFormBuilder().withItemGroups([itemGroup]).build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder().withForms([form]).build()
        eventRepository.save(event)

        when: "I get the provenance of the item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withEventId(event.instanceId)
                .withItemId(item.instanceId)
                .build()
        Response response = getItemProvenance.get(request)

        then: "the provenance is returned"
        def groups = response.groups
        groups.size() == 2
        and: "the provenance has a group for concept"
        def conceptGroup = groups.find { it.label == "concept" }
        def conceptItems = conceptGroup.items
        conceptItems.size() == 5
        conceptItems.any { it.label == "code" && it.value == DIABP_CODE }
        conceptItems.any { it.label == "component" && it.value == DIABP_COMPONENT }
        conceptItems.any { it.label == "location code" && it.value == DIABP_LOCATION_ARM }
        conceptItems.any { it.label == "laterality code" && it.value == DIABP_LATERALITY_LEFT }
        conceptItems.any { it.label == "position code" && it.value == DIABP_POSITION_SITTING }
        and: "the provenance has a group for measurement"
        def measurementGroup = groups.find { it.label == "measurement" }
        def measurementItems = measurementGroup.items
        measurementItems.size() == 4
        measurementItems.any { it.label == "value" && it.value == DIABP_VALUE.toString() }
        measurementItems.any { it.label == "unit" && it.value == DIABP_UNIT }
        measurementItems.any { it.label == "normal range lower limit" && it.value == DIABP_LLN.toString() }
        measurementItems.any { it.label == "normal range upper limit" && it.value == DIABP_ULN.toString() }
        and: "the provenance has items outside of a group"
        def items = response.items
        items.size() == 1
        items.any { it.label == "date" && it.value == DIABP_TIMESTAMP.toString() }
    }

    def "An assigned investigator can get the provenance of a medication item"() {
        given: "I am an assigned investigator"
        generateKnownSubject(aRandomStudyId(), SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "a medication item"
        ProvenanceMedication medication = omeprazole()
        PopulatedItem item = PopulatedItemObjectMother.aDefaultPopulatedItemBuilder().withDataPoint(medication).build()
        and: "an event for a subject containing a form containing an item group containing the item"
        def itemGroup = PopulatedItemGroupObjectMother.aDefaultItemGroupBuilder().withItems([item]).build()
        def form = PopulatedFormObjectMother.aDefaultFormBuilder().withItemGroups([itemGroup]).build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder().withForms([form]).build()
        eventRepository.save(event)

        when: "I get the provenance of the item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withEventId(event.instanceId)
                .withItemId(item.instanceId)
                .build()
        Response response = getItemProvenance.get(request)

        then: "the provenance is returned"
        def groups = response.groups
        groups.size() == 2
        and: "the provenance has a group for concept"
        def conceptGroup = groups.find { it.label == "concept" }
        def conceptItems = conceptGroup.items
        conceptItems.size() == 2
        conceptItems.any { it.label == "name" && it.value == OMEPRAZOLE_NAME }
        conceptItems.any { it.label == "code" && it.value == OMEPRAZOLE_CODE }
        and: "the provenance has a group for dosage"
        def measurementGroup = groups.find { it.label == "dosage" }
        def measurementItems = measurementGroup.items
        measurementItems.size() == 2
        measurementItems.any { it.label == "value" && it.value == OMEPRAZOLE_DOSAGE_VALUE.toString() }
        measurementItems.any { it.label == "unit" && it.value == OMEPRAZOLE_DOSAGE_UNIT }
        and: "the provenance has items outside of a group"
        def items = response.items
        items.size() == 6
        items.any { it.label == "start date" && it.value == OMEPRAZOLE_START_DATE.toString() }
        items.any { it.label == "end date" && it.value == OMEPRAZOLE_END_DATE.toString() }
        items.any { it.label == "administration route" && it.value == OMEPRAZOLE_ADMINISTRATION_ROUTE_ORAL }
        items.any { it.label == "dose form" && it.value == OMEPRAZOLE_DOSE_FORM_CAPSULE }
        items.any { it.label == "dosing frequency" && it.value == OMEPRAZOLE_DOSING_FREQUENCY_DAILY }
        items.any { it.label == "event type" && it.value == OMEPRAZOLE_EVENT_TYPE.toString() }
    }

    @Unroll
    def "An assigned investigator can get the provenance of an item that lacks provenance information"(ProvenanceDataPoint provenanceDataPoint) {
        given: "I am an assigned investigator"
        generateKnownSubject(aRandomStudyId(), SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "an item that lacks provenance information"
        PopulatedItem item = PopulatedItemObjectMother.aDefaultPopulatedItemBuilder().withDataPoint(provenanceDataPoint).build()
        and: "an event for a subject containing a form containing an item group containing the item"
        def itemGroup = PopulatedItemGroupObjectMother.aDefaultItemGroupBuilder().withItems([item]).build()
        def form = PopulatedFormObjectMother.aDefaultFormBuilder().withItemGroups([itemGroup]).build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder().withForms([form]).build()
        eventRepository.save(event)

        when: "I get the provenance of the item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withEventId(event.instanceId)
                .withItemId(item.instanceId)
                .build()
        Response response = getItemProvenance.get(request)

        then: "empty provenance is returned"
        response.groups == []
        response.items == []

        where:
        provenanceDataPoint << [ProvenanceDemographicObjectMother.empty(),
                                ProvenanceLabValueObjectMother.empty(),
                                ProvenanceVitalSignObjectMother.empty(),
                                ProvenanceMedicationObjectMother.empty()]
    }

    def "I cannot get the provenance of an item that does not have provenance"() {
        given: "I am an assigned investigator"
        generateKnownSubject(aRandomStudyId(), SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "an item that does not have provenance"
        PopulatedItem item = PopulatedItemObjectMother.aDefaultPopulatedItemBuilder().withDataPoint(null).build()
        and: "an event for a subject containing a form containing an item group containing the item"
        def itemGroup = PopulatedItemGroupObjectMother.aDefaultItemGroupBuilder().withItems([item]).build()
        def form = PopulatedFormObjectMother.aDefaultFormBuilder().withItemGroups([itemGroup]).build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder().withForms([form]).build()
        eventRepository.save(event)

        when: "I get the provenance of the item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withEventId(event.instanceId)
                .withItemId(item.instanceId)
                .build()
        getItemProvenance.get(request)

        then: "an exception is thrown"
        def exception = thrown UserException
        exception.message == "Item does not have provenance information"
    }

    def "I cannot get the provenance of an unknown item"() {
        given: "I am an assigned investigator"
        generateKnownSubject(aRandomStudyId(), SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "an event for a subject"
        def event = aDefaultEvent()
        eventRepository.save(event)

        when: "I get the provenance of an item that does not exist in the subject's event"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withEventId(event.instanceId)
                .withItemId(ItemId.of("unknown"))
                .build()
        getItemProvenance.get(request)

        then: "an exception is thrown"
        def exception = thrown UserException
        exception.message == "Unknown item with id 'unknown' for subject '123-456'"
    }

    def "I cannot get the provenance of an item for an unknown subject"() {
        given: "a demographic item"
        ProvenanceDemographic demographic = male()
        PopulatedItem item = PopulatedItemObjectMother.aDefaultPopulatedItemBuilder().withDataPoint(demographic).build()
        and: "an event for a subject containing a form containing an item group containing the item"
        def itemGroup = PopulatedItemGroupObjectMother.aDefaultItemGroupBuilder().withItems([item]).build()
        def form = PopulatedFormObjectMother.aDefaultFormBuilder().withItemGroups([itemGroup]).build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder().withForms([form]).build()
        eventRepository.save(event)

        when: "I get the provenance of an item for an unknown subject"
        Request request = Request.newBuilder()
                .withSubjectId(SubjectId.of("unknown"))
                .withEventId(event.instanceId)
                .withItemId(item.instanceId)
                .build()
        getItemProvenance.get(request)

        then: "an exception is thrown"
        thrown AccessDeniedException
    }

    @Unroll
    def "I cannot get the provenance of an item with an invalid request"(SubjectId subjectId, EventId eventId, ItemId itemId, String subjectIdFieldWithError, String eventIdFieldWithError, String itemIdFieldWithError, String expectedError) {
        given: "I am an assigned investigator"
        subjectId && generateKnownSubject(aRandomStudyId(), subjectId, DATE_NOW, USER_ID_KNOWN)

        when: "I get the provenance of an item with subject id '#subjectId' and event id '#eventId' and item id '#itemId'"
        Request request = Request.newBuilder()
                .withSubjectId(subjectId)
                .withEventId(eventId)
                .withItemId(itemId)
                .build()
        getItemProvenance.get(request)

        then: "an exception is thrown"
        UseCaseConstraintViolationException exception = thrown(UseCaseConstraintViolationException)
        exception.constraintViolations.stream().anyMatch({ v -> v.field == subjectIdFieldWithError && v.message == expectedError })
        exception.constraintViolations.stream().anyMatch({ v -> v.field == eventIdFieldWithError && v.message == expectedError })
        exception.constraintViolations.stream().anyMatch({ v -> v.field == itemIdFieldWithError && v.message == expectedError })

        where:
        subjectId          | eventId          | itemId           | subjectIdFieldWithError | eventIdFieldWithError | itemIdFieldWithError | expectedError
        SubjectId.of("")   | EventId.of("")   | ItemId.of("")    | "subjectId.id"          | "eventId.id"          | "itemId.id"          | "must not be blank"
        SubjectId.of("  ") | EventId.of("  ") | ItemId.of("   ") | "subjectId.id"          | "eventId.id"          | "itemId.id"          | "must not be blank"
    }

    @Unroll
    def "I cannot get the provenance of an empty request"(SubjectId subjectId, EventId eventId, ItemId itemId) {
        given: "I am an assigned investigator"
        subjectId && generateKnownSubject(aRandomStudyId(), subjectId, DATE_NOW, USER_ID_KNOWN)

        when: "I get the provenance of an item with subject id '#subjectId' and event id '#eventId' and item id '#itemId'"
        Request request = Request.newBuilder()
                .withSubjectId(subjectId)
                .withEventId(eventId)
                .withItemId(itemId)
                .build()
        getItemProvenance.get(request)

        then: "Access is denied"
        AccessDeniedException e = thrown(AccessDeniedException)
        e.message == "User is not an assigned Investigator"

        where:
        subjectId | eventId | itemId
        null      | null    | null
    }

    def "I cannot get the provenance of an item for a subject for whom I am not an assigned investigator"() {
        given: "I am an assigned investigator"
        generateKnownSubject(aRandomStudyId(), SUBJECT_ID, DATE_NOW, USER_ID_OTHER)
        and: "a demographic item"
        ProvenanceDemographic demographic = male()
        PopulatedItem item = PopulatedItemObjectMother.aDefaultPopulatedItemBuilder().withDataPoint(demographic).build()
        and: "an event for a subject containing a form containing an item group containing the item"
        def itemGroup = PopulatedItemGroupObjectMother.aDefaultItemGroupBuilder().withItems([item]).build()
        def form = PopulatedFormObjectMother.aDefaultFormBuilder().withItemGroups([itemGroup]).build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder().withForms([form]).build()
        eventRepository.save(event)

        when: "I get the provenance of the item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withEventId(event.instanceId)
                .withItemId(item.instanceId)
                .build()
        getItemProvenance.get(request)

        then: "access is denied"
        thrown(AccessDeniedException)
    }

    def "I cannot get the provenance of an item for a subject as an unauthenticated user"() {
        given: "I am an assigned investigator"
        generateKnownSubject(aRandomStudyId(), SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "a demographic item"
        ProvenanceDemographic demographic = male()
        PopulatedItem item = PopulatedItemObjectMother.aDefaultPopulatedItemBuilder().withDataPoint(demographic).build()
        and: "an event for a subject containing a form containing an item group containing the item"
        def itemGroup = PopulatedItemGroupObjectMother.aDefaultItemGroupBuilder().withItems([item]).build()
        def form = PopulatedFormObjectMother.aDefaultFormBuilder().withItemGroups([itemGroup]).build()
        def event = PopulatedEventObjectMother.aDefaultEventBuilder().withForms([form]).build()
        eventRepository.save(event)
        and: "I am not authenticated"
        withoutAuthenticatedUser()

        when: "I get the provenance of the item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withEventId(event.instanceId)
                .withItemId(item.instanceId)
                .build()
        getItemProvenance.get(request)

        then: "access is denied"
        thrown(AccessDeniedException)
    }
}
