package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.provenance.model.*
import com.custodix.insite.local.ehr2edc.query.GetSubmittedItemProvenance
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItem
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedEventId
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedItemId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographicObjectMother.MALE_VALUE
import static com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographicObjectMother.male
import static com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceLabValueObjectMother.*
import static com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceMedicationObjectMother.*
import static com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceVitalSignObjectMother.*
import static com.custodix.insite.local.ehr2edc.query.GetSubmittedItemProvenance.Request
import static com.custodix.insite.local.ehr2edc.query.GetSubmittedItemProvenance.Response
import static com.custodix.insite.local.ehr2edc.submitted.SubmittedEventObjectMother.aDefaultSubmittedEventBuilder
import static com.custodix.insite.local.ehr2edc.submitted.SubmittedFormObjectMother.aDefaultSubmittedFormBuilder
import static com.custodix.insite.local.ehr2edc.submitted.SubmittedItemGroupObjectMother.aDefaultSubmittedItemGroupBuilder
import static com.custodix.insite.local.ehr2edc.submitted.SubmittedItemObjectMother.aDefaultSubmittedItemBuilder
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId

class GetSubmittedItemProvenanceSpec extends AbstractSpecification {

    private static final SubjectId SUBJECT_ID = SubjectId.of("subject-id-098")

    @Autowired
    GetSubmittedItemProvenance getSubmittedItemProvenance

    def "An assigned investigator can get the provenance of a demographic submitted item"() {
        given: "I am an assigned investigator"
        def studyId = aRandomStudyId()
        generateKnownSubject(studyId, SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "a demographic submitted item"
        ProvenanceDemographic demographic = male()
        SubmittedItem item = aDefaultSubmittedItemBuilder().withDataPoint(demographic).build()
        and: "a submitted event for a subject containing a submitted form containing a submitted item group containing the submitted item"
        def itemGroup = aDefaultSubmittedItemGroupBuilder().withSubmittedItems([item]).build()
        def form = aDefaultSubmittedFormBuilder().withSubmittedItemGroups([itemGroup]).build()
        def event = aDefaultSubmittedEventBuilder().withStudyId(studyId).withSubmittedForms([form]).build()
        submittedEventRepository.save(event)

        when: "I get the provenance of the submitted item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withSubmittedEventId(event.id)
                .withSubmittedItemId(item.instanceId)
                .build()
        Response response = getSubmittedItemProvenance.get(request)

        then: "the provenance is returned"
        response.groups == []
        def items = response.items
        items.size() == 2
        items.any { it.label == "demographic" && it.value == "GENDER" }
        items.any { it.label == "value" && it.value == MALE_VALUE }
    }

    def "An assigned investigator can get the provenance of a laboratory submitted item"() {
        given: "I am an assigned investigator"
        def studyId = aRandomStudyId()
        generateKnownSubject(studyId, SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "a laboratory submitted item"
        ProvenanceLabValue labValue = insulinProvenance()
        SubmittedItem item = aDefaultSubmittedItemBuilder().withDataPoint(labValue).build()
        and: "A submitted event for a subject containing a submitted form containing a submitted item group containing the submitted item"
        def itemGroup = aDefaultSubmittedItemGroupBuilder().withSubmittedItems([item]).build()
        def form = aDefaultSubmittedFormBuilder().withSubmittedItemGroups([itemGroup]).build()
        def event = aDefaultSubmittedEventBuilder().withStudyId(studyId).withSubmittedForms([form]).build()
        submittedEventRepository.save(event)

        when: "I get the provenance of the submitted item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withSubmittedEventId(event.id)
                .withSubmittedItemId(item.instanceId)
                .build()
        Response response = getSubmittedItemProvenance.get(request)

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

    def "An assigned investigator can get the provenance of a vital sign submitted item"() {
        given: "I am an assigned investigator"
        def studyId = aRandomStudyId()
        generateKnownSubject(studyId, SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "a vital sign submitted item"
        ProvenanceVitalSign vitalSign = diastolicBloodPressureProvenance()
        SubmittedItem item = aDefaultSubmittedItemBuilder().withDataPoint(vitalSign).build()
        and: "A submitted event for a subject containing a submitted form containing a submitted item group containing the submitted item"
        def itemGroup = aDefaultSubmittedItemGroupBuilder().withSubmittedItems([item]).build()
        def form = aDefaultSubmittedFormBuilder().withSubmittedItemGroups([itemGroup]).build()
        def event = aDefaultSubmittedEventBuilder().withStudyId(studyId).withSubmittedForms([form]).build()
        submittedEventRepository.save(event)

        when: "I get the provenance of the submitted item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withSubmittedEventId(event.id)
                .withSubmittedItemId(item.instanceId)
                .build()
        Response response = getSubmittedItemProvenance.get(request)

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

    def "An assigned investigator can get the provenance of a medication submitted item"() {
        given: "I am an assigned investigator"
        def studyId = aRandomStudyId()
        generateKnownSubject(studyId, SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "a medication submitted item"
        ProvenanceMedication medication = omeprazole()
        SubmittedItem item = aDefaultSubmittedItemBuilder().withDataPoint(medication).build()
        and: "A submitted event for a subject containing a submitted form containing a submitted item group containing the submitted item"
        def itemGroup = aDefaultSubmittedItemGroupBuilder().withSubmittedItems([item]).build()
        def form = aDefaultSubmittedFormBuilder().withSubmittedItemGroups([itemGroup]).build()
        def event = aDefaultSubmittedEventBuilder().withStudyId(studyId).withSubmittedForms([form]).build()
        submittedEventRepository.save(event)

        when: "I get the provenance of the submitted item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withSubmittedEventId(event.id)
                .withSubmittedItemId(item.instanceId)
                .build()
        Response response = getSubmittedItemProvenance.get(request)

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
    def "An assigned investigator can get the provenance of a submitted item that lacks provenance information"(ProvenanceDataPoint provenanceDataPoint) {
        given: "I am an assigned investigator"
        def studyId = aRandomStudyId()
        generateKnownSubject(studyId, SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "a submitted item that lacks provenance information"
        SubmittedItem item = aDefaultSubmittedItemBuilder().withDataPoint(provenanceDataPoint).build()
        and: "A submitted event for a subject containing a submitted form containing a submitted item group containing the submitted item"
        def itemGroup = aDefaultSubmittedItemGroupBuilder().withSubmittedItems([item]).build()
        def form = aDefaultSubmittedFormBuilder().withSubmittedItemGroups([itemGroup]).build()
        def event = aDefaultSubmittedEventBuilder().withStudyId(studyId).withSubmittedForms([form]).build()
        submittedEventRepository.save(event)

        when: "I get the provenance of the submitted item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withSubmittedEventId(event.id)
                .withSubmittedItemId(item.instanceId)
                .build()
        Response response = getSubmittedItemProvenance.get(request)

        then: "empty provenance is returned"
        response.groups == []
        response.items == []

        where:
        provenanceDataPoint << [ProvenanceDemographicObjectMother.empty(),
                                ProvenanceLabValueObjectMother.empty(),
                                ProvenanceVitalSignObjectMother.empty(),
                                ProvenanceMedicationObjectMother.empty()]
    }

    def "I cannot get the provenance of a submitted item that does not have provenance"() {
        given: "I am an assigned investigator"
        def studyId = aRandomStudyId()
        generateKnownSubject(studyId, SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "a submitted item that does not have provenance"
        SubmittedItem item = aDefaultSubmittedItemBuilder().withDataPoint(null).build()
        and: "A submitted event for a subject containing a submitted form containing a submitted item group containing the submitted item"
        def itemGroup = aDefaultSubmittedItemGroupBuilder().withSubmittedItems([item]).build()
        def form = aDefaultSubmittedFormBuilder().withSubmittedItemGroups([itemGroup]).build()
        def event = aDefaultSubmittedEventBuilder().withStudyId(studyId).withSubmittedForms([form]).build()
        submittedEventRepository.save(event)

        when: "I get the provenance of the submitted item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withSubmittedEventId(event.id)
                .withSubmittedItemId(item.instanceId)
                .build()
        getSubmittedItemProvenance.get(request)

        then: "an exception is thrown"
        def exception = thrown UserException
        exception.message == "Submitted item does not have provenance information"
    }

    def "I cannot get the provenance of an unknown submitted item"() {
        given: "I am an assigned investigator"
        def studyId = aRandomStudyId()
        generateKnownSubject(studyId, SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "A submitted event for a subject"
        def event = aDefaultSubmittedEventBuilder().withStudyId(studyId).build()
        submittedEventRepository.save(event)

        when: "I get the provenance of a submitted item that does not exist in the subject's submitted event"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withSubmittedEventId(event.id)
                .withSubmittedItemId(SubmittedItemId.of("unknown"))
                .build()
        getSubmittedItemProvenance.get(request)

        then: "an exception is thrown"
        def exception = thrown UserException
        exception.message == "Unknown submitted item with id 'unknown' in submitted event 'SubmittedEventId'"
    }

    def "I cannot get the provenance of a submitted item for an unknown subject"() {
        given: "a demographic submitted item"
        ProvenanceDemographic demographic = male()
        SubmittedItem item = aDefaultSubmittedItemBuilder().withDataPoint(demographic).build()
        and: "A submitted event for a subject containing a submitted form containing a submitted item group containing the submitted item"
        def itemGroup = aDefaultSubmittedItemGroupBuilder().withSubmittedItems([item]).build()
        def form = aDefaultSubmittedFormBuilder().withSubmittedItemGroups([itemGroup]).build()
        def event = aDefaultSubmittedEventBuilder().withSubmittedForms([form]).build()
        submittedEventRepository.save(event)

        when: "I get the provenance of a submitted item for an unknown subject"
        Request request = Request.newBuilder()
                .withSubjectId(SubjectId.of("unknown"))
                .withSubmittedEventId(event.id)
                .withSubmittedItemId(item.instanceId)
                .build()
        getSubmittedItemProvenance.get(request)

        then: "an exception is thrown"
        thrown AccessDeniedException
    }

    @Unroll
    def "I cannot get the provenance of a submitted item with an invalid request"(SubjectId subjectId, SubmittedEventId submittedEventId, SubmittedItemId submittedItemId, String subjectIdFieldWithError, String submittedEventIdFieldWithError, String submittedItemIdFieldWithError, String expectedError) {
        given: "I am an assigned investigator"
        def studyId = aRandomStudyId()
        subjectId && generateKnownSubject(studyId, subjectId, DATE_NOW, USER_ID_KNOWN)

        when: "I get the provenance of a submitted item with subject id '#subjectId' and event id '#eventId' and item id '#itemId'"
        Request request = Request.newBuilder()
                .withSubjectId(subjectId)
                .withSubmittedEventId(submittedEventId)
                .withSubmittedItemId(submittedItemId)
                .build()
        getSubmittedItemProvenance.get(request)

        then: "an exception is thrown"
        Exception e = thrown(Exception)
        e.message ==  "User is not an assigned Investigator"


        where:
        subjectId          | submittedEventId          | submittedItemId           | subjectIdFieldWithError | submittedEventIdFieldWithError | submittedItemIdFieldWithError | expectedError
        null               | null                      | null                      | "subjectId"             | "submittedEventId"             | "submittedItemId"             | "must not be null"
    }

    def "I cannot get the provenance of a submitted item for a subject for whom I am not an assigned investigator"() {
        given: "a demographic submitted item"
        ProvenanceDemographic demographic = male()
        SubmittedItem item = aDefaultSubmittedItemBuilder().withDataPoint(demographic).build()
        and: "a submitted event for a subject containing a submitted form containing a submitted item group containing the submitted item"
        def itemGroup = aDefaultSubmittedItemGroupBuilder().withSubmittedItems([item]).build()
        def form = aDefaultSubmittedFormBuilder().withSubmittedItemGroups([itemGroup]).build()
        def event = aDefaultSubmittedEventBuilder().withSubmittedForms([form]).build()
        submittedEventRepository.save(event)
        and: "No user is authenticated"
        withoutAuthenticatedUser()

        when: "I get the provenance of the submitted item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withSubmittedEventId(event.id)
                .withSubmittedItemId(item.instanceId)
                .build()
        getSubmittedItemProvenance.get(request)

        then: "an exception is thrown"
        AccessDeniedException e = thrown AccessDeniedException
        e.message == "User is not an assigned Investigator"
    }

    def "I cannot get the provenance of a submitted item for a subject as unauthenticated user"() {
        given: "I am not assigned as investigator"
        generateKnownSubject(aRandomStudyId(), SUBJECT_ID, DATE_NOW, USER_ID_KNOWN)
        and: "a demographic submitted item"
        ProvenanceDemographic demographic = male()
        SubmittedItem item = aDefaultSubmittedItemBuilder().withDataPoint(demographic).build()
        and: "a submitted event for a subject containing a submitted form containing a submitted item group containing the submitted item"
        def itemGroup = aDefaultSubmittedItemGroupBuilder().withSubmittedItems([item]).build()
        def form = aDefaultSubmittedFormBuilder().withSubmittedItemGroups([itemGroup]).build()
        def event = aDefaultSubmittedEventBuilder().withSubmittedForms([form]).build()
        submittedEventRepository.save(event)
        and: "No user is authenticated"
        withoutAuthenticatedUser()

        when: "I get the provenance of the submitted item"
        Request request = Request.newBuilder()
                .withSubjectId(SUBJECT_ID)
                .withSubmittedEventId(event.id)
                .withSubmittedItemId(item.instanceId)
                .build()
        getSubmittedItemProvenance.get(request)

        then: "an exception is thrown"
        AccessDeniedException e = thrown AccessDeniedException
        e.message == "User is not an assigned Investigator"
    }
}
