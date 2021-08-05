package com.custodix.insite.local.ehr2edc;

import com.custodix.insite.local.ehr2edc.snapshots.ItemQueryMappingSnapshot;

public class ItemQueryMappingJson {
	private final String value;

	private ItemQueryMappingJson(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	static ItemQueryMappingJson restoreFromSnapshot(ItemQueryMappingSnapshot itemQueryMappingSnapshot) {
		return new ItemQueryMappingJson(itemQueryMappingSnapshot.getValue());
	}

	public static ItemQueryMappingJson create(final String value) {
		return new ItemQueryMappingJson(value);
	}

	ItemQueryMappingSnapshot toSnapShot() {
		return ItemQueryMappingSnapshot.newBuilder()
				.withValue(value)
				.build();
	}
}
