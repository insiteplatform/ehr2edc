package com.custodix.insite.local.ehr2edc.infra.edc.rave


import com.custodix.insite.local.ehr2edc.submitted.*
import com.custodix.insite.local.ehr2edc.vocabulary.*
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.FileCopyUtils
import spock.lang.Specification

import java.time.Instant

import static com.custodix.insite.local.ehr2edc.submitted.SubmittedLabeledValueObjectMother.aDefaultSubmittedLabeledValueBuilder
import static java.nio.charset.StandardCharsets.UTF_8

@ContextConfiguration(classes = RaveConfiguration)
abstract class AbstractSpecification extends Specification {

    static final def STUDY_ID = StudyId.of("SID_001")
    static final String MEASUREMENT_UNIT_OID = "13899.CM/IN.cm"
    private static final Instant SUBMITTED_DATE = Instant.parse("2020-01-07T08:00:00Z")

    static String readSample(String path) {
        def sampleResource = new ClassPathResource(path)
        addWiremockXmlRequestMatchingXmlPlaceholders(FileCopyUtils.copyToByteArray(sampleResource.inputStream))
    }

    private static String addWiremockXmlRequestMatchingXmlPlaceholders(byte[] fileContent) {
        new String(fileContent, UTF_8).replace("\${xmlunit.ignore}", "[[xmlunit.ignore]]")
    }

    static SubmittedItemGroup anItemGroup(boolean isRepeating) {
        SubmittedItem lab_key = anItem("LBTEST", "value", true);
        SubmittedItem med_key = anItem("CMTRT", "value", true);
        SubmittedItem other = anItem("AN_ID", "value", false);
        return SubmittedItemGroup.newBuilder()
                .withId(ItemGroupDefinitionId.of("itemGroupId"))
                .withName("itemGroupName")
                .withSubmittedItems([lab_key, med_key, other])
                .withRepeating(isRepeating)
                .withPopulatedItemGroupId(ItemGroupId.of("populatedItemGroupId"))
                .build()
    }

    static SubmittedItemGroup anItemGroupContainingAnItemWithUnitReference(boolean unitToBeSubmittedToEdc) {
        SubmittedItem item = anItemWithMeasurementUnitReference("cm", "177", MEASUREMENT_UNIT_OID, unitToBeSubmittedToEdc)
        return SubmittedItemGroup.newBuilder()
                .withId(ItemGroupDefinitionId.of("itemGroupID"))
                .withName("itemGroupName")
                .withSubmittedItems([item])
                .withRepeating(false)
                .withPopulatedItemGroupId(ItemGroupId.of("populatedItemGroupId"))
                .build()
    }

    static SubmittedItem anItem(String id, String value, boolean key) {
        return anItemBuilder(id, value, key).build()
    }

    static SubmittedItem.Builder anItemBuilder(String id, String value, boolean key) {
        return SubmittedItem.newBuilder()
                .withId(ItemDefinitionId.of(id))
                .withValue(aDefaultSubmittedLabeledValueBuilder().withValue(value).build())
                .withKey(key)
                .withSubmittedToEDC(true)
                .withPopulatedItemId(ItemId.of("instance-" + id))
    }

    static anItemWithMeasurementUnitReference(String id, String value, String unitId, boolean unitToBeSubmittedToEdc) {
        return SubmittedItem.newBuilder()
                .withId(ItemDefinitionId.of(id))
                .withValue(aDefaultSubmittedLabeledValueBuilder().withValue(value).build())
                .withSubmittedMeasurementUnitReference(SubmittedMeasurementUnitReference.newBuilder()
                        .withId(unitId)
                        .withSubmittedToEDC(unitToBeSubmittedToEdc)
                        .build())
                .withSubmittedToEDC(true)
                .build()
    }

    static SubmittedEvent eventContaining(SubmittedItemGroup submittedItemGroup) {
        SubmittedEventObjectMother.aDefaultSubmittedEventBuilder()
                .withStudyId(STUDY_ID)
                .withSubjectId(SubjectId.generate())
                .withSubmittedForms([SubmittedForm.newBuilder()
                                             .withName("FORM-NAME")
                                             .withFormDefinitionId(FormDefinitionId.of("FORM"))
                                             .withSubmittedItemGroups([submittedItemGroup])
                                             .withPopulatedFormId(FormId.of("populatedFormId"))
                                             .build()])
                .withEventDefinitionId(EventDefinitionId.of("EVT"))
                .withEventParentId("EVT-PARENT")
                .withPopulatedEventId(EventId.of("populatedEventId"))
                .withSubmittedDate(SUBMITTED_DATE)
                .build()

    }

    static SubmittedEvent anEvent(List<SubmittedForm> forms) {
        return anEventBuilder(forms).build()
    }

    static SubmittedEvent.Builder anEventBuilder(List<SubmittedForm> forms) {
        return SubmittedEventObjectMother.aDefaultSubmittedEventBuilder()
                .withEventDefinitionId(EventDefinitionId.of("EVT"))
                .withStudyId(STUDY_ID)
                .withSubjectId(SubjectId.generate())
                .withSubmittedForms(forms)
                .withPopulatedEventId(EventId.of("instance-EVT"))
                .withSubmittedDate(SUBMITTED_DATE)
                .withEventParentId(null)
    }

    static SubmittedForm aForm(String id, List<SubmittedItemGroup> groups) {
        return aFormBuilder(id, groups).build()
    }

    static SubmittedForm.Builder aFormBuilder(String id, List<SubmittedItemGroup> groups) {
        return SubmittedForm.newBuilder()
                .withFormDefinitionId(FormDefinitionId.of(id))
                .withName("${id}-NAME")
                .withSubmittedItemGroups(groups)
                .withPopulatedFormId(FormId.of("instance-${id}"))
    }

    static SubmittedItemGroup anItemGroup(String id, List<SubmittedItem> items) {
        return SubmittedItemGroup.newBuilder()
                .withId(ItemGroupDefinitionId.of(id))
                .withName("${id}-NAME")
                .withSubmittedItems(items)
                .withPopulatedItemGroupId(ItemGroupId.of("instance-${id}"))
                .build()
    }
}
