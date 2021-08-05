package com.custodix.insite.local.ehr2edc.populator;

import java.util.List;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupId;

public final class PopulatedItemGroupSelection {
	private final ItemGroupId groupId;
	private final List<PopulatedItemSelection> itemSelections;

	private PopulatedItemGroupSelection(Builder builder) {
		groupId = builder.groupId;
		itemSelections = builder.itemSelections;
	}

	public ItemGroupId getGroupId() {
		return groupId;
	}

	public List<PopulatedItemSelection> getItemSelections() {
		return itemSelections;
	}

	public static PopulatedItemGroupSelection of(ItemGroupId groupId, List<PopulatedItemSelection> itemSelections) {
		return PopulatedItemGroupSelection.newBuilder()
				.withGroupId(groupId)
				.withItemSelections(itemSelections)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ItemGroupId groupId;
		private List<PopulatedItemSelection> itemSelections;

		private Builder() {
		}

		public Builder withGroupId(ItemGroupId val) {
			groupId = val;
			return this;
		}

		public Builder withItemSelections(List<PopulatedItemSelection> val) {
			itemSelections = val;
			return this;
		}

		public PopulatedItemGroupSelection build() {
			return new PopulatedItemGroupSelection(this);
		}
	}
}