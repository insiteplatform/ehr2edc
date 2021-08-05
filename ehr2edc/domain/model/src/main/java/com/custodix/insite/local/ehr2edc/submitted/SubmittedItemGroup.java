package com.custodix.insite.local.ehr2edc.submitted;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedItemId;

public final class SubmittedItemGroup {
	private final ItemGroupDefinitionId id;
	private final String name;
	private final List<SubmittedItem> submittedItems;
	private final boolean repeating;
	private final String index;
	private final ItemGroupId populatedItemGroupId;

	private SubmittedItemGroup(Builder builder) {
		id = builder.id;
		name = builder.name;
		submittedItems = builder.submittedItems;
		repeating = builder.repeating;
		index = builder.index;
		populatedItemGroupId = builder.populatedItemGroupId;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public ItemGroupDefinitionId getId() {
		return id;
	}

	public List<SubmittedItem> getSubmittedItems() {
		return submittedItems;
	}

	public boolean isRepeating() {
		return repeating;
	}

	public String getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public boolean hasIndex() {
		return index != null;
	}

	public ItemGroupId getPopulatedItemGroupId() {
		return populatedItemGroupId;
	}

	SubmittedItem getSubmittedItem(ItemDefinitionId itemDefinitionId) {
		return submittedItems.stream()
				.filter(i -> itemDefinitionId.equals(i.getId()))
				.findFirst()
				.orElseThrow(
						() -> DomainException.of("study.submitted.item.definition.unknown", itemDefinitionId.getId(),
								id.getId()));
	}

	Optional<SubmittedItem> findSubmittedItem(SubmittedItemId submittedItemId) {
		return submittedItems.stream()
				.filter(i -> submittedItemId.equals(i.getInstanceId()))
				.findFirst();
	}

	boolean hasKey() {
		return submittedItems.stream()
				.anyMatch(SubmittedItem::isKey);
	}

	List<String> getKey() {
		return submittedItems.stream()
				.filter(SubmittedItem::isKey)
				.map(r -> r.getLabeledValue()
						.getValue())
				.collect(toList());
	}

	public static final class Builder {
		private ItemGroupDefinitionId id;
		private String name;
		private List<SubmittedItem> submittedItems;
		private boolean repeating;
		private String index;
		private ItemGroupId populatedItemGroupId;

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

		public Builder withSubmittedItems(List<SubmittedItem> submittedItems) {
			this.submittedItems = submittedItems;
			return this;
		}

		public Builder withRepeating(boolean repeating) {
			this.repeating = repeating;
			return this;
		}

		public Builder withIndex(String index) {
			this.index = index;
			return this;
		}

		public Builder withPopulatedItemGroupId(ItemGroupId populatedItemGroupId) {
			this.populatedItemGroupId = populatedItemGroupId;
			return this;
		}

		public SubmittedItemGroup build() {
			return new SubmittedItemGroup(this);
		}
	}
}
