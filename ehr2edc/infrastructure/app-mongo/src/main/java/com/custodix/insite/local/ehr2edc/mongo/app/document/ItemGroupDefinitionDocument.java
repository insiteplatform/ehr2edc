package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.snapshots.ItemGroupDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;

public final class ItemGroupDefinitionDocument {
	private final String id;
	private final String name;
	private final List<ItemDefinitionDocument> itemDefinitions;
	private boolean repeating;

	@PersistenceConstructor
	private ItemGroupDefinitionDocument(String id, String name, List<ItemDefinitionDocument> itemDefinitions, Boolean repeating) {
		this.id = id;
		this.name = name;
		this.itemDefinitions = itemDefinitions;
		this.repeating = repeating;
	}

	private ItemGroupDefinitionDocument(final Builder builder) {
		id = builder.id;
		name = builder.name;
		itemDefinitions = builder.itemDefinitions;
		repeating = builder.repeating;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	static List<ItemGroupDefinitionDocument> fromSnapshots(List<ItemGroupDefinitionSnapshot> itemGroups) {
		if (itemGroups == null) {
			return Collections.emptyList();
		}
		return itemGroups.stream()
				.map(s -> new ItemGroupDefinitionDocument(
						s.getId().getId(),
						s.getName(),
						ItemDefinitionDocument.fromSnapshots(s.getItemDefinitions()), s.isRepeating()))
				.collect(Collectors.toList());
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<ItemDefinitionDocument> getItemDefinitions() {
		return itemDefinitions;
	}

	public boolean isRepeating() {
		return repeating;
	}

	public ItemGroupDefinitionSnapshot toSnapshot() {
		return ItemGroupDefinitionSnapshot.newBuilder()
				.withId(ItemGroupDefinitionId.of(id))
				.withName(name)
				.withItemDefinitions(itemDefinitions.stream()
						.map(ItemDefinitionDocument::toSnapshot)
						.collect(Collectors.toList()))
				.withRepeating(repeating)
				.build();
	}

	public static final class Builder {
		private String id;
		private String name;
		private List<ItemDefinitionDocument> itemDefinitions;
		private boolean repeating;

		private Builder() {
		}

		public Builder withId(final String val) {
			id = val;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withItemDefinitions(final List<ItemDefinitionDocument> val) {
			itemDefinitions = val;
			return this;
		}

		public Builder withRepeating(final boolean val) {
			repeating = val;
			return this;
		}

		public ItemGroupDefinitionDocument build() {
			return new ItemGroupDefinitionDocument(this);
		}
	}
}
