package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroup;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupId;

public final class ItemGroupDocument {
	private final String instanceId;
	private final DefinitionMongoSnapshot definition;
	private final List<ItemDocument> items;
	private final String index;

	@PersistenceConstructor
	private ItemGroupDocument(String instanceId, DefinitionMongoSnapshot definition, List<ItemDocument> items,
			String index) {
		this.instanceId = instanceId;
		this.definition = definition;
		this.items = items;
		this.index = index;
	}

	private ItemGroupDocument(Builder builder) {
		instanceId = builder.instanceId;
		definition = builder.definition;
		items = builder.items;
		index = builder.index;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public DefinitionMongoSnapshot getDefinition() {
		return definition;
	}

	public List<ItemDocument> getItems() {
		return items;
	}

	public String getIndex() {
		return index;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(ItemGroupDocument copy) {
		Builder builder = new Builder();
		builder.instanceId = copy.getInstanceId();
		builder.definition = copy.getDefinition();
		builder.items = copy.getItems();
		builder.index = copy.getIndex();
		return builder;
	}

	PopulatedItemGroup toItemGroup() {
		return PopulatedItemGroup.newBuilder()
				.withInstanceId(ItemGroupId.of(instanceId))
				.withDefinition(definition.toDefinition())
				.withItems(items.stream()
						.map(ItemDocument::toItem)
						.collect(Collectors.toList()))
				.withIndex(index)
				.build();
	}

	static List<ItemGroupDocument> restoreFrom(final List<PopulatedItemGroup> itemGroups) {
		if (itemGroups == null) {
			return Collections.emptyList();
		}
		return itemGroups.stream()
				.map(ig -> new ItemGroupDocument(getMongoSnapshotInstanceId(ig),
						DefinitionMongoSnapshot.restoreFrom(ig.getDefinition()),
						ItemDocument.restoreFrom(ig.getItems()), ig.getIndex()))
				.collect(Collectors.toList());
	}

	private static String getMongoSnapshotInstanceId(PopulatedItemGroup ig) {
		return ig.getInstanceId()
				.getId();
	}

	public static final class Builder {
		private String instanceId;
		private DefinitionMongoSnapshot definition;
		private List<ItemDocument> items;
		private String index;

		private Builder() {
		}

		public Builder withInstanceId(final String val) {
			instanceId = val;
			return this;
		}

		public Builder withDefinition(final DefinitionMongoSnapshot val) {
			definition = val;
			return this;
		}

		public Builder withItems(final List<ItemDocument> val) {
			items = val;
			return this;
		}

		public Builder withIndex(String val) {
			index = val;
			return this;
		}

		public ItemGroupDocument build() {
			return new ItemGroupDocument(this);
		}
	}

	public static final class DefinitionMongoSnapshot {
		private final String id;
		private final String name;
		private final boolean repeating;

		@PersistenceConstructor
		private DefinitionMongoSnapshot(String id, String name, Boolean repeating) {
			this.id = id;
			this.name = name;
			this.repeating = repeating;
		}

		private DefinitionMongoSnapshot(Builder builder) {
			id = builder.id;
			name = builder.name;
			repeating = builder.repeating;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public boolean isRepeating() {
			return repeating;
		}

		public static Builder newBuilder(DefinitionMongoSnapshot copy) {
			Builder builder = new Builder();
			builder.id = copy.getId();
			builder.name = copy.getName();
			builder.repeating = copy.isRepeating();
			return builder;
		}

		static DefinitionMongoSnapshot restoreFrom(PopulatedItemGroup.Definition definition) {
			return DefinitionMongoSnapshot.newBuilder()
					.withId(definition.getId()
							.getId())
					.withName(definition.getName())
					.withRepeating(definition.isRepeating())
					.build();
		}

		PopulatedItemGroup.Definition toDefinition() {
			return PopulatedItemGroup.Definition.newBuilder()
					.withId(ItemGroupDefinitionId.of(id))
					.withName(name)
					.withRepeating(repeating)
					.build();
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private String id;
			private String name;
			private boolean repeating;

			private Builder() {
			}

			public Builder withId(String id) {
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

			public DefinitionMongoSnapshot build() {
				return new DefinitionMongoSnapshot(this);
			}
		}
	}
}
