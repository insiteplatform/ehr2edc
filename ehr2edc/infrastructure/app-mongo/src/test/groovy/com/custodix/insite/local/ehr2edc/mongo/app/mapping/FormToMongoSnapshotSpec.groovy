package com.custodix.insite.local.ehr2edc.mongo.app.mapping

import com.custodix.insite.local.ehr2edc.mongo.app.document.FormDocument
import com.custodix.insite.local.ehr2edc.populator.PopulatedItemObjectMother
import com.custodix.insite.local.ehr2edc.populator.PopulatedMeasurementUnitReference
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue
import com.custodix.insite.local.ehr2edc.vocabulary.*
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroupObjectMother.aDefaultItemGroupBuilder
import static com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroupObjectMother.aDefaultItemGroupDefinitionBuilder
import static com.custodix.insite.local.ehr2edc.populator.PopulatedFormObjectMother.aDefaultFormBuilder
import static java.util.Collections.singletonList

class FormToMongoSnapshotSpec extends Specification {

    @Unroll
    def "Converting form with instanceId '#instanceId', formDefinitionId '#formDefinitionId', name '#name', localLab '#localLab' correctly"() {
        given: "a form with instanceId '#instanceId', formDefinitionId '#formDefinitionId', name '#name', localLab '#localLab'"
        def form = aDefaultFormBuilder()
                .withInstanceId(FormId.of(instanceId))
                .withFormDefinitionId(FormDefinitionId.of(formDefinitionId))
                .withName(name)
                .withLocalLab(localLab)
                .build()

        when: "converting to mongo snapshot"
        def formMongoSnapshot = FormDocument.restoreFrom(form)

        then: "form instance id is converted to '#instanceId'"
        formMongoSnapshot.getInstanceId() == instanceId
        and: "form definition id is converted to '#formDefinitionId'"
        formMongoSnapshot.getFormDefinitionId() == formDefinitionId
        and: "name is converted to '#name'"
        formMongoSnapshot.getName() == name
        and: "local lab reference is converted to '#localLab'"
        formMongoSnapshot.getLocalLab() == expectedLocalLab

        where:
        instanceId       | formDefinitionId | name        | localLab            || expectedLocalLab
        "instanceId-123" | '098-123'        | "Form name" | LabName.of("LAB_1") || "LAB_1"
        "instanceId-123" | '098-123'        | "Form name" | null                || null
    }

    @Unroll
    def "Converting a null item group list correctly"() {
        given: "a a null item group list"
        def form = aDefaultFormBuilder()
                .withItemGroups(null)
                .build()

        when: "converting to mongo snapshot"
        def formMongoSnapshot = FormDocument.restoreFrom(form)

        then: "the item group list is empty"
        formMongoSnapshot.getItemGroups().size() == 0

        where:
        itemGroupId | _
        "123-12309" | _
    }

    @Unroll
    def "Converting a null item list correctly"() {
        given: "a a null item list"
        def form = aDefaultFormBuilder()
                .withItemGroups(singletonList(aDefaultItemGroupBuilder().withItems(null).build()))
                .build()

        when: "converting to mongo snapshot"
        def formMongoSnapshot = FormDocument.restoreFrom(form)

        then: "the item  list is empty"
        formMongoSnapshot.getItemGroups().size() == 1
        formMongoSnapshot.getItemGroups()[0].getItems().size() == 0

        where:
        itemGroupId | _
        "123-12309" | _
    }

    @Unroll
    def "Converting item group id correctly"(final String itemGroupId) {
        given: "a item group with itemGroupId '#itemGroupId'"
        def definition = aDefaultItemGroupDefinitionBuilder().withId(ItemGroupDefinitionId.of(itemGroupId)).build()
        def form = aDefaultFormBuilder()
                .withItemGroups(singletonList(aDefaultItemGroupBuilder().withDefinition(definition).build()))
                .build()

        when: "converting to mongo snapshot"
        def formMongoSnapshot = FormDocument.restoreFrom(form)

        then: "item group id is converted to '#itemGroupId'"
        formMongoSnapshot.getItemGroups().size() == 1
        formMongoSnapshot.getItemGroups()[0].definition.id == itemGroupId

        where:
        itemGroupId | _
        "123-12309" | _
    }

    @Unroll
    def "Convert an item group with index correctly"() {
        given: "a item group with index '#index'"
        def definition = aDefaultItemGroupDefinitionBuilder().withId(ItemGroupDefinitionId.of("123-12309")).build()
        def form = aDefaultFormBuilder()
                .withItemGroups(singletonList(aDefaultItemGroupBuilder().withDefinition(definition)
                        .withIndex(index).build()))
                .build()


        when: "converting to mongo snapshot"
        def formMongoSnapshot = FormDocument.restoreFrom(form)

        then: "I expect #index as index"
        formMongoSnapshot.getItemGroups()[0].getIndex() == index

        where:
        index | _
        "5"   | _
    }

    @Unroll
    def "Converting item correctly"(final String itemId, final LabeledValue value) {
        given: "an item with itemId '#itemId'"
        def item = PopulatedItemObjectMother.aDefaultPopulatedItemBuilder()
                .withId(ItemDefinitionId.of(itemId))
                .withValue(value)
                .withKey(true)
                .build()
        def form = aDefaultFormBuilder()
                .withItemGroups(singletonList(aDefaultItemGroupBuilder().withItems(singletonList(item)).build()))
                .build()

        when: "converting to mongo snapshot"
        def formMongoSnapshot = FormDocument.restoreFrom(form)

        then: "item is converted with id '#itemId' and value '#value'"
        formMongoSnapshot.getItemGroups().size() == 1
        formMongoSnapshot.getItemGroups()[0].getItems().size() == 1
        formMongoSnapshot.getItemGroups()[0].getItems()[0].id == itemId
        formMongoSnapshot.getItemGroups()[0].getItems()[0].labeledValue == value
        formMongoSnapshot.getItemGroups()[0].getItems()[0].key

        where:
        itemId      | value                      | _
        "123-12309" | new LabeledValue("10.097") | _
    }

    @Unroll
    def "Converting item with measurement unit reference correctly"(final String itemId, final LabeledValue value, final String measurementUnitReferenceId, final boolean readOnly) {
        given: "an item with itemId '#itemId', value '#value', measurement unit reference id '#measurementUnitReferenceId' and readOnly '#readOnly'"
        def item = PopulatedItemObjectMother.aDefaultPopulatedItemBuilder()
                .withId(ItemDefinitionId.of(itemId))
                .withValue(value)
                .withMeasurementUnitReference(PopulatedMeasurementUnitReference.newBuilder().withId(measurementUnitReferenceId).withReadOnly(readOnly).build())
                .build()
        def form = aDefaultFormBuilder()
                .withItemGroups(singletonList(aDefaultItemGroupBuilder().withItems(singletonList(item)).build()))
                .build()

        when: "converting to mongo snapshot"
        def formMongoSnapshot = FormDocument.restoreFrom(form)

        then: "item is converted with id '#itemId' and value '#value' and measurementUnitReferenceId '#measurementUnitReferenceId' and readOnly '#readOnly'"
        formMongoSnapshot.getItemGroups().size() == 1
        formMongoSnapshot.getItemGroups()[0].getItems().size() == 1
        formMongoSnapshot.getItemGroups()[0].getItems()[0].id == itemId
        formMongoSnapshot.getItemGroups()[0].getItems()[0].labeledValue == value
        formMongoSnapshot.getItemGroups()[0].getItems()[0].outputUnit == !readOnly
        formMongoSnapshot.getItemGroups()[0].getItems()[0].measurementUnitReference.get().id == measurementUnitReferenceId

        where:
        itemId      | value                      | measurementUnitReferenceId | readOnly | _
        "123-12309" | new LabeledValue("10.097") | "13899.CM/IN.cm"           | true     | _
    }

    @Unroll
    def "Converting read-only item correctly"(final String itemId, final LabeledValue value) {
        given: "a read-only item with itemId '#itemId'"
        def item = PopulatedItemObjectMother.aDefaultPopulatedItemBuilder()
                .withId(ItemDefinitionId.of(itemId))
                .withValue(value)
                .withReadOnly(true)
                .build()
        def form = aDefaultFormBuilder()
                .withItemGroups(singletonList(aDefaultItemGroupBuilder().withItems(singletonList(item)).build()))
                .build()

        when: "converting to mongo snapshot"
        def formMongoSnapshot = FormDocument.restoreFrom(form)

        then: "read-only item is converted with id '#itemId' and value '#value'"
        formMongoSnapshot.getItemGroups().size() == 1
        formMongoSnapshot.getItemGroups()[0].getItems().size() == 1
        with(formMongoSnapshot.getItemGroups()[0].getItems()[0]) {
            it.id == itemId
            it.labeledValue == value
            it.readOnly
        }

        where:
        itemId      | value                      | _
        "123-12309" | new LabeledValue("10.097") | _
    }
}
