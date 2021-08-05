package com.custodix.insite.local.ehr2edc.query.populator

import com.custodix.insite.local.ehr2edc.DomainTime
import com.custodix.insite.local.ehr2edc.ItemQueryMappingJson
import com.custodix.insite.local.ehr2edc.domain.service.ItemQueryMappingService
import com.custodix.insite.local.ehr2edc.infra.time.TestTimeService
import com.custodix.insite.local.ehr2edc.populator.EHRGateway
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification
import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.BooleanToNYProjector
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement.MeasurementToUnitProjector
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement.MeasurementToValueProjector
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ConvertProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.SetProjectedValueUnit
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ConceptCriterion
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.DemographicTypeCriterion
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion
import com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate.*
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderProjector
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderToSDTMCodeProjector
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQuery
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.Demographics
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.LabValueToMeasurementProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept.LabConceptToCodeProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept.LabConceptToFastingBooleanProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept.LabValueToLabConceptProjector
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LabValues
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LaboratoryQuery
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationToNameProjector
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.Medications
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.ProjectVitalSignValue
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.VitalSignField
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.concept.VitalSignConceptToCodeProjector
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.concept.VitalSignToVitalSignConceptProjector
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSigns
import com.custodix.insite.local.ehr2edc.query.mongo.MongoEHRGatewayFactory
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

import static com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem.UnitMapping.EXPORT
import static com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem.UnitMapping.IGNORE
import static com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValueField.UNIT
import static com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValueField.VALUE
import static com.custodix.insite.local.ehr2edc.query.populator.VitalSignFactory.CONCEPT_HEIGHT
import static java.util.Collections.emptyMap
import static java.util.Collections.singletonMap

abstract class AbstractFormPopulatorSpecification extends Specification {
    private static final ConceptCode CONCEPT_GLUCOSE = ConceptCode.conceptFor("2339-0")
    private static final ConceptCode CONCEPT_HEMOGLOBIN = ConceptCode.conceptFor("718-7")
    private static final ConceptCode CONCEPT_HEMATOCRIT = ConceptCode.conceptFor("4544-3")

    SubjectId subjectId = SubjectId.of("patient-zero")

    DemographicFactory demographicFactory = new DemographicFactory()
    Demographic demographic = demographicFactory.aDemographic(subjectId)
    LabValueFactory labValueFactory = new LabValueFactory()
    LabValue glucose1 = labValueFactory.aLabValueGlucose(subjectId, LocalDateTime.of(2019, Month.JUNE, 20, 0, 0))
    LabValue glucose2 = labValueFactory.aLabValueGlucose(subjectId, LocalDateTime.of(2019, Month.JUNE, 21, 0, 0))
    LabValue hemoglobin = labValueFactory.aLabValueHemoglobin(subjectId)
    LabValue hematocrit = labValueFactory.aLabValueHematocrit(subjectId)
    VitalSignFactory vitalSignFactory = new VitalSignFactory()
    VitalSign bmiVitalSign = vitalSignFactory.bmi(subjectId, LocalDateTime.of(2019, Month.JUNE, 20, 0, 0))
    VitalSign heightVitalSign = vitalSignFactory.height(subjectId, LocalDateTime.of(2019, Month.JUNE, 20, 0, 0))
    MedicationFactory medicationFactory = new MedicationFactory()
    Medication medication = medicationFactory.aOmeprazoleMedication(subjectId, LocalDateTime.of(2019, Month.JUNE, 20, 0, 0))

    EHRGateway demographicGateway = Stub()
    EHRGateway labValueGateway = Stub()
    EHRGateway medicationGateway = Stub()
    EHRGateway vitalSignGateway = Stub()
    FormFactory formFactory = new FormFactory()
    ItemQueryMappingService itemQueryMappingService = new FormPopulatorConfiguration().itemQueryMappingService()
    MongoEHRGatewayFactory mongoEHRGatewayFactory = new MongoEHRGatewayFactory(Arrays.asList(demographicGateway, labValueGateway, medicationGateway, vitalSignGateway))
    EhrGatewayFactoryFactory ehrGatewayFactoryFactory = Stub()

    QueryMappingFormPopulator formPopulator() {
        return new QueryMappingFormPopulator(new FormPopulatorLogger(), itemQueryMappingService, ehrGatewayFactoryFactory)
    }

    def setup() {
        DomainTime.setTime(new TestTimeService())
        populateDemographics()
        populateLabValues()
        populateMedications()

        vitalSignGateway.canHandle(_ as VitalSignQuery) >> true
        labValueGateway.canHandle(_ as LaboratoryQuery) >> true
        demographicGateway.canHandle(_ as DemographicQuery) >> true
        medicationGateway.canHandle(_ as MedicationQuery) >> true

        ehrGatewayFactoryFactory.selectGateway(_ as Query, _ as PopulationSpecification) >> {
            Query query, PopulationSpecification specification ->
            mongoEHRGatewayFactory.create(query, specification)
        }
    }

    void populateDemographics() {
        demographicGateway.execute(_ as Query, _ as  LocalDate) >> {
            Query query, LocalDate referenceDate ->
                query.getCriteria().subject().subjectId == subjectId && query.getCriteria().demographicType() == Optional.of(DemographicTypeCriterion.type(DemographicType.BIRTH_DATE)) ?
                    new Demographics(Arrays.asList(demographic)):
                    new Demographics(Collections.emptyList())
        }
    }

    void populateLabValues() {
        labValueGateway.execute(_ as Query, _ as LocalDate) >> new LabValues(Arrays.asList(glucose1, glucose2, hemoglobin, hematocrit))
    }

    void populateBMI() {
        vitalSignGateway.execute(_ as Query, _ as LocalDate) >> new VitalSigns(Arrays.asList(bmiVitalSign))
    }

    void populateHeight() {
        vitalSignGateway.execute(_ as Query, _ as LocalDate) >> new VitalSigns(Arrays.asList(heightVitalSign))
    }

    void populateMedications() {
        medicationGateway.execute(_ as Query, _ as LocalDate) >> new Medications(Arrays.asList(medication))
    }

    Map createDMItemQueryMappings() {
        Map<ItemDefinitionId, ItemQueryMappingJson> mappings = new HashMap<>()
        mappings.putAll(createAgeValueMapping("DM_DOB.AGE"))
        mappings.putAll(createAgeUnitMapping("DM_DOB.AGEU"))
        mappings.putAll(createGenderMapping("DM_DOB.SEX"))
        return mappings
    }

    Map createDMAndLBItemQueryMappings() {
        Map<ItemDefinitionId, ItemQueryMappingJson> mappings = new HashMap<>()
        mappings.putAll(createDMItemQueryMappings())
        mappings.putAll(createLBItemQueryMappings())
        return mappings
    }

    Map createDMAndLogLineItemQueryMappings() {
        Map<ItemDefinitionId, ItemQueryMappingJson> mappings = new HashMap<>()
        mappings.putAll(createDMItemQueryMappings())
        mappings.putAll(createLogLineItemMappings())
        return mappings
    }

    Map createLBItemQueryMappings() {
        def query = QueryDsl.labValueQuery().query
        query.addCriterion(ConceptCriterion.conceptIsExactly(CONCEPT_GLUCOSE))
        Map<ItemDefinitionId, ItemQueryMappingJson> mappings = new HashMap<>()
        mappings.putAll(createLabCodeMapping("LB_GLUC.LBLOINC", query))
        mappings.putAll(createLabResultValueMapping("LB_GLUC.LBORRES", query))
        mappings.putAll(createLabResultUnitMapping("LB_GLUC.LBORRESU", query))
        mappings.putAll(createLabFastingMapping("LB_GLUC.LBFAST", query))
        return mappings
    }

    Map createMedicationItemMappings() {
        def query = QueryDsl.medicationQueryDsl().query
        query.addCriterion(SubjectCriterion.subjectIs(subjectId))
        Map<ItemDefinitionId, ItemQueryMappingJson> mappings = new HashMap<>()
        mappings.putAll(createMedicationNameMapping("LOG_LINE.CMTRT", query))
        return mappings
    }

    Map createVitalSignItemMappings() {
        def query = QueryDsl.vitalSignQueryDsl().query
        query.addCriterion(SubjectCriterion.subjectIs(subjectId))
        query.addCriterion(ConceptCriterion.conceptIn(Collections.singletonList(VitalSignFactory.CONCEPT_BMI)))
        Map<ItemDefinitionId, ItemQueryMappingJson> mappings = new HashMap<>()
        mappings.putAll(createVitalSignCodeMapping("LOG_LINE.VTS", query))
        return mappings
    }

    Map createHeightMappings() {
        def query = QueryDsl.vitalSignQueryDsl().query
        query.addCriterion(SubjectCriterion.subjectIs(subjectId))
        query.addCriterion(ConceptCriterion.conceptIn(Collections.singletonList(CONCEPT_HEIGHT)))
        Map<ItemDefinitionId, ItemQueryMappingJson> mappings = new HashMap<>()
        mappings.putAll(createVitalSignValueInCmSpecifiedMapping("VS_HEIGHT.HEIGHT", query))
        mappings.putAll(createVitalSignUnitInCmMapping("VS_HEIGHT.HEIGHTU", query))
        return mappings
    }

    Map createReadOnlyHeightMappings() {
        def query = QueryDsl.vitalSignQueryDsl().query
        query.addCriterion(SubjectCriterion.subjectIs(subjectId))
        query.addCriterion(ConceptCriterion.conceptIn(Collections.singletonList(CONCEPT_HEIGHT)))
        Map<ItemDefinitionId, ItemQueryMappingJson> mappings = new HashMap<>()
        mappings.putAll(createVitalSignValueInCmSpecifiedReadOnlyMapping("VS_HEIGHT.HEIGHT", query))
        mappings.putAll(createVitalSignUnitInCmMapping("VS_HEIGHT.HEIGHTU", query))
        return mappings
    }

    Map createLogLineItemMappings() {
        def query = QueryDsl.labValueQuery().query
        query.addCriterion(ConceptCriterion.conceptIn([CONCEPT_GLUCOSE, CONCEPT_HEMOGLOBIN, CONCEPT_HEMATOCRIT]))
        Map<ItemDefinitionId, ItemQueryMappingJson> mappings = new HashMap<>()
        mappings.putAll(createLabCodeMapping("LOG_LINE.LBLOINC", query))
        mappings.putAll(createLabResultValueMapping("LOG_LINE.LBORRES", query))
        mappings.putAll(createLabResultUnitMapping("LOG_LINE.LBORRESU", query))
        return mappings
    }

    Map createAgeValueMapping(String itemDefinitionId) {
        return singletonMap(ItemDefinitionId.of(itemDefinitionId),
                itemQueryMappingService.toItemQueryMappingJson(QueryDsl.demographicQuery().forType(DemographicType.BIRTH_DATE).query,
                        [new DateOfBirthProjector(), new DateOfBirthToAgeProjector(AgeUnit.YEARS), new AgeToNumericalProjector()]))
    }

    Map createAgeUnitMapping(String itemDefinitionId) {
        return singletonMap(ItemDefinitionId.of(itemDefinitionId), itemQueryMappingService.toItemQueryMappingJson(QueryDsl.demographicQuery().forType(DemographicType.BIRTH_DATE).query, [new DateOfBirthProjector(), new DateOfBirthToAgeProjector(AgeUnit.YEARS), new AgeToUnitProjector()]))
    }

    Map createGenderMapping(String itemDefinitionId) {
        return singletonMap(ItemDefinitionId.of(itemDefinitionId), itemQueryMappingService.toItemQueryMappingJson(QueryDsl.demographicQuery().forType(DemographicType.GENDER).query, [new GenderProjector(), new GenderToSDTMCodeProjector()]))
    }

    Map createLabCodeMapping(String itemDefinitionId, LaboratoryQuery query) {
        return singletonMap(ItemDefinitionId.of(itemDefinitionId), itemQueryMappingService.toItemQueryMappingJson(query, [new LabValueToLabConceptProjector(), new LabConceptToCodeProjector()]))
    }

    Map createLabResultValueMapping(String itemDefinitionId, LaboratoryQuery query) {
        return singletonMap(ItemDefinitionId.of(itemDefinitionId), itemQueryMappingService.toItemQueryMappingJson(query, [new LabValueToMeasurementProjector(), new MeasurementToValueProjector()]))
    }

    Map createLabResultUnitMapping(String itemDefinitionId, LaboratoryQuery query) {
        return singletonMap(ItemDefinitionId.of(itemDefinitionId), itemQueryMappingService.toItemQueryMappingJson(query, [new LabValueToMeasurementProjector(), new MeasurementToUnitProjector()]))
    }

    Map createLabFastingMapping(String itemDefinitionId, LaboratoryQuery query) {
        return singletonMap(ItemDefinitionId.of(itemDefinitionId), itemQueryMappingService.toItemQueryMappingJson(query, [new LabValueToLabConceptProjector(), new LabConceptToFastingBooleanProjector(), new BooleanToNYProjector()]))
    }

    Map createMedicationNameMapping(String itemDefinitionId, MedicationQuery query) {
        return singletonMap(ItemDefinitionId.of(itemDefinitionId), itemQueryMappingService.toItemQueryMappingJson(query, [new MedicationToNameProjector()]))
    }

    Map createVitalSignCodeMapping(String itemDefinitionId, VitalSignQuery query) {
        return singletonMap(ItemDefinitionId.of(itemDefinitionId), itemQueryMappingService.toItemQueryMappingJson(query, [new VitalSignToVitalSignConceptProjector(), new VitalSignConceptToCodeProjector()]))
    }

    Map createVitalSignUnitInCmMapping(String itemDefinitionId, VitalSignQuery query) {
        return singletonMap(ItemDefinitionId.of(itemDefinitionId), itemQueryMappingService.toItemQueryMappingJson(query, [new ProjectVitalSignValue(VitalSignField.VALUE),
                                                                                                                          new ConvertProjectedValue("cm"),
                                                                                                                          new ProjectedValueToFormItem(emptyMap(), UNIT, IGNORE, true, false)]))
    }

    Map createVitalSignValueInCmSpecifiedMapping(String itemDefinitionId, VitalSignQuery query) {
        return singletonMap(ItemDefinitionId.of(itemDefinitionId), itemQueryMappingService.toItemQueryMappingJson(query, [new ProjectVitalSignValue(VitalSignField.VALUE),
                                                                                                                          new ConvertProjectedValue("cm"),
                                                                                                                          new SetProjectedValueUnit("13899.CM/IN.cm"),
                                                                                                                          new ProjectedValueToFormItem(emptyMap(), VALUE, EXPORT, false, true)]))
    }

    Map createVitalSignValueInCmSpecifiedReadOnlyMapping(String itemDefinitionId, VitalSignQuery query) {
        return singletonMap(ItemDefinitionId.of(itemDefinitionId), itemQueryMappingService.toItemQueryMappingJson(query, [new ProjectVitalSignValue(VitalSignField.VALUE),
                                                                                                                          new ConvertProjectedValue("cm"),
                                                                                                                          new SetProjectedValueUnit("13899.CM/IN.cm"),
                                                                                                                          new ProjectedValueToFormItem(emptyMap(), VALUE, EXPORT, true, true)]))
    }
}
