package com.custodix.insite.local.ehr2edc.snapshots;

import java.util.List;

public final class MetaDataDefinitionSnapshot {
	private final String id;
	private final List<EventDefinitionSnapshot> eventDefinitions;

	private MetaDataDefinitionSnapshot(Builder builder) {
		id = builder.id;
		eventDefinitions = builder.eventDefinitions;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getId() {
		return id;
	}

	public List<EventDefinitionSnapshot> getEventDefinitions() {
		return eventDefinitions;
	}

	public static final class Builder {
		private String id;
		private List<EventDefinitionSnapshot> eventDefinitions;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withEventDefinitions(List<EventDefinitionSnapshot> eventDefinitions) {
			this.eventDefinitions = eventDefinitions;
			return this;
		}

		public MetaDataDefinitionSnapshot build() {
			return new MetaDataDefinitionSnapshot(this);
		}
	}
}
