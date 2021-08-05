package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.snapshots.ItemQueryMappingSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;

public class ItemQueryMappingDocument {

	private final String itemId;
	private final String value;

	private ItemQueryMappingDocument(final String itemId, final String value) {
		this.itemId = itemId;
		this.value = value;
	}

	public String getItemId() {
		return itemId;
	}

	public String getValue() {
		return value;
	}

	public static Collection<ItemQueryMappingDocument> fromSnapshots(
			Map<ItemDefinitionId, ItemQueryMappingSnapshot> itemQueryMappingSnapshots) {
		if(itemQueryMappingSnapshots == null ) {
			return Collections.emptyList();
		}
		return itemQueryMappingSnapshots.entrySet().stream()
				.map(e -> new ItemQueryMappingDocument(
						e.getKey().getId(),
						e.getValue().getValue()
						))
				.collect(Collectors.toList());
	}

	ItemQueryMappingSnapshot toSnapShot() {
		return ItemQueryMappingSnapshot.newBuilder()
				.withValue(value)
				.build();
	}

}
