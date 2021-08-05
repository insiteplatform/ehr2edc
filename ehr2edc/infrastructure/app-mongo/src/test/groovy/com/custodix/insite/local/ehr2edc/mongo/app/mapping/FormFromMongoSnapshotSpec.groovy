package com.custodix.insite.local.ehr2edc.mongo.app.mapping

import com.custodix.insite.local.ehr2edc.mongo.app.document.ItemGroupDocument
import com.custodix.insite.local.ehr2edc.mongo.app.document.LabelDocument
import com.custodix.insite.local.ehr2edc.mongo.app.document.LabeledValueMongoSnapshot
import com.custodix.insite.local.ehr2edc.mongo.app.document.MeasurementUnitReferenceDocument
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.Label
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.FormId
import com.custodix.insite.local.ehr2edc.vocabulary.LabName
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.mongo.app.document.FormMongoSnapshotObjectMother.aDefaultFormMongoSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.mongo.app.document.ItemGroupMongoSnapshotObjectMother.aDefaultItemGroupMongoSnapshot
import static com.custodix.insite.local.ehr2edc.mongo.app.document.ItemGroupMongoSnapshotObjectMother.aDefaultItemGroupMongoSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.mongo.app.document.ItemMongoSnapshotObjectMother.aDefaultItemMongoSnapshotBuilder
import static java.util.Collections.singletonList

class FormFromMongoSnapshotSpec extends Specification {

    @Unroll
    def "Converting form mongo snapshot with instanceId '#instanceId', formDefinitionId '#formDefinitionId', name '#name', localLab '#localLab'"() {
        given: "a form mongo snapshot with instanceId '#instanceId', formDefinitionId '#formDefinitionId', name '#name', localLab '#localLab'"
        def formMongoSnapshot = aDefaultFormMongoSnapshotBuilder()
                .withInstanceId(instanceId)
                .withFormDefinitionId(formDefinitionId)
                .withName(name)
                .withLocalLab(localLab)
                .build()

        when: "converting to form"
        def form = formMongoSnapshot.toForm()

        then: "form instance id is converted to '#instanceId'"
        form.getInstanceId() == FormId.of(instanceId)
        then: "form definition id is converted to '#formDefinitionId'"
        form.getFormDefinitionId() == FormDefinitionId.of(formDefinitionId)
        and: "name is converted to '#name'"
        form.getName() == name
        and: "local lab reference is converted to '#expectedLocalLab'"
        form.getLocalLab() == expectedLocalLab

        where:
        instanceId       | formDefinitionId | name        | localLab || expectedLocalLab
        "instanceId-123" | "098-123"        | "Form name" | "LAB_1"  || LabName.of("LAB_1")
        "instanceId-123" | "098-123"        | "Form name" | null     || null
    }

    @Unroll
    def "Converting item group id correctly"(final String itemGroupId) {
        given: "a item group with itemGroupId '#itemGroupId'"
        def itemGroup = aDefaultItemGroupMongoSnapshot()
        def itemGroupDefinition = ItemGroupDocument.DefinitionMongoSnapshot
                .newBuilder(itemGroup.definition)
                .withId(itemGroupId)
                .build()
        def formMongoSnapshot = aDefaultFormMongoSnapshotBuilder()
                .withItemGroups(singletonList(ItemGroupDocument.newBuilder(itemGroup).withDefinition(itemGroupDefinition).build()))
                .build()

        when: "converting to form"
        def form = formMongoSnapshot.toForm()

        then: "item group id is converted to '#itemGroupId'"
        form.getItemGroups().size() == 1
        form.getItemGroups()[0].id.id == itemGroupId

        where:
        itemGroupId | _
        "123-12309" | _
    }

    @Unroll
    def "Converting item correctly"(final String itemId, final LabeledValueMongoSnapshot value) {
        given: "an item with itemId '#itemId'"
        def item = aDefaultItemMongoSnapshotBuilder()
                .withId(itemId)
                .withLabeledValue(value)
                .withKey(true)
                .build()
        def formMongoSnapshot = aDefaultFormMongoSnapshotBuilder()
                .withItemGroups(singletonList(aDefaultItemGroupMongoSnapshotBuilder().withItems(singletonList(item)).build()))
                .build()

        when: "converting to form"
        def form = formMongoSnapshot.toForm()

        then: "item is converted with id '#itemId' and value '#value'"
        form.getItemGroups().size() == 1
        form.getItemGroups()[0].getItems().size() == 1
        with(form.getItemGroups()[0].getItems()[0]) {
            it.id.id == itemId
            it.value == new LabeledValue("10.097", singletonList(new Label(Locale.ENGLISH, "label")))
            it.key
        }

        where:
        itemId      | value                                                                     | _
        "123-12309" | LabeledValueMongoSnapshot.newBuilder().withValue("10.097").withLabels(singletonList(
                LabelDocument.newBuilder().withLocale("en").withText("label").build())).build() | _
    }

    @Unroll
    def "Converting item with legacy 'value' field correctly"(final String itemId, final String value) {
        given: "an item with itemId '#itemId'"
        def item = aDefaultItemMongoSnapshotBuilder()
                .withId(itemId)
                .withValue(value)
                .withKey(true)
                .build()
        def formMongoSnapshot = aDefaultFormMongoSnapshotBuilder()
                .withItemGroups(singletonList(aDefaultItemGroupMongoSnapshotBuilder().withItems(singletonList(item)).build()))
                .build()

        when: "converting to form"
        def form = formMongoSnapshot.toForm()

        then: "item is converted with id '#itemId' and value '#value'"
        form.getItemGroups().size() == 1
        form.getItemGroups()[0].getItems().size() == 1
        with(form.getItemGroups()[0].getItems()[0]) {
            it.id.id == itemId
            it.value == new LabeledValue("10.097")
            it.key
        }

        where:
        itemId      | value    | _
        "123-12309" | "10.097" | _
    }

    @Unroll
    def "Converting item with measurement unit reference correctly"(final String itemId, final LabeledValueMongoSnapshot value, final String measurementUnitReferenceId) {
        given: "an item with itemId '#itemId', value '#value' and measurement unit reference id '#measurementUnitReferenceId'"
        def measurementUnitReference = MeasurementUnitReferenceDocument.newBuilder()
                .withId(measurementUnitReferenceId)
                .build()
        def item = aDefaultItemMongoSnapshotBuilder()
                .withId(itemId)
                .withLabeledValue(value)
                .withMeasurementUnitReference(measurementUnitReference)
                .build()
        def formMongoSnapshot = aDefaultFormMongoSnapshotBuilder()
                .withItemGroups(singletonList(aDefaultItemGroupMongoSnapshotBuilder().withItems(singletonList(item)).build()))
                .build()

        when: "converting to form"
        def form = formMongoSnapshot.toForm()

        then: "item is converted with id '#itemId' and value '#value' and measurementUnitReferenceId '#measurementUnitReferenceId'"
        form.getItemGroups().size() == 1
        form.getItemGroups()[0].getItems().size() == 1
        with(form.getItemGroups()[0].getItems()[0]) {
            it.id.id == itemId
            it.value == new LabeledValue("10.097")
            it.measurementUnitReference.get().id == measurementUnitReferenceId
        }

        where:
        itemId      | value                                                              | measurementUnitReferenceId | _
        "123-12309" | LabeledValueMongoSnapshot.newBuilder().withValue("10.097").build() | "13899.CM/IN.cm"           | _
    }

    @Unroll
    def "Converting read-only item correctly"(final String itemId, final LabeledValueMongoSnapshot value) {
        given: "a read-only item with itemId '#itemId'"
        def item = aDefaultItemMongoSnapshotBuilder()
                .withId(itemId)
                .withLabeledValue(value)
                .withReadOnly(true)
                .build()
        def formMongoSnapshot = aDefaultFormMongoSnapshotBuilder()
                .withItemGroups(singletonList(aDefaultItemGroupMongoSnapshotBuilder().withItems(singletonList(item)).build()))
                .build()

        when: "converting to form"
        def form = formMongoSnapshot.toForm()

        then: "read-only item is converted with id '#itemId' and value '#value'"
        form.getItemGroups().size() == 1
        form.getItemGroups()[0].getItems().size() == 1
        with(form.getItemGroups()[0].getItems()[0]) {
            it.id.id == itemId
            it.value == new LabeledValue("10.097")
            it.readOnly
        }

        where:
        itemId      | value                                                              | _
        "123-12309" | LabeledValueMongoSnapshot.newBuilder().withValue("10.097").build() | _
    }
}
