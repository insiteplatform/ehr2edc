package com.custodix.insite.local.ehr2edc.submitted;

import static com.custodix.insite.local.ehr2edc.submitted.SubmittedItemObjectMother.aDefaultSubmittedItemBuilder;

import java.util.Collections;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroup;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupId;

public class SubmittedItemGroupObjectMother {
	public static SubmittedItemGroup.Builder aDefaultSubmittedItemGroupBuilder() {
		return SubmittedItemGroup.newBuilder()
				.withId(ItemGroupDefinitionId.of("itemGroupId"))
				.withName("itemGroupName")
				.withSubmittedItems(Collections.singletonList(aDefaultSubmittedItemBuilder().build()))
				.withRepeating(false)
				.withPopulatedItemGroupId(ItemGroupId.of("populatedItemGroupId"));
	}

	public static SubmittedItemGroup aSubmittedItemGroup(PopulatedItemGroup populatedItemGroup) {
		return SubmittedItemGroup.newBuilder()
				.withId(populatedItemGroup.getId())
				.withName(populatedItemGroup.getName())
				.withPopulatedItemGroupId(populatedItemGroup.getInstanceId())
				.withRepeating(populatedItemGroup.isRepeating())
				.withIndex(populatedItemGroup.getIndex())
				.withSubmittedItems(populatedItemGroup.getItems()
						.stream()
						.map(SubmittedItemObjectMother::aSubmittedItem)
						.collect(Collectors.toList()))
				.build();
	}
}
