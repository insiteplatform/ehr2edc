package com.custodix.insite.local.ehr2edc.snapshots;

import java.util.List;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;

public final class ItemGroupDefinitionSnapshot {
	private final ItemGroupDefinitionId id;
	private final String name;
	private final List<ItemDefinitionSnapshot> itemDefinitions;
	private final boolean repeating;

	private ItemGroupDefinitionSnapshot(final Builder builder) {
		id = builder.id;
		name = builder.name;
		itemDefinitions = builder.itemDefinitions;
		repeating = builder.repeating;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public ItemGroupDefinitionId getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isRepeating() {
		return repeating;
	}

	public List<ItemDefinitionSnapshot> getItemDefinitions() {
		return itemDefinitions;
	}

	public static final class Builder {
		private ItemGroupDefinitionId id;
		private String name;
		private List<ItemDefinitionSnapshot> itemDefinitions;
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

		public Builder withRepeating(final boolean val) {
			repeating = val;
			return this;
		}

		public Builder withItemDefinitions(final List<ItemDefinitionSnapshot> val) {
			itemDefinitions = val;
			return this;
		}

		public ItemGroupDefinitionSnapshot build() {
			return new ItemGroupDefinitionSnapshot(this);
		}
	}
}
