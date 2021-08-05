package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.DomainTime
import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.command.PopulateEvent
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent
import com.custodix.insite.local.ehr2edc.populator.PopulatedForm
import com.custodix.insite.local.ehr2edc.populator.PopulatedItem
import com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroup
import com.custodix.insite.local.ehr2edc.provenance.model.*
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionStep
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.model.DemographicDocumentObjectMother
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabConceptObjectMother
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabMeasurementObjectMother
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabValueDocumentObjectMother
import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationConceptFieldObjectMother
import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationDocumentObjectMother
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignConceptFieldObjectMother
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignDocumentObjectMother
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model.VitalSignMeasurementFieldObjectMother
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.snapshots.*
import com.custodix.insite.local.ehr2edc.vocabulary.*
import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.provenance.model.DemographicType.GENDER
import static com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshotObjectMother.aDefaultEventDefinitionSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.FormDefinitionSnapshotObjectMother.aDefaultFormDefinitionSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.ItemDefinitionSnapshotObjectMother.aDefaultItemDefinitionBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.ItemGroupDefinitionSnapshotObjectMother.aDefaultItemGroupDefinitionBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.MetadataDefinitionSnapshotObjectMother.aDefaultMetaDataDefinitionSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.MetadataDefinitionSnapshotObjectMother.aMetaDataDefinitionSnapshotWithEventDefinitions
import static com.custodix.insite.local.ehr2edc.snapshots.StudySnapshotObjectMother.aDefaultStudySnapshotBuilder
import static com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.vocabulary.EHRRegistrationStatus.NOT_REGISTERED
import static com.custodix.insite.local.ehr2edc.vocabulary.EHRRegistrationStatus.REGISTERED
import static java.util.Collections.emptyList
import static java.util.Collections.singletonList

class PopulateEventSpec extends AbstractSpecification {

    private static final StudyId STUDY_ID = StudyId.of("STUDY")
    private static final SubjectId SUBJECT_ID = SubjectId.of("SUBJ-1")
    private static final EventDefinitionId EVENT_DEFINITION_ID = EventDefinitionId.of("EventDefinitionId-1")
    private static final String EVENT_PARENT_ID = "parent"
    private static final ItemDefinitionId ITEM_ID = ItemDefinitionId.of("item-123")
    public static final EDCSubjectReference EDC_SUBJECT_REFERENCE = EDCSubjectReference.of("EDCSubjectReference-123")

    private static final String DEMOGRAPHIC_ITEM_QUERY_MAPPING_FILE = "com/custodix/insite/local/ehr2edc/application/PopulateFormsPopulateSpec_DemographicQueryMapping.json"
    private static final String LAB_VALUE_ITEM_QUERY_MAPPING_FILE = "com/custodix/insite/local/ehr2edc/application/PopulateFormsPopulateSpec_LabValueQueryMapping.json"
    private static final String VITAL_SIGN_ITEM_QUERY_MAPPING_FILE = "com/custodix/insite/local/ehr2edc/application/PopulateFormsPopulateSpec_VitalSignQueryMapping.json"
    private static final String MEDICATION_ITEM_QUERY_MAPPING_FILE = "com/custodix/insite/local/ehr2edc/application/PopulateFormsPopulateSpec_MedicationQueryMapping.json"
    private static final String ITEM_GROUP_NAME = "item-group-name-123-567"
    private static final ItemGroupDefinitionId ITEM_GROUP_ID = ItemGroupDefinitionId.of("item-group-id-123-567")
    private static final String ITEM_NAME = "item-name-123-567"
    private static final String FORM_NAME = "form-name-123-098"
    private static final String ITEM_LABEL_FRENCH = "french-label"
    private static final String ITEM_LABEL_ENGLISH = "english-label"
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("")
    public static final LocalDate REFERENCE_DATE = LocalDate.of(2018, 12, 10)
    public static final String EVENT_NAME = "event-name-987-987"
    private static final LabName LOCAL_LAB = LabName.of("LAB_1")

    @Autowired
    private PopulateEvent populateEvent

    def "I can populate an event for all types of data points"() {
        given: "a study with an event with a form with an item group with an item for each type of data point"
        createStudyWithItemForEachDataPointType()
        and: "subject has a demographic data point with gender '#demographicValue'"
        aDemographic()
        and: "subject has a laboratory data point with specimen '#laboratoryValue'"
        aLabValue()
        and: "subject has a vitalsign data point with measurement value '#vitalSignValue'"
        aVitalSign()
        and: "subject has a medication data point with name '#medicationValue'"
        aMedication()
        and: "a request to populate the event"
        PopulateEvent.Request request = createRequest()

        when: "I populate the event"
        populateEvent.populate(request)

        then: "The populated event is saved"
        PopulatedEvent event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId()).get()
        and: "The form is populated"
        event.populatedForms.size() == 1
        PopulatedForm form = event.populatedForms.get(0)
        and: "The item group is populated with an item for each type of data point"
        form.itemGroups.size() == 1
        PopulatedItemGroup itemGroup = form.itemGroups.get(0)
        List<PopulatedItem> items = itemGroup.items
        items.size() == 4
        and: "The demographic item is populated with value '#demographicValue'"
        PopulatedItem dmItem = items.find({ it.id.id == "DM" })
        dmItem.value == new LabeledValue(demographicValue)
        assertProvenanceDemographic(dmItem.dataPoint as ProvenanceDemographic)
        assertProjectionStepsDemographic(dmItem.projectionSteps)
        and: "The laboratory item is populated with value '#laboratoryValue'"
        PopulatedItem lbItem = items.find({ it.id.id == "LB" })
        lbItem.value == new LabeledValue(laboratoryValue)
        assertProvenanceLabValue(lbItem.dataPoint as ProvenanceLabValue)
        assertProjectionStepsLabValue(lbItem.projectionSteps)
        and: "The vital sign item is populated with value '#vitalSignValue'"
        PopulatedItem vsItem = items.find({ it.id.id == "VS" })
        vsItem.value == new LabeledValue(vitalSignValue)
        assertProvenanceVitalSign(vsItem.dataPoint as ProvenanceVitalSign)
        assertProjectionStepsVitalSign(vsItem.projectionSteps)
        and: "The medication item is populated with value '#medicationValue'"
        PopulatedItem medItem = items.find({ it.id.id == "MED" })
        medItem.value == new LabeledValue(medicationValue)
        assertProvenanceMedication(medItem.dataPoint as ProvenanceMedication)
        assertProjectionStepsMedication(medItem.projectionSteps)

        where:
        demographicValue | laboratoryValue | vitalSignValue | medicationValue
        "F"              | "Blood"         | "18.5"         | "omeprazole"
    }

    def "Only authenticated users can populate an event"(StudyId studyId, SubjectId subjectId, EventDefinitionId eventDefinitionId, String itemId) {
        given: "a study"
        createStudy(USER_ID_KNOWN)
        and: "no user is authenticated"
        withoutAuthenticatedUser()
        and: "a request to populate the study"
        PopulateEvent.Request request = createRequest()

        when: "populate event"
        populateEvent.populate(request)

        then: "An access denied exception is thrown"
        def ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"

        where:
        studyId  | subjectId  | eventDefinitionId   | itemId
        STUDY_ID | SUBJECT_ID | EVENT_DEFINITION_ID | ITEM_ID
    }

    def "Only an assigned investigator can populate an event"(StudyId studyId, SubjectId subjectId, EventDefinitionId eventDefinitionId, String itemId) {
        given: "a study"
        createStudy(USER_ID_OTHER)
        and: "a request to populate the study"
        PopulateEvent.Request request = createRequest()

        when: "populate event"
        populateEvent.populate(request)

        then: "An access denied exception is thrown"
        def ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"

        where:
        studyId  | subjectId  | eventDefinitionId   | itemId
        STUDY_ID | SUBJECT_ID | EVENT_DEFINITION_ID | ITEM_ID
    }

    def "No forms are populated if there are no populated items"() {
        given: "a study"
        createStudy(USER_ID_KNOWN)

        and: "A valid request for which no data is available"
        PopulateEvent.Request request = createRequest()

        when: "I populate an event"
        populateEvent.populate(request)

        then: "No populated form were saved on the event"
        def event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        event.present
        event.get().populatedForms.isEmpty()
    }

    def "Set population time correctly"(StudyId studyId, SubjectId subjectId, EventDefinitionId eventDefinitionId, String itemId) {
        given: "an existing study with studyId '#studyId', subjectId '#subjectId', eventDefinitionId '#eventDefinitionId' and itemId '#itemId'"
        createStudy(USER_ID_KNOWN)
        aDemographic()
        and: "A request with studyId '#studyId', subjectId '#subjectId', eventDefinitionId '#eventDefinitionId'"
        PopulateEvent.Request request = createRequest()

        when: "populate an event"
        populateEvent.populate(request)

        then: "The populated event is saved"
        def event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        event.present
        and: "populated time has been set to now"
        event.get().getPopulationTime() == DomainTime.now()

        where:
        studyId  | subjectId  | eventDefinitionId   | itemId
        STUDY_ID | SUBJECT_ID | EVENT_DEFINITION_ID | ITEM_ID
    }

    def "Set EDC subject reference correctly"(
            StudyId studyId,
            SubjectId subjectId,
            EventDefinitionId eventDefinitionId,
            EDCSubjectReference edcSubjectReference) {
        given:
        "An existing study with " +
                "studyId '#studyId', " +
                "subjectId '#subjectId', " +
                "eventDefinitionId '#eventDefinitionId', " +
                "EDCSubjectReference '#EDCSubjectReference' "
        createStudy(USER_ID_KNOWN)
        aDemographic()
        and: "A request with studyId '#studyId', subjectId '#subjectId', eventDefinitionId '#eventDefinitionId'"
        PopulateEvent.Request request = createRequest()

        when: "populate an event"
        populateEvent.populate(request)

        then: "The populated event is saved"
        def event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        event.present
        and: "edcSubjectReference is set correctly to '#EDCSubjectReference'"
        event.get().getEdcSubjectReference() == edcSubjectReference

        where:
        studyId  | subjectId  | eventDefinitionId   | edcSubjectReference
        STUDY_ID | SUBJECT_ID | EVENT_DEFINITION_ID | EDC_SUBJECT_REFERENCE
    }

    def "Set event properties correctly"(
            StudyId studyId,
            SubjectId subjectId,
            EventDefinitionId eventDefinitionId,
            String eventName) {
        given: "I am signed in as a user with identifier '#USER_ID_KNOWN.id'"
        withCurrentUserHavingId(USER_ID_KNOWN)

        and:
        "An existing study with " +
                "studyId '#studyId', " +
                "subjectId '#subjectId', " +
                "eventDefinitionId '#eventDefinitionId', " +
                "event name '#eventName'"
        createStudy(USER_ID_KNOWN)
        aDemographic()
        and: "A request with studyId '#studyId', subjectId '#subjectId', eventDefinitionId '#eventDefinitionId'"
        PopulateEvent.Request request = createRequest()

        when: "populate an event"
        populateEvent.populate(request)

        then: "The populated event is saved"
        def event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        event.present
        with(event.get()) {
            it.name == eventName
            isEventIdALongValue(it)
            it.eventParentId == EVENT_PARENT_ID
            it.studyId == studyId
            it.subjectId == subjectId
            it.edcSubjectReference == EDC_SUBJECT_REFERENCE
            it.referenceDate == REFERENCE_DATE
            it.getPopulationTime() == DomainTime.now()
            it.populatedForms.size() == 1
            it.populator.present
            it.populator.get() == USER_ID_KNOWN
        }

        where:
        studyId  | subjectId  | eventDefinitionId   | eventName
        STUDY_ID | SUBJECT_ID | EVENT_DEFINITION_ID | EVENT_NAME
    }

    def "Set form properties correctly"(
            String formName,
            LabName localLab) {
        given:
        "An existing study with " +
                "form name '#formName', " +
                "local lab '#localLab'"
        createStudy(USER_ID_KNOWN)
        aDemographic()
        and: "A request"
        PopulateEvent.Request request = createRequest()

        when: "populate an event"
        populateEvent.populate(request)

        then: "The populated event was saved"
        def eventOptional = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        eventOptional.present
        def event = eventOptional.get()
        def form = event.populatedForms[0]
        and: "formName is set correctly to '#formName'"
        form.name == FORM_NAME
        and: "local lab is set correctly to '#localLab'"
        form.localLab == localLab

        where:
        formName  | localLab
        FORM_NAME | LOCAL_LAB
    }

    def "I can populate a form that maps the subject's consent date"() {
        given:
        "An existing study with " +
                "form name '#formName', " +
                "a subject with as consent date '#consentDate'"
        def studySnapshot = StudySnapshotObjectMother.aDefaultStudySnapshotBuilder(UserIdentifier.of("42"))
                .withStudyId(STUDY_ID)
                .withSubjects(singletonList(createSubject(consentDate)))
                .withMetadata(aMetaDataDefinitionSnapshotWithEventDefinitions(createEventDefinitionSnapshot()))
                .withItemQueryMappings(Collections.singletonMap(
                        ITEM_ID,
                        ItemQueryMappingSnapshot.newBuilder().withValue('{\n' +
                                '  "query": {' +
                                '    "type": "demographic",' +
                                '    "criteria": { "criteria": [] }' +
                                '  },' +
                                '  "projectors": [' +
                                '    {"type": "getConsentDate"},' +
                                '    {"type": "dateToString", "pattern": "yyyy-MM-dd"}' +
                                '  ]' +
                                '}').build()
                )).build()
        studyRepository.save(Study.restoreSnapshot(studySnapshot))
        aDemographic()
        and: "A request"
        PopulateEvent.Request request = createRequest()

        when: "populate an event"
        populateEvent.populate(request)

        then: "The populated event was saved"
        def eventOptional = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        eventOptional.present
        def event = eventOptional.get()
        def form = event.populatedForms[0]
        and: "formName is set correctly to '#formName'"
        form.name == FORM_NAME
        and: "populated item has the consent date"
        PopulatedItem item = getPopulatedItem(form, ITEM_GROUP_ID, ITEM_ID)
        item.value.value == expectedConsentDate

        where:
        formName  | consentDate                   | expectedConsentDate
        FORM_NAME | LocalDate.parse("2019-10-01") | "2019-10-01"
    }

    def "i cannot populate when the study does not have event definitions"() {
        given: "a study without event definitions"
        def studySnapshot = aDefaultStudySnapshotBuilder(USER_ID_KNOWN)
                .withStudyId(STUDY_ID)
                .withSubjects(singletonList(aDefaultSubjectSnapshotBuilder().withSubjectId(SUBJECT_ID).withEHRRegistrationStatus(REGISTERED).build()))
                .withMetadata(aDefaultMetaDataDefinitionSnapshotBuilder().withEventDefinitions(emptyList()).build())
                .build()
        studyRepository.save(Study.restoreSnapshot(studySnapshot))
        and: "A request"
        PopulateEvent.Request request = createRequest()

        when: "populate an event"
        populateEvent.populate(request)

        then: "an domain exception is thrown"
        def userException = thrown(UserException)
        userException.message == "Study 'STUDY' has no events defined, please ensure that the study has events defined before populating."
    }

    def "i cannot populate the subject when the subject is not registered in EHR"() {
        given: "a study with subject 'SUBJ-1' not registered in EHR"
        def studySnapshot = aDefaultStudySnapshotBuilder(UserIdentifier.of("42"))
                .withStudyId(STUDY_ID)
                .withSubjects(singletonList(aDefaultSubjectSnapshotBuilder().withSubjectId(SUBJECT_ID).withEHRRegistrationStatus(NOT_REGISTERED).build()))
                .withMetadata(aMetaDataDefinitionSnapshotWithEventDefinitions(createEventDefinitionSnapshot()))
                .build()
        studyRepository.save(Study.restoreSnapshot(studySnapshot))
        and: "A request to populate subject 'SUBJ-1'"
        PopulateEvent.Request request = createRequest()

        when: "populate an event"
        populateEvent.populate(request)

        then: "an userException is thrown"
        def userException = thrown(UserException)
        userException.message == "The events are currently not ready for data point fetching, please refresh the page to get the latest status."
    }

    private PopulatedItem getPopulatedItem(PopulatedForm form, ItemGroupDefinitionId item_group_id, ItemDefinitionId item_id) {
        PopulatedItemGroup itemGroup = getPopulatedItemGroup(form, item_group_id)
        return getPopulatedItem(itemGroup, item_id)
    }

    private PopulatedItemGroup getPopulatedItemGroup(PopulatedForm form, ItemGroupDefinitionId item_group_id) {
        form.getItemGroups().stream().filter({ group -> group.getDefinition().getId() == item_group_id }).findFirst().get()
    }

    private PopulatedItem getPopulatedItem(PopulatedItemGroup itemGroup, ItemDefinitionId item_id) {
        itemGroup.getItems().stream().filter({ item -> item.getId() == item_id }).findFirst().get()
    }

    def isEventIdALongValue(PopulatedEvent event) {
        try {
            Long.parseLong(event.instanceId.id)
            true
        } catch (Exception e) {
            false
        }
    }

    def "Set item group name correctly"(
            StudyId studyId,
            SubjectId subjectId,
            EventDefinitionId eventDefinitionId,
            String itemGroupName) {
        given:
        "An existing study with " +
                "studyId '#studyId', " +
                "subjectId '#subjectId', " +
                "eventDefinitionId '#eventDefinitionId', " +
                "item group name '#itemGroupName'"
        createStudy(USER_ID_KNOWN)
        aDemographic()
        and: "A request with studyId '#studyId', subjectId '#subjectId', eventDefinitionId '#eventDefinitionId'"
        PopulateEvent.Request request = createRequest()

        when: "populate an event"
        populateEvent.populate(request)

        then: "The populated event was saved"
        def event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        event.present
        def form = event.get().populatedForms[0]
        and: "itemGroupName is set correctly to '#itemGroupName'"
        form.getItemGroups().size() == 1
        form.getItemGroups()[0].name == ITEM_GROUP_NAME

        where:
        studyId  | subjectId  | eventDefinitionId   | itemGroupName
        STUDY_ID | SUBJECT_ID | EVENT_DEFINITION_ID | ITEM_GROUP_NAME
    }

    def "Set item name correctly"(
            StudyId studyId,
            SubjectId subjectId,
            EventDefinitionId eventDefinitionId,
            String itemId,
            String itemLabel) {
        given:
        "An existing study with " +
                "studyId '#studyId', " +
                "subjectId '#subjectId', " +
                "eventDefinitionId '#eventDefinitionId', " +
                "item label '#itemLabel' " +
                "and itemId '#itemId'"
        createStudy(USER_ID_KNOWN)
        aDemographic()
        and: "A request with studyId '#studyId', subjectId '#subjectId', eventDefinitionId '#eventDefinitionId'"
        PopulateEvent.Request request = createRequest()

        when: "populate an event"
        populateEvent.populate(request)

        then: "The populated event was saved"
        def event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        event.present
        def form = event.get().populatedForms[0]
        and: "itemLabel is set correctly to '#itemLabel'"
        form.getItemGroups().size() == 1
        form.getItemGroups()[0].getItems().size() == 1
        form.getItemGroups()[0].getItems()[0].getDisplayLabel(DEFAULT_LOCALE) == itemLabel

        where:
        studyId  | subjectId  | eventDefinitionId   | itemId  | itemLabel
        STUDY_ID | SUBJECT_ID | EVENT_DEFINITION_ID | ITEM_ID | ITEM_NAME
    }

    def "Set item labels correctly"(
            String locale,
            String itemLabel) {
        given: "An existing study with a form"
        createStudy(USER_ID_KNOWN)
        aDemographic()

        and: "An event population request"
        PopulateEvent.Request request = createRequest()

        when: "an event is populated"
        populateEvent.populate(request)

        then: "The populated event was saved"
        def event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        event.present
        def form = event.get().populatedForms[0]
        and: "itemLabel in '#locale' is set correctly to '#itemLabel'"
        form.getItemGroups()[0].getItems()[0].getDisplayLabel(Locale.forLanguageTag(locale)) == itemLabel

        where:
        locale                         | itemLabel
        Locale.FRENCH.toLanguageTag()  | ITEM_LABEL_FRENCH
        Locale.ENGLISH.toLanguageTag() | ITEM_LABEL_ENGLISH
    }

    private static void assertProvenanceDemographic(ProvenanceDemographic provenanceDemographic) {
        assert provenanceDemographic.value == DemographicDocumentObjectMother.GENDER_FEMALE
        assert provenanceDemographic.demographicType == GENDER
    }

    private static void assertProjectionStepsDemographic(List<ProjectionStep> steps) {
        assert steps.size() == 2
        assert steps*.projector == ["Fact to gender", "Gender to SDTM-code"]
    }

    private static void assertProvenanceLabValue(ProvenanceLabValue provenanceLabValue) {
        assert provenanceLabValue.labConcept.concept.code == LabConceptObjectMother.INSULIN_CODE
        assert provenanceLabValue.labConcept.component == LabConceptObjectMother.INSULIN_COMPONENT
        assert provenanceLabValue.labConcept.method == LabConceptObjectMother.INSULIN_METHOD
        assert provenanceLabValue.labConcept.fastingStatus == FastingStatus.valueOf(LabConceptObjectMother.INSULIN_FASTING_STATUS)
        assert provenanceLabValue.labConcept.specimen == LabConceptObjectMother.INSULIN_SPECIMEN
        assert provenanceLabValue.startDate == LabValueDocumentObjectMother.INSULIN_START_DATE
        assert provenanceLabValue.endDate == LabValueDocumentObjectMother.INSULIN_END_DATE
        assert provenanceLabValue.quantitativeResult.value == LabMeasurementObjectMother.INSULIN_VALUE
        assert provenanceLabValue.quantitativeResult.unit == LabMeasurementObjectMother.INSULIN_UNIT
        assert provenanceLabValue.quantitativeResult.lowerLimit == LabMeasurementObjectMother.INSULIN_LLN
        assert provenanceLabValue.quantitativeResult.upperLimit == LabMeasurementObjectMother.INSULIN_ULN
        assert provenanceLabValue.vendor == LabValueDocumentObjectMother.INSULIN_VENDOR
    }

    private static void assertProjectionStepsLabValue(List<ProjectionStep> steps) {
        assert steps.size() == 2
        assert steps*.projector == ["Project to ProjectedValue", "ProjectedValue to FormItem"]
    }

    private static void assertProvenanceVitalSign(ProvenanceVitalSign provenanceVitalSign) {
        assert provenanceVitalSign.concept.concept.code == VitalSignConceptFieldObjectMother.BMI_CONCEPT_CODE
        assert provenanceVitalSign.concept.component == VitalSignConceptFieldObjectMother.BMI_COMPONENT
        assert provenanceVitalSign.concept.location == VitalSignConceptFieldObjectMother.BMI_LOCATION
        assert provenanceVitalSign.concept.laterality == VitalSignConceptFieldObjectMother.BMI_LATERALITY
        assert provenanceVitalSign.concept.position == VitalSignConceptFieldObjectMother.BMI_POSITION
        assert provenanceVitalSign.effectiveDateTime == VitalSignDocumentObjectMother.BMI_EFFECTIVE_DATE
        assert provenanceVitalSign.measurement.value == VitalSignMeasurementFieldObjectMother.BMI_VALUE
        assert provenanceVitalSign.measurement.unit == VitalSignMeasurementFieldObjectMother.BMI_UNIT
        assert provenanceVitalSign.measurement.lowerLimit == VitalSignMeasurementFieldObjectMother.BMI_LLN
        assert provenanceVitalSign.measurement.upperLimit == VitalSignMeasurementFieldObjectMother.BMI_ULN
    }

    private static void assertProjectionStepsVitalSign(List<ProjectionStep> steps) {
        assert steps.size() == 2
        assert steps*.projector == ["Project to ProjectedValue", "ProjectedValue to FormItem"]
    }

    private static void assertProvenanceMedication(ProvenanceMedication provenanceMedication) {
        assert provenanceMedication.concept.concept.code == MedicationConceptFieldObjectMother.OMEPRAZOLE_CONCEPT_CODE
        assert provenanceMedication.concept.name == MedicationConceptFieldObjectMother.OMEPRAZOLE_NAME
        assert provenanceMedication.startDate == MedicationDocumentObjectMother.OMEPRAZOLE_START_DATE
        assert provenanceMedication.endDate == MedicationDocumentObjectMother.OMEPRAZOLE_END_DATE
        assert provenanceMedication.dosage.value == MedicationDocumentObjectMother.OMEPRAZOLE_DOSAGE_VALUE
        assert provenanceMedication.dosage.unit == MedicationDocumentObjectMother.OMEPRAZOLE_DOSAGE_UNIT
        assert provenanceMedication.administrationRoute == MedicationDocumentObjectMother.OMEPRAZOLE_ADMINISTRATION_ROUTE_ORAL
        assert provenanceMedication.doseForm == MedicationDocumentObjectMother.OMEPRAZOLE_DOSE_FORM_CAPSULE
        assert provenanceMedication.dosingFrequency == MedicationDocumentObjectMother.OMEPRAZOLE_DOSING_FREQUENCY_DAILY
    }

    private static void assertProjectionStepsMedication(List<ProjectionStep> steps) {
        assert steps.size() == 2
        assert steps*.projector == ["Project to ProjectedValue", "ProjectedValue to FormItem"]
    }

    PopulateEvent.Request createRequest() {
        return PopulateEvent.Request.newBuilder()
                .withReferenceDate(REFERENCE_DATE)
                .withStudyId(STUDY_ID)
                .withEventDefinitionId(EVENT_DEFINITION_ID)
                .withSubjectId(SUBJECT_ID)
                .build()
    }

    private createStudy(UserIdentifier investigator) {
        def studySnapshot = aDefaultStudySnapshotBuilder(investigator)
                .withStudyId(STUDY_ID)
                .withSubjects(singletonList(createSubject(DATE_NOW)))
                .withMetadata(aMetaDataDefinitionSnapshotWithEventDefinitions(createEventDefinitionSnapshot()))
                .withItemQueryMappings(Collections.singletonMap(
                        ITEM_ID,
                        getDemographicItemQueryMapping()
                )).build()
        studyRepository.save(Study.restoreSnapshot(studySnapshot))
    }

    private createStudyWithItemForEachDataPointType() {
        def itemDM = ItemDefinitionId.of("DM")
        def demographic = aDefaultItemDefinitionBuilder().withId(itemDM).build()
        def demographicMapping = getItemQueryMapping(DEMOGRAPHIC_ITEM_QUERY_MAPPING_FILE)
        def itemLB = ItemDefinitionId.of("LB")
        def labValue = aDefaultItemDefinitionBuilder().withId(itemLB).build()
        def labValueMapping = getItemQueryMapping(LAB_VALUE_ITEM_QUERY_MAPPING_FILE)
        def itemVS = ItemDefinitionId.of("VS")
        def vitalSign = aDefaultItemDefinitionBuilder().withId(itemVS).build()
        def vitalSignMapping = getItemQueryMapping(VITAL_SIGN_ITEM_QUERY_MAPPING_FILE)
        def itemMED = ItemDefinitionId.of("MED")
        def medication = aDefaultItemDefinitionBuilder().withId(itemMED).build()
        def medicationMapping = getItemQueryMapping(MEDICATION_ITEM_QUERY_MAPPING_FILE)
        def group = aDefaultItemGroupDefinitionBuilder().withItemDefinitions([demographic, labValue, vitalSign, medication]).build()
        def form = aDefaultFormDefinitionSnapshotBuilder().withItemGroupDefinitions([group]).build()
        def event = aDefaultEventDefinitionSnapshotBuilder().withId(EVENT_DEFINITION_ID).withFormDefinitions(singletonList(form)).build()

        def mappings = new HashMap()
        mappings.put(itemDM, demographicMapping)
        mappings.put(itemLB, labValueMapping)
        mappings.put(itemVS, vitalSignMapping)
        mappings.put(itemMED, medicationMapping)

        def study = aDefaultStudySnapshotBuilder(UserIdentifier.of("42"))
                .withStudyId(STUDY_ID)
                .withSubjects(singletonList(createSubject(DATE_NOW)))
                .withMetadata(aMetaDataDefinitionSnapshotWithEventDefinitions(event))
                .withItemQueryMappings(mappings)
                .build()
        studyRepository.save(Study.restoreSnapshot(study))
    }

    private SubjectSnapshot createSubject(LocalDate consentDate) {
        aDefaultSubjectSnapshotBuilder()
                .withPatientCDWReference(generateKnownPatientId())
                .withSubjectId(SUBJECT_ID)
                .withEdcSubjectReference(EDC_SUBJECT_REFERENCE)
                .withEHRRegistrationStatus(REGISTERED)
                .withDateOfConsent(consentDate)
                .build()
    }

    private static EventDefinitionSnapshot createEventDefinitionSnapshot() {
        aDefaultEventDefinitionSnapshotBuilder()
                .withId(EVENT_DEFINITION_ID)
                .withParentId(EVENT_PARENT_ID)
                .withName(EVENT_NAME)
                .withFormDefinitions(singletonList(createFormDefinitionSnapshot()))
                .build()
    }

    private static FormDefinitionSnapshot createFormDefinitionSnapshot() {
        aDefaultFormDefinitionSnapshotBuilder()
                .withName(FORM_NAME)
                .withItemGroupDefinitions(singletonList(createItemGroupDefinitionSnapshot()))
                .withLocalLab(LOCAL_LAB)
                .build()
    }

    private static ItemGroupDefinitionSnapshot createItemGroupDefinitionSnapshot() {
        aDefaultItemGroupDefinitionBuilder()
                .withName(ITEM_GROUP_NAME).withId(ITEM_GROUP_ID)
                .withItemDefinitions(singletonList(createItemDefinitionSnapshot()))
                .build()
    }

    private static ItemDefinitionSnapshot createItemDefinitionSnapshot() {
        Map<String, String> translations = new HashMap<>()
        translations.put(Locale.ENGLISH.toLanguageTag(), ITEM_LABEL_ENGLISH)
        translations.put(Locale.FRENCH.toLanguageTag(), ITEM_LABEL_FRENCH)
        aDefaultItemDefinitionBuilder().withId(ITEM_ID).withLabel(new ItemLabelSnapshot(ITEM_NAME, new QuestionSnapshot(translations))).build()
    }

    private static ItemQueryMappingSnapshot getDemographicItemQueryMapping() throws IOException {
        return getItemQueryMapping(DEMOGRAPHIC_ITEM_QUERY_MAPPING_FILE)
    }

    private static ItemQueryMappingSnapshot getItemQueryMapping(String mappingFilePath) throws IOException {
        URL url = Resources.getResource(mappingFilePath)
        def mapping = Resources.toString(url, Charsets.UTF_8)
        return ItemQueryMappingSnapshot.newBuilder().withValue(mapping).build()
    }

    private aDemographic() {
        def demographic = DemographicDocumentObjectMother.genderFemaleBuilder().withSubjectId(SUBJECT_ID).build()
        demographicRepository.save(demographic)
    }

    private aLabValue() {
        def labValue = LabValueDocumentObjectMother.insulinLabValueDocumentBuilder().forSubject(SUBJECT_ID).build()
        labValueRepository.save(labValue)
    }

    private aVitalSign() {
        def vitalSign = VitalSignDocumentObjectMother.aNormalAdultBmiVitalSignDocumentBuilder().withSubjectId(SUBJECT_ID).build()
        vitalSignRepository.save(vitalSign)
    }

    private aMedication() {
        def medication = MedicationDocumentObjectMother.anOmeprazoleMedicationDocumentBuilder().withSubjectId(SUBJECT_ID).build()
        medicationRepository.save(medication)
    }
}