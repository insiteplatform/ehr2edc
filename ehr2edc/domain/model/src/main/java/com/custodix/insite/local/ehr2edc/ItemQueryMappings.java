package com.custodix.insite.local.ehr2edc;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;

import com.custodix.insite.local.ehr2edc.snapshots.ItemQueryMappingSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;

public class ItemQueryMappings {

	private Map<ItemDefinitionId, ItemQueryMappingJson> itemQueryMappingJsonMap;

	private ItemQueryMappings(final Map<ItemDefinitionId, ItemQueryMappingJson> itemQueryMappingJsonMap) {
		this.itemQueryMappingJsonMap = itemQueryMappingJsonMap;
	}

	private ItemQueryMappings(final Builder builder) {
		itemQueryMappingJsonMap = builder.itemQueryMappings == null ? new HashMap<>() : builder.itemQueryMappings;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static ItemQueryMappings restoreFrom(final Map<ItemDefinitionId, ItemQueryMappingSnapshot> itemQueryMappingSnapshots) {
		return itemQueryMappingSnapshots.entrySet()
				.stream()
				.collect(collectingAndThen(collectToMapFromSnapshot(), ItemQueryMappings::new));
	}

	private static Collector<Map.Entry<ItemDefinitionId, ItemQueryMappingSnapshot>, ?, Map<ItemDefinitionId, ItemQueryMappingJson>> collectToMapFromSnapshot() {
		return toMap(Map.Entry::getKey, e -> ItemQueryMappingJson.restoreFromSnapshot(e.getValue()));
	}

	public Map<ItemDefinitionId, ItemQueryMappingJson> getItemQueryMappingJsonMap() {
		return Collections.unmodifiableMap(itemQueryMappingJsonMap);
	}

	boolean hasMapping(final ItemDefinitionId itemId) {
		return itemQueryMappingJsonMap.containsKey(itemId);
	}

	public Map<ItemDefinitionId, ItemQueryMappingSnapshot> toSnapshot() {
		return itemQueryMappingJsonMap.entrySet()
				.stream()
				.collect(toMap(Map.Entry::getKey, e -> e.getValue()
						.toSnapShot()));
	}

	void put(ItemDefinitionId itemId, ItemQueryMappingJson itemQueryMappingJson) {
		itemQueryMappingJsonMap.put(itemId, itemQueryMappingJson);
	}

	void delete(ItemDefinitionId itemId) {
		itemQueryMappingJsonMap.remove(itemId);
	}

	void clear() {
		this.itemQueryMappingJsonMap.clear();
	}

	public static final class Builder {
		private Map<ItemDefinitionId, ItemQueryMappingJson> itemQueryMappings;

		private Builder() {
		}

		public Builder withItemQueryMappings(final Map<ItemDefinitionId, ItemQueryMappingJson> val) {
			itemQueryMappings = val;
			return this;
		}

		public ItemQueryMappings build() {
			return new ItemQueryMappings(this);
		}
	}
}
