package com.custodix.insite.local.ehr2edc.query.populator

import com.custodix.insite.local.ehr2edc.ItemQueryMappingJson
import com.custodix.insite.local.ehr2edc.ItemQueryMappings
import com.custodix.insite.local.ehr2edc.metadata.model.FormDefinition
import com.custodix.insite.local.ehr2edc.populator.PopulatedForm
import com.custodix.insite.local.ehr2edc.populator.PopulatedItem
import com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroup
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.Demographics
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LabValues
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.Medications
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.slf4j.MDC
import org.springframework.boot.test.context.SpringBootTest

import java.time.LocalDate

@SpringBootTest(classes = QueryMappingFormPopulatorSpec.class)
class QueryMappingFormPopulatorSpec extends AbstractFormPopulatorSpecification {

    def "Can populate a form with fixed index for repeating item group"() {
        given: "A form with a repeating item group with items for vital sign codes"
        def formDefinition = formFactory.aFormWithRepeatingItemGroup(FormFactory.aRepeatingVitalSignItemGroup())
        and: "A subject #subjectId.id with a vital sign record for BMI"
        assert subjectId
        populateBMI()

        and: "vital sign query mappings"
        def vitalSignQueryMappings = createVitalSignItemMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, vitalSignQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with one populated item group"
        populatedForm.itemGroups.size() == 1
        and: "with a populated first item group about vital signs"
        PopulatedItemGroup firstItemGroup = populatedForm.itemGroups.find({ it.id.id == "LOG_LINE" })
        and: "with VTS item having value '#expectedVitalSignCode'"
        firstItemGroup.items.any({ it.id.id == "LOG_LINE.VTS" && it.value == expectedVitalSignCode })

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
        expectedVitalSignCode = new LabeledValue("BMI_CODE")
        index = "2"
    }

    def "Can populate a form with a repeating item group for vital signs"() {
        given: "A form with a repeating item group with items for vital sign codes"
        def formDefinition = formFactory.aFormWithRepeatingItemGroup(FormFactory.aRepeatingVitalSignItemGroup())
        and: "A subject #subjectId.id with a vital sign record for BMI"
        assert subjectId
        populateBMI()

        and: "vital sign query mappings"
        def vitalSignQueryMappings = createVitalSignItemMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, vitalSignQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with one populated item group"
        populatedForm.itemGroups.size() == 1
        and: "with a populated item group about vital signs"
        PopulatedItemGroup firstItemGroup = populatedForm.itemGroups.find({ it.id.id == "LOG_LINE" })
        and: "with VTS item having value '#expectedVitalSignCode'"
        firstItemGroup.items.any({ it.id.id == "LOG_LINE.VTS" && it.value == expectedVitalSignCode })

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
        expectedVitalSignCode = new LabeledValue("BMI_CODE")
    }

    def "Can populate a form with a repeating item group for medication"() {
        given: "A form with a repeating item group with items for medication names"
        def formDefinition = formFactory.aFormWithRepeatingItemGroup(FormFactory.aRepeatingMedicationItemGroup())
        and: "A subject #subjectId.id with a medication record for omeprazole"
        assert subjectId

        and: "log medication item query mappings for medication name"
        def medicationQueryMappings = createMedicationItemMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, medicationQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with one populated item group"
        populatedForm.itemGroups.size() == 1
        and: "with a populated item group about omeprazole"
        PopulatedItemGroup itemGroupGlucoseFirst = populatedForm.itemGroups.find({ it.id.id == "LOG_LINE" })
        List<PopulatedItem> itemsGlucoseFirst = itemGroupGlucoseFirst.items
        and: "with CMTRT item having value '#expectedMedicationName'"
        itemsGlucoseFirst.any({ it.id.id == "LOG_LINE.CMTRT" && it.value == expectedMedicationName })

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
        expectedMedicationName = new LabeledValue("omeprazole")
    }

    def "Can populate a form with an item group about demographics"() {
        given: "A form with an item group with items for age and age units"
        def formDefinition = formFactory.aFormWithDemographicsItemGroup()
        and: "A subject #subjectId.id with date of birth 01/01/2000"
        assert subjectId
        and: "demographic item query mappings"
        def demographicItemQueryMappings = createDMItemQueryMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, demographicItemQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with one populated item group"
        populatedForm.itemGroups.size() == 1
        PopulatedItemGroup itemGroup = populatedForm.itemGroups.get(0)
        itemGroup.id.id == "DM_DOB"
        List<PopulatedItem> items = itemGroup.items
        and: "with AGE item having value '#expectedAge'"
        items.any({ it.id.id == "DM_DOB.AGE" && it.value == expectedAge })
        and: "with AGEU item having value '#expectedUnits'"
        items.any({ it.id.id == "DM_DOB.AGEU" && it.value == expectedUnits })

        MDC.getCopyOfContextMap().size() == 0

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
        expectedAge = new LabeledValue("19")
        expectedUnits = new LabeledValue("YEARS")
    }

    def "Can populate a form with an item group about glucose"() {
        given: "A form with an item group with items for glucose code, measurement value and measurement unit"
        def formDefinition = formFactory.aFormWithGlucoseItemGroup()
        and: "A subject #subjectId.id with a measurement for glucose"
        assert subjectId
        and: "lab value item query mappings"
        def labValueItemQueryMappings = createLBItemQueryMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, labValueItemQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with one populated item group"
        populatedForm.itemGroups.size() == 1
        PopulatedItemGroup itemGroup = populatedForm.itemGroups.get(0)
        itemGroup.id.id == "LB_GLUC"
        List<PopulatedItem> items = itemGroup.items
        and: "with LBLOINC item having value '#expectedLoinc'"
        items.any({ it.id.id == "LB_GLUC.LBLOINC" && it.value == expectedLoinc })
        and: "with LBORRES item having value '#expectedValue'"
        items.any({ it.id.id == "LB_GLUC.LBORRES" && it.value == expectedValue })
        and: "with LBORRESU item having value '#expectedUnit'"
        items.any({ it.id.id == "LB_GLUC.LBORRESU" && it.value == expectedUnit })
        and: "with LBFAST item having value '#expectedFast'"
        items.any({ it.id.id == "LB_GLUC.LBFAST" && it.value == expectedFast })

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
        expectedLoinc = new LabeledValue("2339-0")
        expectedValue = new LabeledValue("13")
        expectedUnit = new LabeledValue("mg/dL")
        expectedFast = new LabeledValue("N")
    }

    def "Can populate a form with an item group about height in a measurement unit specified by the sponsor"() {
        given: "A form with an item group for height"
        def formDefinition = formFactory.aFormWithHeightItemGroup()
        and: "A subject #subjectId.id with a measurement for height with value '1.77' and unit 'm'"
        assert subjectId
        populateHeight()
        and: "The item for HEIGHT has a mapping to return the subject's height value after conversion to 'cm' with OID '#expectedMeasurementUnitOid' specified by the sponsor"
        def heightQueryMappings = createHeightMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, heightQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with one populated item group"
        populatedForm.itemGroups.size() == 1
        PopulatedItemGroup itemGroup = populatedForm.itemGroups.get(0)
        itemGroup.id.id == "VS_HEIGHT"
        List<PopulatedItem> items = itemGroup.items
        and: "the item group contains the HEIGHT item"
        PopulatedItem item = items.find({
            it.id.id == "VS_HEIGHT.HEIGHT"
        })
        and: "the item has value '#expectedValue'"
        item.value == expectedValue
        and: "the item has a measurement unit reference with id '#expectedMeasurementUnitOid'"
        item.measurementUnitReference.get().id == expectedMeasurementUnitOid
        !item.measurementUnitReference.get().readOnly
        and:
        item.key

        where:
        referenceDate = LocalDate.of(2019, 6, 20)
        expectedValue = new LabeledValue("177")
        expectedMeasurementUnitOid = "13899.CM/IN.cm"
    }

    def "Can populate a form with an item group about height containing only readOnly items"() {
        given: "A form with an item group for height"
        def formDefinition = formFactory.aFormWithHeightItemGroup()
        and: "A subject #subjectId.id with a measurement for height with value '1.77' and unit 'm'"
        assert subjectId
        populateHeight()
        and: "The READ-ONLY item for HEIGHT has a mapping to return the subject's height value after conversion to 'cm' with OID '#expectedMeasurementUnitOid' specified by the sponsor"
        def readOnlyHeightQueryMappings = createReadOnlyHeightMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, readOnlyHeightQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with zero populated item group"
        populatedForm.itemGroups.size() == 0

        where:
        referenceDate = LocalDate.of(2019, 6, 20)
        expectedValue = new LabeledValue("177.0")
        expectedMeasurementUnitOid = "13899.CM/IN.cm"
    }

    def "Can populate a form with an item group about height containing a read-only item"() {
        given: "A form with an item group for height"
        def formDefinition = formFactory.aFormWithHeightItemGroup()
        and: "A subject #subjectId.id with a measurement for height with value '1.77' and unit 'm'"
        assert subjectId
        populateHeight()
        and: "The item for HEIGHTU has a mapping to return the subject's height unit after conversion to 'cm' as a read-only item"
        def heightQueryMappings = createHeightMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, heightQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with one populated item group"
        populatedForm.itemGroups.size() == 1
        PopulatedItemGroup itemGroup = populatedForm.itemGroups.get(0)
        itemGroup.id.id == "VS_HEIGHT"
        List<PopulatedItem> items = itemGroup.items
        and: "the item group contains the HEIGHTU item"
        PopulatedItem item = items.find({
            it.id.id == "VS_HEIGHT.HEIGHTU"
        })
        and: "the item has value '#expectedValue'"
        item.value == expectedValue
        and: "the item is read-only"
        item.readOnly

        where:
        referenceDate = LocalDate.of(2019, 6, 20)
        expectedValue = new LabeledValue("cm")
    }

    def "Can populate a form with an item group about demographics and an item group about glucose"() {
        given: "A form with an item group with items for age and age units and an item group with items for glucose code, measurement value and measurement unit"
        def formDefinition = formFactory.aFormWithDemographicsItemGroupAndGlucoseItemGroup()
        and: "A subject #subjectId.id with date of birth 01/01/2000 and a measurement for glucose"
        assert subjectId
        and: "lab value & demographics item query mappings"
        def demographicAndLabValueItemQueryMappings = createDMAndLBItemQueryMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, demographicAndLabValueItemQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with two populated item groups"
        populatedForm.itemGroups.size() == 2
        and: "with a populated item group about demographics"
        PopulatedItemGroup itemGroupDemographics = populatedForm.itemGroups.find({ it.id.id == "DM_DOB" })
        List<PopulatedItem> itemsDemographics = itemGroupDemographics.items
        and: "with AGE item having value '#expectedAge'"
        itemsDemographics.any({ it.id.id == "DM_DOB.AGE" && it.value == expectedAge })
        and: "with AGEU item having value '#expectedUnits'"
        itemsDemographics.any({ it.id.id == "DM_DOB.AGEU" && it.value == expectedUnits })
        and: "with a populated item group about glucose"
        PopulatedItemGroup itemGroupGlucose = populatedForm.itemGroups.find({ it.id.id == "LB_GLUC" })
        List<PopulatedItem> itemsGlucose = itemGroupGlucose.items
        and: "with LBLOINC item having value '#expectedLoinc'"
        itemsGlucose.any({ it.id.id == "LB_GLUC.LBLOINC" && it.value == expectedLoinc })
        and: "with LBORRES item having value '#expectedValue'"
        itemsGlucose.any({ it.id.id == "LB_GLUC.LBORRES" && it.value == expectedValue })
        and: "with LBORRESU item having value '#expectedUnit'"
        itemsGlucose.any({ it.id.id == "LB_GLUC.LBORRESU" && it.value == expectedUnit })
        and: "with LBFAST item having value '#expectedFast'"
        itemsGlucose.any({ it.id.id == "LB_GLUC.LBFAST" && it.value == expectedFast })

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
        expectedAge = new LabeledValue("19")
        expectedUnits = new LabeledValue("YEARS")
        expectedLoinc = new LabeledValue("2339-0")
        expectedValue = new LabeledValue("13")
        expectedUnit = new LabeledValue("mg/dL")
        expectedFast = new LabeledValue("N")
    }

    def "Can populate a form with a repeating item group"() {
        given: "A form with a repeating item group with items for code, measurement value and measurement unit for hemoglobin and hematocrit"
        def formDefinition = formFactory.aFormWithRepeatingItemGroup(FormFactory.aRepeatingLabItemGroup())
        and: "A subject #subjectId.id with a measurement for hemoglobin and hematocrit"
        assert subjectId

        and: "log item query mappings"
        def logLineItemQueryMappings = createLogLineItemMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, logLineItemQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with four populated item groups"
        populatedForm.itemGroups.size() == 3

        List<PopulatedItem> allItems = populatedForm.itemGroups.findAll({ it.id.id == "LOG_LINE" }).items.flatten()

        and: "with LBLOINC item having value '#expectedLoinc'"
        allItems.any({ it.id.id == "LOG_LINE.LBLOINC" && it.value == expectedGlucoseLoinc })
        and: "with LBORRES item having value '#expectedValue'"
        allItems.any({ it.id.id == "LOG_LINE.LBORRES" && it.value == expectedGlucoseValue })
        and: "with LBORRESU item having value '#expectedUnit'"
        allItems.any({ it.id.id == "LOG_LINE.LBORRESU" && it.value == expectedGlucoseUnit })

        and: "with LBLOINC item having value '#expectedLoinc'"
        allItems.any({ it.id.id == "LOG_LINE.LBLOINC" && it.value == expectedHemoglobinLoinc })
        and: "with LBORRES item having value '#expectedValue'"
        allItems.any({ it.id.id == "LOG_LINE.LBORRES" && it.value == expectedHemoglobinValue })
        and: "with LBORRESU item having value '#expectedUnit'"
        allItems.any({ it.id.id == "LOG_LINE.LBORRESU" && it.value == expectedHemoglobinUnit })

        and: "with LBLOINC item having value '#expectedLoinc'"
        allItems.any({ it.id.id == "LOG_LINE.LBLOINC" && it.value == expectedHematocritLoinc })
        and: "with LBORRES item having value '#expectedValue'"
        allItems.any({ it.id.id == "LOG_LINE.LBORRES" && it.value == expectedHematocritValue })
        and: "with LBORRESU item having value '#expectedUnit'"
        allItems.any({ it.id.id == "LOG_LINE.LBORRESU" && it.value == expectedHematocritUnit })

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
        expectedGlucoseLoinc = new LabeledValue("2339-0")
        expectedGlucoseValue = new LabeledValue("13")
        expectedGlucoseUnit = new LabeledValue("mg/dL")
        expectedHemoglobinLoinc = new LabeledValue("718-7")
        expectedHemoglobinValue = new LabeledValue("14")
        expectedHemoglobinUnit = new LabeledValue("g/dL")
        expectedHematocritLoinc = new LabeledValue("4544-3")
        expectedHematocritValue = new LabeledValue("16")
        expectedHematocritUnit = new LabeledValue("g/dL")
    }

    def "Can populate a form with an item group about demographics and a repeating item group"() {
        given: "A form with an item group with items for age and age units and a repeating item group with items for code, measurement value and measurement unit for hemoglobin and hematocrit"
        def formDefinition = formFactory.aFormWithDemographicsItemGroupAndRepeatingItemGroup()
        and: "A subject #subjectId.id with a measurement for hemoglobin and hematocrit"
        assert subjectId
        and: "log line & demographics item query mappings"
        def demographicAndLogLineItemQueryMappings = createDMAndLogLineItemQueryMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, demographicAndLogLineItemQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with five populated item groups"
        populatedForm.itemGroups.size() == 4
        and: "with a populated item group about demographics"
        PopulatedItemGroup itemGroupDemographics = populatedForm.itemGroups.find({ it.id.id == "DM_DOB" })
        List<PopulatedItem> itemsDemographics = itemGroupDemographics.items
        and: "with AGE item having value '#expectedAge'"
        itemsDemographics.any({ it.id.id == "DM_DOB.AGE" && it.value == expectedAge })
        and: "with AGEU item having value '#expectedUnits'"
        itemsDemographics.any({ it.id.id == "DM_DOB.AGEU" && it.value == expectedUnits })

        List<PopulatedItem> allItems = populatedForm.itemGroups.findAll({ it.id.id == "LOG_LINE" }).items.flatten()

        and: "with LBLOINC item having value '#expectedLoinc'"
        allItems.any({ it.id.id == "LOG_LINE.LBLOINC" && it.value == expectedGlucoseLoinc })
        and: "with LBORRES item having value '#expectedValue'"
        allItems.any({ it.id.id == "LOG_LINE.LBORRES" && it.value == expectedGlucoseValue })
        and: "with LBORRESU item having value '#expectedUnit'"
        allItems.any({ it.id.id == "LOG_LINE.LBORRESU" && it.value == expectedGlucoseUnit })

        and: "with LBLOINC item having value '#expectedLoinc'"
        allItems.any({ it.id.id == "LOG_LINE.LBLOINC" && it.value == expectedHemoglobinLoinc })
        and: "with LBORRES item having value '#expectedValue'"
        allItems.any({ it.id.id == "LOG_LINE.LBORRES" && it.value == expectedHemoglobinValue })
        and: "with LBORRESU item having value '#expectedUnit'"
        allItems.any({ it.id.id == "LOG_LINE.LBORRESU" && it.value == expectedHemoglobinUnit })

        and: "with LBLOINC item having value '#expectedLoinc'"
        allItems.any({ it.id.id == "LOG_LINE.LBLOINC" && it.value == expectedHematocritLoinc })
        and: "with LBORRES item having value '#expectedValue'"
        allItems.any({ it.id.id == "LOG_LINE.LBORRES" && it.value == expectedHematocritValue })
        and: "with LBORRESU item having value '#expectedUnit'"
        allItems.any({ it.id.id == "LOG_LINE.LBORRESU" && it.value == expectedHematocritUnit })

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
        expectedAge = new LabeledValue("19")
        expectedUnits = new LabeledValue("YEARS")
        expectedGlucoseLoinc = new LabeledValue("2339-0")
        expectedGlucoseValue = new LabeledValue("13")
        expectedGlucoseUnit = new LabeledValue("mg/dL")
        expectedHemoglobinLoinc = new LabeledValue("718-7")
        expectedHemoglobinValue = new LabeledValue("14")
        expectedHemoglobinUnit = new LabeledValue("g/dL")
        expectedHematocritLoinc = new LabeledValue("4544-3")
        expectedHematocritValue = new LabeledValue("16")
        expectedHematocritUnit = new LabeledValue("g/dL")
    }

    def "Can populate a form with a non-repeating item group for which an item is mapped and a repeating item group for which no items are mapped"() {
        given: "A form with a demographic item group and a repeating item group"
        def formDefinition = formFactory.aFormWithDemographicsItemGroupAndRepeatingItemGroup()
        and: "A subject #subjectId.id"
        assert subjectId
        and: "The AGE item in the demographic item group is mapped while no items in the repeating item group are mapped"
        def itemId = "DM_DOB.AGE"
        def mapping = createAgeValueMapping(itemId)

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, mapping)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with 1 populated item group"
        populatedForm.itemGroups.size() == 1
        and: "with a populated item group about demographics"
        PopulatedItemGroup itemGroupDemographics = populatedForm.itemGroups.find({ it.id.id == "DM_DOB" })
        List<PopulatedItem> itemsDemographics = itemGroupDemographics.items
        and: "with AGE item having value '#expectedAge'"
        itemsDemographics.any({ it.id.id == itemId && it.value == expectedAge })

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
        expectedAge = new LabeledValue("19")
    }

    def "Does not populate known items for unknown subject"() {
        given: "A form with an item group with items for age and age units"
        def formDefinition = formFactory.aFormWithDemographicsItemGroup()
        and: "An unknown subjectId '#unknownId'"
        assert unknownId
        and: "demographic item query mappings"
        def demographicItemQueryMappings = createDMItemQueryMappings()

        when: "I populate the form for subject #unknownId.id at #referenceDate"
        def specification = aPopulationSpecification(formDefinition, unknownId, referenceDate, demographicItemQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with zero populated item group"
        populatedForm.itemGroups.size() == 0

        where:
        unknownId = SubjectId.of("unknown")
        referenceDate = LocalDate.of(2019, 6, 26)
    }

    def "Does not populate unmapped items"() {
        given: "A form with an item group with items for age and age units"
        def formDefinition = formFactory.aFormWithDemographicsItemGroup()
        and: "A subject #subjectId.id with date of birth 01/01/2000"
        assert subjectId
        and: "demographic item query mappings"
        def demographicItemQueryMappings = createDMItemQueryMappings()

        when: "I populate the form for this subject"
        def specification = aPopulationSpecification(formDefinition, subjectId, LocalDate.of(2019, 6, 26), demographicItemQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        def items = populatedForm.itemGroups.find({ it.id.id == "DM_DOB" }).items
        and: "with unmapped item 'DM_DOB.ETHNIC' having no value"
        !items.any({ it.id.id == "DM_DOB.ETHNIC" })
    }

    def "Populates missing items that have mappings for unknown values"() {
        given: "A form with an item group with items for age and age units"
        def formDefinition = formFactory.aFormWithDemographicsItemGroup()
        and: "A subject #subjectId.id with date of birth 01/01/2000 and no gender"
        assert subjectId
        and: "Mappings for item DM_DOB.SEX"
        def demographicItemQueryMappings = createDMItemQueryMappings()

        when: "I populate the form"
        def specification = aPopulationSpecification(formDefinition, subjectId, LocalDate.of(2019, 6, 26), demographicItemQueryMappings)
        def filled = formPopulator().populateForm(specification, formDefinition)

        then: "I expect no unmapped fields to be present"
        filled.formDefinitionId == formDefinition.id
        def items = filled.itemGroups.find({ it.id.id == "DM_DOB" }).items
        and: "the mapped item 'DM_DOB.SEX' to have no value"
        !items.any({ it.id.id == "DM_DOB.SEX" })
    }

    def "Does not populate labValue items with empty result"() {
        given: "A form with an item group with items for glucose code, measurement value and measurement unit"
        def formDefinition = formFactory.aFormWithGlucoseItemGroup()
        and: "A subject #subjectId.id without measurements"
        assert subjectId
        and: "lab value item query mappings"
        def labValueItemQueryMappings = createLBItemQueryMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, labValueItemQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "The gateway returns no results"
        // INFO: Overriding a stub in the 'then' takes precedence over the stub declared in the setup()
        labValueGateway.execute(_ as Query, _ as LocalDate) >> new LabValues(Collections.emptyList())
        and: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "with one populated item group"
        populatedForm.itemGroups.size() == 1
        def itemGroup = populatedForm.itemGroups.get(0)
        def items = itemGroup.items
        and: "An item is generated for the 'LB_GLUC.LBFAST' variable with value 'U' which was projected from the empty data point"
        items.any({ it.id.id == "LB_GLUC.LBFAST" && it.value == new LabeledValue('U') })
        and: "No items are generated for the other variables"
        items.size() == 1

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
    }

    def "Does not populate demographics items with empty result"() {
        given: "A form with an item group with items for age and age units"
        def formDefinition = formFactory.aFormWithDemographicsItemGroup()
        and: "A subject #subjectId.id"
        assert subjectId
        and: "demographic item query mappings"
        def demographicItemQueryMappings = createDMItemQueryMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, demographicItemQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "The gateway returns no results"
        // INFO: Overriding a stub in the 'then' takes precedence over the stub declared in the setup()
        demographicGateway.execute(_ as Query, _ as LocalDate) >> new Demographics(Collections.emptyList())
        and: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "without populated item group"
        populatedForm.itemGroups.size() == 0

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
    }

    def "Does not populate medication items with empty result"() {
        given: "A form with a repeating item group with items for medication names"
        def formDefinition = formFactory.aFormWithRepeatingItemGroup(FormFactory.aRepeatingMedicationItemGroup())
        and: "A subject #subjectId.id"
        assert subjectId
        and: "log medication item query mappings for medication name"
        def medicationQueryMappings = createMedicationItemMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, medicationQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "The gateway returns no results"
        // INFO: Overriding a stub in the 'then' takes precedence over the stub declared in the setup()
        medicationGateway.execute(_ as Query, _ as LocalDate) >> new Medications(Collections.emptyList())
        and: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "without populated item group"
        populatedForm.itemGroups.size() == 0

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
    }

    def "Does not populate vital signs with empty result"() {
        given: "A form with a repeating item group with items for vital sign codes"
        def formDefinition = formFactory.aFormWithRepeatingItemGroup(FormFactory.aRepeatingVitalSignItemGroup())
        and: "A subject #subjectId.id"
        assert subjectId
        and: "vital sign query mappings"
        def vitalSignQueryMappings = createVitalSignItemMappings()

        when: "I populate the form with reference date #referenceDate for subject #subjectId.id"
        def specification = aPopulationSpecification(formDefinition, subjectId, referenceDate, vitalSignQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "The gateway returns no results"
        // INFO: Overriding a stub in the 'then' takes precedence over the stub declared in the setup()
        vitalSignGateway.findAll(_ as Criteria, _ as LocalDate) >> []
        and: "I expect the form to be populated"
        populatedForm.formDefinitionId == formDefinition.id
        and: "without populated item group"
        populatedForm.itemGroups.size() == 0

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
    }

    def "Can populate a form with a local lab reference"() {
        given: "A form with a local lab reference"
        def formDefinition = formFactory.aFormWithLocalLabReference()
        and: "A subject"
        assert subjectId
        and: "demographic item query mappings"
        def demographicItemQueryMappings = createDMItemQueryMappings()

        when: "I populate the form for the subject"
        def specification = aPopulationSpecification(formDefinition, subjectId, LocalDate.now(), demographicItemQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then:
        populatedForm.localLab == formDefinition.localLab
    }

    def "Demographic populated items contain the original data point"() {
        given: "A form with an itemgroup with items for demographics"
        def formDefinition = formFactory.aFormWithDemographicsItemGroup()
        and: "A subject with demographic data points"
        assert subjectId
        and: "demographic item query mappings"
        def demographicItemQueryMappings = createDMItemQueryMappings()

        when: "I populate the form for the subject"
        def specification = aPopulationSpecification(formDefinition, subjectId, LocalDate.now(), demographicItemQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "The populated items contain the original data point"
        populatedForm.itemGroups.size() == 1
        PopulatedItemGroup itemGroup = populatedForm.itemGroups.get(0)
        List<PopulatedItem> items = itemGroup.items
        items.size() == 2
        items.any({ it.id.id == "DM_DOB.AGE" && it.dataPoint != null })
        items.any({ it.id.id == "DM_DOB.AGEU" && it.dataPoint != null })

        where:
        referenceDate = LocalDate.of(2019, 6, 26)
        expectedAge = new LabeledValue("19")
        expectedUnits = new LabeledValue("YEARS")
    }

    def "Laboratory populated items contain the original data point"() {
        given: "A form with an item group with items for glucose code, measurement value and measurement unit"
        def formDefinition = formFactory.aFormWithGlucoseItemGroup()
        and: "A subject with a measurement for glucose"
        assert subjectId
        and: "lab value item query mappings"
        def labValueItemQueryMappings = createLBItemQueryMappings()

        when: "I populate the form for the subject"
        def specification = aPopulationSpecification(formDefinition, subjectId, LocalDate.of(2019, 6, 26), labValueItemQueryMappings)
        PopulatedForm populatedForm = formPopulator().populateForm(specification, formDefinition)

        then: "The populated items contain the original data point"
        populatedForm.itemGroups.size() == 1
        PopulatedItemGroup itemGroup = populatedForm.itemGroups.get(0)
        List<PopulatedItem> items = itemGroup.items
        items.size() == 4
        items.any({ it.id.id == "LB_GLUC.LBLOINC" && it.dataPoint != null })
        items.any({ it.id.id == "LB_GLUC.LBORRES" && it.dataPoint != null })
        items.any({ it.id.id == "LB_GLUC.LBORRESU" && it.dataPoint != null })
        items.any({ it.id.id == "LB_GLUC.LBFAST" && it.dataPoint != null })
    }

    private static PopulationSpecification aPopulationSpecification(FormDefinition form, SubjectId subjectId,
                                                                    LocalDate referenceDate, Map<ItemDefinitionId, ItemQueryMappingJson> studyItemQueryMappings) {
        PopulationSpecification.newBuilder()

                .withStudyItemQueryMappings(new ItemQueryMappings(studyItemQueryMappings))
                .withReferenceDate(referenceDate)
                .withSubjectId(subjectId)
                .build()
    }
}
