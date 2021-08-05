package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.command.PopulateEvent
import com.custodix.insite.local.ehr2edc.domain.service.ItemQueryMappingService
import com.custodix.insite.local.ehr2edc.mongo.app.document.StudyDocument
import com.custodix.insite.local.ehr2edc.mongo.app.study.StudyMongoRepository
import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement.MeasurementToUnitProjector
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValueField
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ConceptCriterion
import com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.LabValueField
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.LabValueToMeasurementProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.ProjectLabValue
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept.LabConceptToCodeProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept.LabValueToLabConceptProjector
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.repository.DemographicRepository
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.*
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.repository.LabValueRepository
import com.custodix.insite.local.ehr2edc.querymapping.model.ItemQueryMapping
import com.custodix.insite.local.ehr2edc.snapshots.*
import com.custodix.insite.local.ehr2edc.vocabulary.*
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate
import java.time.LocalDateTime

import static com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem.UnitMapping.DISPLAY
import static com.custodix.insite.local.ehr2edc.snapshots.MetadataDefinitionSnapshotObjectMother.aMetaDataDefinitionSnapshotWithEventDefinitions
import static com.custodix.insite.local.ehr2edc.vocabulary.EHRRegistrationStatus.REGISTERED

class PopulateEventIntegrationDuplicateLogLineSpec extends AbstractSpecification {

    private static final ConceptCode CONCEPT_GLUCOSE = ConceptCode.conceptFor("2339-0");
    private static final StudyId STUDY_ID = StudyId.of("STUDY")
    private static final EventDefinitionId EVENT_DEFINITION_ID = EventDefinitionId.of("EventDefinitionId-1")
    private static final String EVENT_PARENT_ID = "parent"
    private static final EDCSubjectReference EDC_SUBJECT_REFERENCE = EDCSubjectReference.of("EDCSubjectReference-123")
    private static final String LOGLINE_ITEM_GROUP_ID = "LOG_LINE.ITEM_GROUP.ID"
    private static final String LB_GLUC_LOINC_ITEM_ID = "LB_GLUC.LBLOINC"
    private static final String LB_GLUC_LBORRESU_ITEM_ID = "LB_GLUC.LBORRESU"
    private static final String GLUCOSE_MEASUREMENT_UNIT = "mg/dL"
    private static final String LB_GLUC_LBORRES_ITEM_ID = "LB_GLUC.LBORRES"
    private static final String GLUCOSE_VALUE_INDEX = "GlucoseValueIndex"
    private static final String GLUCOSE_VALUE = "13"
    private static final String LOGLINE_ITEM_GROUP_NAME = "LOG_LINE.ITEM_GROUP.NAME"
    private static final String FORM_NAME = "form-name-123-098"
    private static final LocalDateTime NOW = LocalDateTime.now()
    private static final String SUBJECT_ID_VALUE = "SUBJ-1"
    private static final LocalDateTime TWO_HOURS_AGO = NOW.minusHours(2L)

    @Autowired
    private PopulateEvent populateEvent
    @Autowired
    private StudyMongoRepository.StudyMongoSnapshotRepository studyMongoSnapshotRepository

    @Autowired
    private DemographicRepository demographicRepository

    @Autowired
    private LabValueRepository labValueRepository

    @Autowired
    private ItemQueryMappingService itemQueryMappingService

    def setup() {
        eventMongoSnapshotRepository.deleteAll()
    }

    def "handles duplicate log line correctly"(String subjectIdValue, LocalDateTime glucoseLabValueDate1, LocalDateTime glucoseLabValueDate2) {
        given: "a subject with id #subjectIdValue"
        def subjectId = SubjectId.of(subjectIdValue)
        and: "lab value glucose for #subjectId and on date #glucoseLabValueDate1 for a subject with id #subjectIdValue"
        populateGlucoseLabValueFor(subjectId, glucoseLabValueDate1)
        and: "lab value glucose for #subjectId and on date #glucoseLabValueDate2 for a subject with id #subjectIdValue"
        populateGlucoseLabValueFor(subjectId, glucoseLabValueDate2)

        createStudyWithRepeatingGlucoseItemGroup(subjectId)

        and: "An event population request"
        PopulateEvent.Request request = createRequest(subjectId)

        when: "an event is populated"
        populateEvent.populate(request)

        then: "Populate event contains 1 form"
        def event = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId())
        event.isPresent()
        def forms = eventRepository.findLatestEvent(request.getSubjectId(), request.getEventDefinitionId()).get().getPopulatedForms()
        forms.size() == 1
        def form = forms[0]
        and: "form with 1 item group"
        form.getItemGroups().size() == 1
        def itemGroup = form.getItemGroups()[0]
        and: "item group containing  3 items"
        itemGroup.id.id == LOGLINE_ITEM_GROUP_ID
        itemGroup.index == GLUCOSE_VALUE_INDEX &&
        itemGroup.items.size() == 3

        and: "1 item with id LB_GLUC.LBLOINC"
        itemGroup.getItems().any{
            item  ->
                item.id.id == LB_GLUC_LOINC_ITEM_ID &&
                item.value == new LabeledValue(CONCEPT_GLUCOSE.getCode())
        }

        and: "1 item with id LB_GLUC.LBORRES"
        itemGroup.getItems().any{
            item  ->
                item.id.id == LB_GLUC_LBORRES_ITEM_ID &&
                item.value == new LabeledValue(GLUCOSE_VALUE) &&
                item.measurementUnitReference.isPresent() &&
                item.measurementUnitReference.get().id == GLUCOSE_MEASUREMENT_UNIT
        }
        
        and: "1 item with id LB_GLUC.LBORRESU"
        itemGroup.getItems().any{
            item  ->
                item.id.id == LB_GLUC_LBORRESU_ITEM_ID &&
                item.value == new LabeledValue(GLUCOSE_MEASUREMENT_UNIT)
        }

        where:
        subjectIdValue      | glucoseLabValueDate1  | glucoseLabValueDate2
        SUBJECT_ID_VALUE    | NOW                    | TWO_HOURS_AGO
    }

    PopulateEvent.Request createRequest(SubjectId subjectId) {
        return PopulateEvent.Request.newBuilder()
                .withReferenceDate(LocalDate.now())
                .withStudyId(STUDY_ID)
                .withEventDefinitionId(EVENT_DEFINITION_ID)
                .withSubjectId(subjectId)
                .build()
    }


    private populateGlucoseLabValueFor(SubjectId subjectId, LocalDateTime labvalueDate) {
        LabConceptField labConcept = LabConceptObjectMother.aDefaultLabConceptBuilder()
                .withConcept(CONCEPT_GLUCOSE)
                .withComponent("Glucose")
                .withMethod("")
                .withFastingStatus("NOT_FASTING")
                .withSpecimen("Bld")
                .build();

        LabMeasurementField measurement = LabMeasurementObjectMother.aDefaultLabMeasurementBuilder()
                .withValue(new BigDecimal(GLUCOSE_VALUE))
                .withUnit(GLUCOSE_MEASUREMENT_UNIT)
                .withLowerLimit(BigDecimal.TEN)
                .withUpperLimit(new BigDecimal(20))
                .build()

        def glucoseLabValue = LabValueDocumentObjectMother.aDefaultLabValueDocumentBuilder()
                .forSubject(subjectId)
                .withStartDate(labvalueDate)
                .withEndDate(labvalueDate)
                .withLabConcept(labConcept)
                .withQuantitativeResult(measurement)
                .build()

        labValueRepository.save(glucoseLabValue)
    }

    Map<ItemDefinitionId, ItemQueryMappingSnapshot> getItemQueryMappings() {
        def query = QueryDsl.labValueQuery().query
        query.addCriterion(ConceptCriterion.conceptIsExactly(CONCEPT_GLUCOSE))
        def glucoseCode = ItemQueryMapping.newBuilder()
                .withQuery(query)
                .withProjectors([new LabValueToLabConceptProjector(), new LabConceptToCodeProjector()])
                .build()

        def glucoseValue = ItemQueryMapping.newBuilder()
                .withQuery(query)
                .withProjectors([
                        new ProjectLabValue(LabValueField.VALUE),
                        new ProjectedValueToFormItem(
                                Collections.singletonMap(CONCEPT_GLUCOSE.getCode(), GLUCOSE_VALUE_INDEX),
                                ProjectedValueField.VALUE,
                                DISPLAY,
                                false,
                                true
                        )])
                .build()
        
        def glucoseUnit = ItemQueryMapping.newBuilder()
                .withQuery(query)
                .withProjectors([new LabValueToMeasurementProjector(), new MeasurementToUnitProjector()])
                .build()

        Map<ItemDefinitionId, ItemQueryMappingSnapshot> labValueItemQueryMappings = new HashMap<>()

        labValueItemQueryMappings.put(ItemDefinitionId.of(LB_GLUC_LOINC_ITEM_ID), toSnapshot(glucoseCode))
        labValueItemQueryMappings.put(ItemDefinitionId.of(LB_GLUC_LBORRES_ITEM_ID), toSnapshot(glucoseValue))
        labValueItemQueryMappings.put(ItemDefinitionId.of(LB_GLUC_LBORRESU_ITEM_ID), toSnapshot(glucoseUnit))

        return labValueItemQueryMappings
    }

    ItemQueryMappingSnapshot toSnapshot(final ItemQueryMapping itemQueryMapping) {
        ItemQueryMappingSnapshot.newBuilder()
                .withValue(itemQueryMappingService.toItemQueryMappingJson(itemQueryMapping.getQuery(), itemQueryMapping.getProjectors()).getValue())
                .build()
    }

    private createStudyWithRepeatingGlucoseItemGroup(SubjectId subjectId) {
        def studySnapshot = StudySnapshotObjectMother.aDefaultStudySnapshotBuilder(UserIdentifier.of("42"))
                .withStudyId(STUDY_ID)
                .withSubjects(Collections.singletonList(createSubject(subjectId)))
                .withMetadata(aMetaDataDefinitionSnapshotWithEventDefinitions(createEventDefinitionSnapshot()))
                .withItemQueryMappings(getItemQueryMappings()).build()
        studyMongoSnapshotRepository.save(StudyDocument.fromSnapshot(studySnapshot))
    }

    private SubjectSnapshot createSubject(SubjectId subjectId) {
        SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder()
                .withPatientCDWReference(generateKnownPatientId())
                .withSubjectId(subjectId)
                .withEdcSubjectReference(EDC_SUBJECT_REFERENCE)
                .withEHRRegistrationStatus(REGISTERED)
                .withDateOfConsent(DATE_NOW)
                .build()
    }

    private static EventDefinitionSnapshot createEventDefinitionSnapshot() {
        EventDefinitionSnapshotObjectMother.aDefaultEventDefinitionSnapshotBuilder()
                .withId(EVENT_DEFINITION_ID)
                .withParentId(EVENT_PARENT_ID)
                .withFormDefinitions(Collections.singletonList(createFormDefinitionSnapshot()))
                .build()
    }

    private static FormDefinitionSnapshot createFormDefinitionSnapshot() {
        FormDefinitionSnapshotObjectMother.aDefaultFormDefinitionSnapshotBuilder()
                .withName(FORM_NAME)
                .withItemGroupDefinitions(Collections.singletonList(createItemGroupDefinitionSnapshot()))
                .build()
    }

    private static ItemGroupDefinitionSnapshot createItemGroupDefinitionSnapshot() {

        ItemDefinitionSnapshot loinc = anItem(LB_GLUC_LOINC_ITEM_ID)
        ItemDefinitionSnapshot value = anItem(LB_GLUC_LBORRES_ITEM_ID)
        ItemDefinitionSnapshot unit = anItem(LB_GLUC_LBORRESU_ITEM_ID)

        ItemGroupDefinitionSnapshotObjectMother.aDefaultItemGroupDefinitionBuilder()
                .withId(ItemGroupDefinitionId.of(LOGLINE_ITEM_GROUP_ID))
                .withName(LOGLINE_ITEM_GROUP_NAME)
                .withItemDefinitions(Arrays.asList(loinc, value, unit))
                .withRepeating(true)
                .build()
    }

    private static ItemDefinitionSnapshot anItem(String id) {
        return ItemDefinitionSnapshotObjectMother.aDefaultItemDefinitionBuilder()
                .withId(ItemDefinitionId.of(id))
                .build();
    }

}