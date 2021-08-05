package com.custodix.insite.local.ehr2edc.populator;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;

public final class PopulatedItemSelection {
	private final ItemId itemId;

	private PopulatedItemSelection(Builder builder) {
		itemId = builder.itemId;
	}

	public ItemId getItemId() {
		return itemId;
	}

	public static PopulatedItemSelection of(ItemId itemId) {
		return PopulatedItemSelection.newBuilder()
				.withItemId(itemId)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ItemId itemId;

		private Builder() {
		}

		Builder withItemId(ItemId val) {
			itemId = val;
			return this;
		}

		public PopulatedItemSelection build() {
			return new PopulatedItemSelection(this);
		}
	}
}