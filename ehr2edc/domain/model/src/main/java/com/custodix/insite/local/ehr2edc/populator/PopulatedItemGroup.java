package com.custodix.insite.local.ehr2edc.populator;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.submitted.SubmittedItem;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItemGroup;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;

public final class PopulatedItemGroup {
	private final ItemGroupId instanceId;
	private final Definition definition;
	private final List<PopulatedItem> items;
	private final String index;

	private PopulatedItemGroup(Builder builder) {
		instanceId = builder.instanceId;
		definition = builder.definition;
		items = builder.items;
		index = builder.index;
	}

	public ItemGroupId getInstanceId() {
		return instanceId;
	}

	public Definition getDefinition() {
		return definition;
	}

	public List<PopulatedItem> getItems() {
		return items;
	}

	public String getName() {
		return definition.getName();
	}

	public ItemGroupDefinitionId getId() {
		return definition.getId();
	}

	public boolean isRepeating() {
		return definition.isRepeating();
	}

	public boolean isNotReadOnly() {
		return !items.stream()
				.allMatch(PopulatedItem::isReadOnly);
	}

	public String getIndex() {
		return index;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public boolean isNotEmpty() {
		return !items.isEmpty();
	}

	SubmittedItemGroup toReviewedItemGroup(List<PopulatedItemSelection> itemSelections) {
		List<SubmittedItem> reviewedItems = toReviewedItems(itemSelections);
		return SubmittedItemGroup.newBuilder()
				.withId(definition.getId())
				.withName(definition.getName())
				.withSubmittedItems(reviewedItems)
				.withRepeating(definition.isRepeating())
				.withIndex(index)
				.withPopulatedItemGroupId(instanceId)
				.build();
	}

	private List<SubmittedItem> toReviewedItems(List<PopulatedItemSelection> itemSelections) {
		List<String> itemIds = itemSelections.stream()
				.map(item -> item.getItemId()
						.getId())
				.collect(toList());
		return items.stream()
				.filter(item -> itemSelected(itemIds, item) || includeAsInformativeItem(item, itemIds))
				.map(PopulatedItem::toReviewedItem)
				.collect(toList());
	}

	private boolean itemSelected(List<String> itemIds, PopulatedItem item) {
		return itemIds.contains(item.getInstanceId()
				.getId());
	}

	private boolean includeAsInformativeItem(PopulatedItem item, List<String> itemsIds) {
		return item.isReadOnly() && atLeastOneItemInThisGroupIsSelected(itemsIds);
	}

	private boolean atLeastOneItemInThisGroupIsSelected(List<String> itemsIds) {
		return this.items.stream().map(i -> i.getInstanceId().getId()).anyMatch(itemsIds::contains);
	}

	Optional<PopulatedItem> findItemById(ItemId itemId) {
		return items.stream()
				.filter(i -> itemId.equals(i.getInstanceId()))
				.findFirst();
	}

	public static final class Builder {
		private ItemGroupId instanceId;
		private Definition definition;
		private List<PopulatedItem> items;
		private String index;

		private Builder() {
		}

		public Builder withInstanceId(ItemGroupId instanceId) {
			this.instanceId = instanceId;
			return this;
		}

		public Builder withDefinition(Definition definition) {
			this.definition = definition;
			return this;
		}

		public Builder withItems(List<PopulatedItem> items) {
			this.items = items;
			return this;
		}

		public Builder withIndex(String index) {
			this.index = index;
			return this;
		}

		public PopulatedItemGroup build() {
			return new PopulatedItemGroup(this);
		}
	}

	public static final class Definition {
		private final ItemGroupDefinitionId id;
		private final String name;
		private final boolean repeating;

		private Definition(Builder builder) {
			id = builder.id;
			name = builder.name;
			repeating = builder.repeating;
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

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private ItemGroupDefinitionId id;
			private String name;
			private boolean repeating;

			private Builder() {
			}

			public Builder withId(ItemGroupDefinitionId id) {
				this.id = id;
				return this;
			}

			public Builder withName(String name) {
				this.name = name;
				return this;
			}

			public Builder withRepeating(boolean repeating) {
				this.repeating = repeating;
				return this;
			}

			public Definition build() {
				return new Definition(this);
			}
		}
	}

}
