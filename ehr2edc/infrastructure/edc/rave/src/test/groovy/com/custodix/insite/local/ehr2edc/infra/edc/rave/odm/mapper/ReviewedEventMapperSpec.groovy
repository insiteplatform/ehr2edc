package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.mapper

import com.custodix.insite.local.ehr2edc.infra.edc.rave.AbstractSpecification
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.ItemData
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.ItemGroupData
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.StudyEventData
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.TransactionType
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItem
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItemGroup
import com.custodix.insite.local.ehr2edc.vocabulary.*

import static com.custodix.insite.local.ehr2edc.submitted.SubmittedLabeledValueObjectMother.aDefaultSubmittedLabeledValueBuilder

class ReviewedEventMapperSpec extends AbstractSpecification {
    def "Adds no attributes to non-repeating item groups and their data"() {
        given: "A submitted form containing a regular item group"
        SubmittedItemGroup rig = anItemGroup(false)
        SubmittedEvent event = eventContaining(rig)
        and: "An edc connection"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aDefaultStudyConnection()

        when: "I map the form to its ODM representation"
        StudyEventData data = new ReviewedEventMapper(event, connection).mapStudyEventData()

        then: "The item group has no repeatKey or transactionType"
        ItemGroupData group = data.formData[0].itemGroupData[0]
        group.itemGroupRepeatKey == null
        group.transactionType == null

        and: "The items have no transactionType"
        group.itemData.each {
            assert it.transactionType != TransactionType.CONTEXT
        }
    }

    def "Adds appropriate attributes to repeating item groups and their data"() {
        given: "A submitted form containing a repeating item group"
        SubmittedItemGroup rig = anItemGroup(true)
        SubmittedEvent event = eventContaining(rig)
        and: "An edc connection"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aDefaultStudyConnection()

        when: "I map the form to its ODM representation"
        StudyEventData data = new ReviewedEventMapper(event, connection).mapStudyEventData()

        then: "The item group has a repeatKey and transactionType"
        ItemGroupData group = data.formData[0].itemGroupData[0]
        group.itemGroupRepeatKey == ItemGroupData.REPEAT_KEY
        group.transactionType == ItemGroupData.REPEAT_TRANSACTION_TYPE

        and: "The 'LBTEST' item has a transactionType Context"
        group.itemData.find({ it.itemOID == "LBTEST" }).transactionType == TransactionType.CONTEXT

        and: "The 'CMTRT' item has a transactionType Context"
        group.itemData.find({ it.itemOID == "CMTRT" }).transactionType == TransactionType.CONTEXT

        and: "The other items have no transactionType"
        group.itemData.find({ it.itemOID == "AN_ID" }).transactionType == null
    }

    def "Maps an item with unit reference to be submitted to EDC correctly"() {
        given: "A submitted form containing an item group containing an item with unit reference to submitted to edc"
        SubmittedItemGroup rig = anItemGroupContainingAnItemWithUnitReference(true)
        SubmittedEvent event = eventContaining(rig)
        and: "An edc connection"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aDefaultStudyConnection()

        when: "I map the event to its ODM representation"
        StudyEventData data = new ReviewedEventMapper(event, connection).mapStudyEventData()

        then: "The item has the correct unit reference"
        ItemData item = data.formData[0].itemGroupData[0].itemData[0]
        item.measurementUnitRef.measurementUnitOID == AbstractSpecification.MEASUREMENT_UNIT_OID
    }

    def "Maps an item with unit reference to be NOT submitted to EDC correctly"() {
        given: "A submitted form containing an item group containing an item with unit reference to be NOT submitted to edc"
        SubmittedItemGroup rig = anItemGroupContainingAnItemWithUnitReference(false)
        SubmittedEvent event = eventContaining(rig)
        and: "An edc connection"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aDefaultStudyConnection()

        when: "I map the event to its ODM representation"
        StudyEventData data = new ReviewedEventMapper(event, connection).mapStudyEventData()

        then: "The item has the correct unit reference"
        ItemData item = data.formData[0].itemGroupData[0].itemData[0]
        item.measurementUnitRef == null
    }

    def "Dont map items that should not be submitted to edc"() {
        given: "A submitted form containing an item group containing an item that is for informational purposes only"
        SubmittedItemGroup rig = anItemGroupWithOnlyAReadOnlyItem()
        SubmittedEvent event = eventContaining(rig)
        and: "An edc connection"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aDefaultStudyConnection()

        when: "I map the event to its ODM representation"
        StudyEventData data = new ReviewedEventMapper(event, connection).mapStudyEventData()

        then: "There is no item"
        data.formData[0].itemGroupData[0].itemData.size() == 0
    }

    static SubmittedItemGroup anItemGroupWithOnlyAReadOnlyItem() {
        SubmittedItem item = anItemThatShouldNotBeSubmittedToEDC("someId", "someValue", false)
        return SubmittedItemGroup.newBuilder()
                .withId(ItemGroupDefinitionId.of("itemGroupID"))
                .withName("itemGroupName")
                .withSubmittedItems([item])
                .withRepeating(false)
                .withPopulatedItemGroupId(ItemGroupId.of("populatedItemGroupId"))
                .build()
    }

    static anItemThatShouldNotBeSubmittedToEDC(String id, String value, boolean key) {
        return SubmittedItem.newBuilder()
                .withId(ItemDefinitionId.of(id))
                .withValue(aDefaultSubmittedLabeledValueBuilder().withValue(value).build())
                .withKey(key)
                .withSubmittedToEDC(false)
                .withPopulatedItemId(ItemId.of("instance-" + id))
                .build()
    }


}
