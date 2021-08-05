package com.custodix.insite.local.ehr2edc.metadata.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroup;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.snapshots.ItemDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.ItemGroupDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;

public final class ItemGroupDefinition {
	private final ItemGroupDefinitionId id;
	private final String name;
	private final boolean repeating;
	private final List<ItemDefinition> itemDefinitions;

	private ItemGroupDefinition(ItemGroupDefinitionId id, String name, List<ItemDefinition> itemDefinitions, boolean repeating) {
		this.id = id;
		this.name = name;
		this.itemDefinitions = itemDefinitions;
		this.repeating = repeating;
	}

	private ItemGroupDefinition(final Builder builder) {
		id = builder.id;
		name = builder.name;
		itemDefinitions = builder.itemDefinitions;
		repeating = builder.repeating;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public ItemDefinition getItemBy(final String itemId) {
		return this.itemDefinitions.stream()
				.filter(item -> item.getId().getId().equals(itemId))
				.findFirst()
				.orElseThrow(() -> new SystemException(String.format("Cannot find item with id '%s' in a group item with id  '%s' ", itemId, this.id)));
	}

	static List<ItemGroupDefinition> restoreFrom(List<ItemGroupDefinitionSnapshot> itemGroupSnapshots) {
		return itemGroupSnapshots == null ?
				new ArrayList<>() :
				itemGroupSnapshots.stream()
						.map(ItemGroupDefinition::restoreFrom)
						.collect(Collectors.toList());
	}

	private static ItemGroupDefinition restoreFrom(ItemGroupDefinitionSnapshot itemGroupSnapshot) {
		return new ItemGroupDefinition(itemGroupSnapshot.getId(),
				itemGroupSnapshot.getName(),
				ItemDefinition.restoreFrom(itemGroupSnapshot.getItemDefinitions()),
				itemGroupSnapshot.isRepeating());
	}

	public ItemGroupDefinitionSnapshot toSnapshot() {
		return ItemGroupDefinitionSnapshot.newBuilder()
				.withId(id)
				.withName(name)
				.withItemDefinitions(itemsToSnapshot())
				.withRepeating(repeating)
				.build();
	}

	public PopulatedItemGroup.Definition toPopulatedItemGroupDefinition() {
		return PopulatedItemGroup.Definition.newBuilder()
				.withId(id)
				.withName(name)
				.withRepeating(repeating)
				.build();
	}

	private List<ItemDefinitionSnapshot> itemsToSnapshot() {
		return itemDefinitions == null ?
				null :
				itemDefinitions.stream()
						.map(ItemDefinition::toSnapshot)
						.collect(Collectors.toList());
	}

	public ItemGroupDefinitionId getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<ItemDefinition> getItemDefinitions() {
		return itemDefinitions;
	}

	public boolean isRepeating() {
		return repeating;
	}

	public static final class Builder {
		private ItemGroupDefinitionId id;
		private String name;
		private List<ItemDefinition> itemDefinitions;
		private boolean repeating;

		private Builder() {
		}

		public Builder withId(final ItemGroupDefinitionId val) {
			id = val;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withItemDefinitions(final List<ItemDefinition> val) {
			itemDefinitions = val;
			return this;
		}

		public Builder withRepeating(final boolean val) {
			repeating = val;
			return this;
		}

		public ItemGroupDefinition build() {
			return new ItemGroupDefinition(this);
		}
	}
}
