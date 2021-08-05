package com.custodix.insite.local.ehr2edc.populator;

import static com.custodix.insite.local.ehr2edc.populator.PopulatedItemObjectMother.aDefaultPopulatedItem;

import java.util.Collections;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupId;

public class PopulatedItemGroupObjectMother {

	public static PopulatedItemGroup aDefaultItemGroup() {
		return aDefaultItemGroupBuilder().build();
	}

	public static PopulatedItemGroup.Builder aDefaultItemGroupBuilder() {
		return PopulatedItemGroup.newBuilder()
				.withInstanceId(ItemGroupId.of("itemgroup-123-123"))
				.withDefinition(aDefaultItemGroupDefinitionBuilder().build())
				.withItems(Collections.singletonList(aDefaultPopulatedItem()));
	}

	public static PopulatedItemGroup.Definition.Builder aDefaultItemGroupDefinitionBuilder() {
		return PopulatedItemGroup.Definition.newBuilder()
				.withId(ItemGroupDefinitionId.of("123-123"))
				.withName("item group name")
				.withRepeating(false);
	}

}