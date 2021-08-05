package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.snapshots.FormDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.ItemGroupDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.LabName;

public final class FormDefinitionDocument {
	private final String id;
	private final String name;
	private final List<ItemGroupDefinitionDocument> itemGroupDefinitions;
	private final String localLab;

	@PersistenceConstructor
	private FormDefinitionDocument(String id, String name,
			List<ItemGroupDefinitionDocument> itemGroupDefinitions, String localLab) {
		this.id = id;
		this.name = name;
		this.itemGroupDefinitions = itemGroupDefinitions;
		this.localLab = localLab;
	}

	private FormDefinitionDocument(Builder builder) {
		id = builder.id;
		name = builder.name;
		itemGroupDefinitions = builder.itemGroupDefinitions;
		localLab = builder.localLab;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	static List<FormDefinitionDocument> fromSnapshot(List<FormDefinitionSnapshot> formDefinitionSnapshots) {
		if (formDefinitionSnapshots == null) {
			return Collections.emptyList();
		}
		return formDefinitionSnapshots.stream()
				.map(FormDefinitionDocument::fromSnapshot)
				.collect(Collectors.toList());
	}

	static FormDefinitionDocument fromSnapshot(FormDefinitionSnapshot formDefinitionSnapshot) {
		String id = formDefinitionSnapshot.getId()
				.getId();
		String localLab = Optional.ofNullable(formDefinitionSnapshot.getLocalLab())
				.map(LabName::getName)
				.orElse(null);
		List<ItemGroupDefinitionDocument> itemGroups = ItemGroupDefinitionDocument.fromSnapshots(
				formDefinitionSnapshot.getItemGroupDefinitions());
		return FormDefinitionDocument.newBuilder()
				.withId(id)
				.withName(formDefinitionSnapshot.getName())
				.withItemGroupDefinitions(itemGroups)
				.withLocalLab(localLab)
				.build();
	}

	public String getId() {
		return id;
	}

	public List<ItemGroupDefinitionDocument> getItemGroupDefinitions() {
		return itemGroupDefinitions;
	}

	public FormDefinitionSnapshot toSnapshot() {
		return FormDefinitionSnapshot.newBuilder()
				.withId(FormDefinitionId.of(id))
				.withName(name)
				.withItemGroupDefinitions(toItemGroups())
				.withLocalLab(localLab != null ? LabName.of(localLab) : null)
				.build();
	}

	private List<ItemGroupDefinitionSnapshot> toItemGroups() {
		return itemGroupDefinitions == null ?
				Collections.emptyList() :
				itemGroupDefinitions.stream()
						.map(ItemGroupDefinitionDocument::toSnapshot)
						.collect(Collectors.toList());
	}

	public static final class Builder {
		private String id;
		private String name;
		private List<ItemGroupDefinitionDocument> itemGroupDefinitions;
		private String localLab;

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

		public Builder withItemGroupDefinitions(List<ItemGroupDefinitionDocument> itemGroupDefinitions) {
			this.itemGroupDefinitions = itemGroupDefinitions;
			return this;
		}

		public Builder withLocalLab(String localLab) {
			this.localLab = localLab;
			return this;
		}

		public FormDefinitionDocument build() {
			return new FormDefinitionDocument(this);
		}
	}
}
