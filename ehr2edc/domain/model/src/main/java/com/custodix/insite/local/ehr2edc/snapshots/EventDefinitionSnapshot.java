package com.custodix.insite.local.ehr2edc.snapshots;

import java.util.List;

import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;

public final class EventDefinitionSnapshot {

	private final EventDefinitionId id;
	private final String name;
	private final String parentId;
	private final List<FormDefinitionSnapshot> formDefinitions;

	private EventDefinitionSnapshot(Builder builder) {
		id = builder.id;
		name = builder.name;
		parentId = builder.parentId;
		formDefinitions = builder.formDefinitions;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public EventDefinitionId getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getParentId() {
		return parentId;
	}

	public List<FormDefinitionSnapshot> getFormDefinitionSnapshots() {
		return formDefinitions;
	}

	public static final class Builder {
		private EventDefinitionId id;
		private String name;
		private String parentId;
		private List<FormDefinitionSnapshot> formDefinitions;

		private Builder() {
		}

		public Builder withId(EventDefinitionId id) {
			this.id = id;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withParentId(String parentId) {
			this.parentId = parentId;
			return this;
		}

		public Builder withFormDefinitions(List<FormDefinitionSnapshot> formDefinitions) {
			this.formDefinitions = formDefinitions;
			return this;
		}

		public EventDefinitionSnapshot build() {
			return new EventDefinitionSnapshot(this);
		}
	}
}
