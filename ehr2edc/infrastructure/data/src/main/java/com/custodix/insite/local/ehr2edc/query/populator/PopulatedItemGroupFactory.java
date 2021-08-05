package com.custodix.insite.local.ehr2edc.query.populator;

import static java.util.UUID.randomUUID;

import java.util.List;

import com.custodix.insite.local.ehr2edc.metadata.model.ItemGroupDefinition;
import com.custodix.insite.local.ehr2edc.populator.PopulatedItem;
import com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroup;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupId;

class PopulatedItemGroupFactory {

	private PopulatedItemGroupFactory() {}

	static PopulatedItemGroup createItemGroup(ItemGroupDefinition itemGroupDefinition, List<PopulatedItem> items) {
		return PopulatedItemGroup.newBuilder()
				.withInstanceId(ItemGroupId.of(randomUUID().toString()))
				.withDefinition(itemGroupDefinition.toPopulatedItemGroupDefinition())
				.withItems(items)
				.withIndex(extractIndex(items))
				.build();
	}

	private static String extractIndex(List<PopulatedItem> items) {
		return items.stream()
				.filter(PopulatedItemGroupFactory::hasIndex)
				.map(PopulatedItem::getIndex)
				.findFirst()
				.orElse(null);
	}

	private static boolean hasIndex(PopulatedItem i) {
		return i.getIndex() != null;
	}

}
