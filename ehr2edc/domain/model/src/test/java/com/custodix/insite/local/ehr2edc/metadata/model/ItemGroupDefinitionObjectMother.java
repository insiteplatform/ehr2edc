package com.custodix.insite.local.ehr2edc.metadata.model;

import static com.custodix.insite.local.ehr2edc.metadata.model.ItemDefinitionObjectMother.aDefaultItem;

import java.util.Collections;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;

public class ItemGroupDefinitionObjectMother {

	public static ItemGroupDefinition aDefaultItemGroupDefinition() {
		return aDefaultItemGroupDefinitionBuilder().build();
	}

	public static ItemGroupDefinition.Builder aDefaultItemGroupDefinitionBuilder() {
		return ItemGroupDefinition.newBuilder()
				.withId(ItemGroupDefinitionId.of("123-123"))
				.withItemDefinitions(Collections.singletonList(aDefaultItem()))
				.withRepeating(false);
	}

	public static ItemGroupDefinition.Builder aRepeatingtItemGroupDefinitionBuilder() {
		return ItemGroupDefinition.newBuilder()
				.withId(ItemGroupDefinitionId.of("123-123"))
				.withItemDefinitions(Collections.singletonList(aDefaultItem()))
				.withRepeating(true);
	}
}