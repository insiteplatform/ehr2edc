package com.custodix.insite.local.ehr2edc.snapshots;

import java.util.List;

import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.LabName;

public final class FormDefinitionSnapshot {
	private final FormDefinitionId id;
	private final String name;
	private final List<ItemGroupDefinitionSnapshot> itemGroupDefinitions;
	private final LabName localLab;

	private FormDefinitionSnapshot(final Builder builder) {
		id = builder.id;
		itemGroupDefinitions = builder.itemGroupDefinitions;
		name = builder.name;
		localLab = builder.localLab;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public FormDefinitionId getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<ItemGroupDefinitionSnapshot> getItemGroupDefinitions() {
		return itemGroupDefinitions;
	}

	public LabName getLocalLab() {
		return localLab;
	}

	public static final class Builder {
		private FormDefinitionId id;
		private String name;
		private List<ItemGroupDefinitionSnapshot> itemGroupDefinitions;
		private LabName localLab;

		private Builder() {
		}

		public Builder withId(final FormDefinitionId val) {
			id = val;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withItemGroupDefinitions(final List<ItemGroupDefinitionSnapshot> val) {
			itemGroupDefinitions = val;
			return this;
		}

		public Builder withLocalLab(final LabName val) {
			localLab = val;
			return this;
		}

		public FormDefinitionSnapshot build() {
			return new FormDefinitionSnapshot(this);
		}
	}
}
