package com.custodix.insite.local.ehr2edc.usecase.impl.getformoverview

import com.custodix.insite.local.ehr2edc.metadata.model.ItemLabel
import com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroup
import com.custodix.insite.local.ehr2edc.populator.PopulatedMeasurementUnitReference
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.Label
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue
import com.custodix.insite.local.ehr2edc.vocabulary.*
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.populator.PopulatedEventObjectMother.aDefaultEvent
import static com.custodix.insite.local.ehr2edc.populator.PopulatedEventObjectMother.aDefaultEventBuilder
import static com.custodix.insite.local.ehr2edc.populator.PopulatedFormObjectMother.aDefaultFormBuilder
import static com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroupObjectMother.aDefaultItemGroupBuilder
import static com.custodix.insite.local.ehr2edc.populator.PopulatedItemObjectMother.aDefaultPopulatedItemBuilder

class FormOverviewFactorySpec extends Specification {

    public static final LocalDate TWO_DAYS_AGO = LocalDate.now().minusDays(2)
    FormOverviewFactory formOverviewFactory = new FormOverviewFactory()

    def "Maps form properties correctly"(String instanceId, FormDefinitionId formDefinitionId, String name, LocalDate referenceDate, Instant populationTime) {
        given:
        "event with " +
                "referenceDate '#referenceDate'," +
                "populationTime '#populationTime'"
        def event = aDefaultEventBuilder()
                .withReferenceDate(referenceDate)
                .withPopulationTime(populationTime)
                .build()
        and:
        "form with " +
                "instanceId '#instanceId'" +
                "formDefinitionId '#formDefinitionId'," +
                "name '#name'," +
                "referenceDate '#referenceDate'," +
                "populationTime '#populationTime'"
        def form = aDefaultFormBuilder()
                .withInstanceId(FormId.of(instanceId))
                .withFormDefinitionId(formDefinitionId)
                .withName(name)
                .build()

        when: "creating overview form"
        def formOverview = formOverviewFactory.createForm(form, event)

        then:
        "overview form is created with properties:" +
                "id '#instanceId'" +
                "name '#name'," +
                "referenceDate '#referenceDate'," +
                "populationTime '#populationTime'"

        formOverview.id.id == instanceId
        formOverview.name == name
        formOverview.referenceDate == referenceDate
        formOverview.populationTime == populationTime

        where:
        instanceId          | formDefinitionId                 | name               | referenceDate | populationTime || _
        "form-instance-123" | FormDefinitionId.of("1235-1235") | "name of the form" | TWO_DAYS_AGO  | Instant.now()  || _
    }

    def "Maps item groups properties correctly"(String instanceId, String itemGroupDefinitionId, String name) {
        given: "An event"
        def event = aDefaultEvent()
        and:
        "a form with item group with properties: " +
                "instanceId '#instanceId'" +
                "itemGroupDefinitionId '#itemGroupDefinitionId',  " +
                "name '#name'"
        def itemGroup = aDefaultItemGroupBuilder()
                .withInstanceId(ItemGroupId.of(instanceId))
                .withDefinition(PopulatedItemGroup.Definition.newBuilder()
                        .withId(ItemGroupDefinitionId.of(itemGroupDefinitionId))
                        .withName(name)
                        .build())
                .build()
        def form = aDefaultFormBuilder().withItemGroups(Collections.singletonList(itemGroup)).build()

        when: "creating overview form"
        def formOverview = formOverviewFactory.createForm(form, event)

        then: "overview form is created with one item group:"
        formOverview.itemGroups.size() == 1
        and: "the item group has the following properties: "
        "id '#instanceId'," +
                "name '#name'"

        formOverview.itemGroups[0].id.id == instanceId
        formOverview.itemGroups[0].name == name

        where:
        instanceId     | itemGroupDefinitionId   | name                      || _
        "item-group-1" | "this my item group id" | "this my item group name" || _
    }

    def "Maps item properties correctly"(String instanceId, String itemDefinitionId, String name, LabeledValue value, PopulatedMeasurementUnitReference unit, boolean readOnly, String expectedValue, String expectedValueLabel, String expectedUnit, boolean exportable) {
        given: "An event"
        def event = aDefaultEvent()
        and:
        "a form item with properties: " +
                "instanceId '#instanceId'" +
                "itemDefinitionId '#itemGroupId', " +
                "name '#name', " +
                "value '#value', unit #unit and " +
                "read-only '#readOnly'"
        def item = aDefaultPopulatedItemBuilder()
                .withInstanceId(ItemId.of(instanceId))
                .withId(ItemDefinitionId.of(itemDefinitionId))
                .withLabel(ItemLabel.newBuilder().withName(name).build())
                .withValue(value)
                .withReadOnly(readOnly)
                .withMeasurementUnitReference(unit)
                .build()
        def itemGroup = aDefaultItemGroupBuilder()
                .withItems(Collections.singletonList(item))
                .build()
        def form = aDefaultFormBuilder().withItemGroups(Collections.singletonList(itemGroup)).build()

        when: "creating overview form"
        def formOverview = formOverviewFactory.createForm(form, event)

        then: "overview form is created with one item:"
        formOverview.itemGroups.size() == 1
        formOverview.itemGroups[0].items.size() == 1
        and: "the item  has the following properties: "
        "id '#instanceId', " +
                "name '#name', " +
                "value '#expectedValue', " +
                "valueLabel '#expectedValueLabel', " +
                "unit '#expectedUnit', " +
                "exportable '#exportable'"

        formOverview.itemGroups[0].items[0].id.id == instanceId
        formOverview.itemGroups[0].items[0].name == name
        formOverview.itemGroups[0].items[0].value == expectedValue
        formOverview.itemGroups[0].items[0].valueLabel == expectedValueLabel
        formOverview.itemGroups[0].items[0].unit == expectedUnit
        formOverview.itemGroups[0].items[0].exportable == exportable

        where:
        instanceId        | itemDefinitionId  | name                | value                                                                                | unit                                                                | readOnly || expectedValue        | expectedValueLabel | expectedUnit | exportable
        "item-instance-1" | "this my item id" | "this my item name" | new LabeledValue("this my item value")                                               | PopulatedMeasurementUnitReference.newBuilder().withId("kg").build() | false    || "this my item value" | null               | "kg"         | true
        "item-instance-1" | "this my item id" | "this my item name" | new LabeledValue("this my item value")                                               | null                                                                | true     || "this my item value" | null               | null         | false
        "item-instance-1" | "this my item id" | "this my item name" | new LabeledValue("this my item value", [new Label(Locale.ENGLISH, "English label")]) | null                                                                | true     || "this my item value" | "English label"    | null         | false
        "item-instance-1" | "this my item id" | "this my item name" | new LabeledValue("this my item value", [new Label(Locale.FRENCH, "French label")])   | null                                                                | true     || "this my item value" | null               | null         | false
    }
}