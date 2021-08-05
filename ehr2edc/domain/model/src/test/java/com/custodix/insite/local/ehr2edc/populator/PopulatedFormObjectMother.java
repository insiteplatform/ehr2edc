package com.custodix.insite.local.ehr2edc.populator;

import static com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroupObjectMother.aDefaultItemGroup;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;

import com.custodix.insite.local.ehr2edc.metadata.model.ItemLabel;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDemographicObjectMother;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue;
import com.custodix.insite.local.ehr2edc.snapshots.FormDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.ItemDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.ItemGroupDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public class PopulatedFormObjectMother {
	public static PopulatedForm aDefaultForm() {
		return aDefaultFormBuilder().build();
	}

	public static PopulatedForm.Builder aDefaultFormBuilder() {
		return PopulatedForm.newBuilder()
				.withInstanceId(FormId.of("form-123"))
				.withFormDefinitionId(FormDefinitionId.of("123-123"))
				.withName("Form name")
				.withItemGroups(Collections.singletonList(aDefaultItemGroup()));
	}

	public static PopulatedForm generateForm() {
		return PopulatedForm.newBuilder()
				.withInstanceId(FormId.of(randomUUID().toString()))
				.withFormDefinitionId(FormDefinitionId.of(randomUUID().toString()))
				.withItemGroups(emptyList())
				.build();
	}

	public static PopulatedForm createForm(FormKeyObjectMother formKey) {
		FormDefinitionId formDefinitionId = formKey.formDefinition.getId();
		return PopulatedForm.newBuilder()
				.withInstanceId(FormId.of(randomUUID().toString()))
				.withName(formKey.formDefinition.getName())
				.withFormDefinitionId(formDefinitionId)
				.withItemGroups(createPopulatedItemGroups(formKey.formDefinition.getItemGroupDefinitions()))
				.build();
	}

	private static List<PopulatedItemGroup> createPopulatedItemGroups(List<ItemGroupDefinitionSnapshot> itemGroups) {
		return itemGroups.stream()
				.map(PopulatedFormObjectMother::createPopulatedItemGroup)
				.collect(toList());
	}

	private static PopulatedItemGroup createPopulatedItemGroup(ItemGroupDefinitionSnapshot itemGroup) {
		return PopulatedItemGroup.newBuilder()
				.withInstanceId(ItemGroupId.of(randomUUID().toString()))
				.withDefinition(createPopulatedItemGroupDefinition(itemGroup))
				.withItems(createPopulatedItems(itemGroup.getItemDefinitions()))
				.withIndex("itemGroup-4".equals(itemGroup.getId().getId()) ? "abc123" : null)
				.build();
	}

	private static PopulatedItemGroup.Definition createPopulatedItemGroupDefinition(ItemGroupDefinitionSnapshot itemGroup) {
		return PopulatedItemGroup.Definition.newBuilder()
				.withId(itemGroup.getId())
				.withName(itemGroup.getName())
				.withRepeating(itemGroup.isRepeating())
				.build();
	}

	private static List<PopulatedItem> createPopulatedItems(List<ItemDefinitionSnapshot> items) {
		return items.stream()
				.map(PopulatedFormObjectMother::createPopulatedItem)
				.collect(toList());
	}

	private static PopulatedItem createPopulatedItem(ItemDefinitionSnapshot item) {
		return PopulatedItem.newBuilder()
				.withInstanceId(ItemId.of(randomUUID().toString()))
				.withId(item.getId())
				.withLabel(ItemLabel.restoreFrom(item.getLabel()))
				.withValue(new LabeledValue("1"))
				.withKey(item.getId().getId().contains("CMTRT"))
				.withDataPoint(ProvenanceDemographicObjectMother.aDefaultProvenanceDemographic())
				.withProjectionSteps(emptyList())
				.build();
	}

	public static class FormKeyObjectMother {
		public final StudyId studyId;
		public final EventDefinitionId eventDefinitionId;
		public final FormDefinitionSnapshot formDefinition;

		FormKeyObjectMother(StudyId studyId, EventDefinitionId eventDefinitionId,
				FormDefinitionSnapshot formDefinition) {
			this.studyId = studyId;
			this.eventDefinitionId = eventDefinitionId;
			this.formDefinition = formDefinition;
		}
	}
}