package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.MetaDataDefinitionSnapshot;

public final class MetaDataDefinitionDocument {
	private final String id;
	private final List<EventDefinitionDocument> eventDefinitions;

	@PersistenceConstructor
	private MetaDataDefinitionDocument(String id, List<EventDefinitionDocument> eventDefinitions) {
		this.id = id;
		this.eventDefinitions = eventDefinitions;
	}

	private MetaDataDefinitionDocument(final Builder builder) {
		id = builder.id;
		eventDefinitions = builder.eventDefinitions;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	static MetaDataDefinitionDocument fromSnapshot(MetaDataDefinitionSnapshot metadata) {
		return new MetaDataDefinitionDocument(metadata.getId(),
				EventDefinitionDocument.fromSnapshots(metadata.getEventDefinitions()));
	}

	public String getId() {
		return id;
	}

	public List<EventDefinitionDocument> getEventDefinitions() {
		return eventDefinitions;
	}

	public MetaDataDefinitionSnapshot toSnapshot() {
		return MetaDataDefinitionSnapshot.newBuilder()
				.withId(id)
				.withEventDefinitions(toEventDefinitionSnapshots())
				.build();
	}

	private List<EventDefinitionSnapshot> toEventDefinitionSnapshots() {
		return eventDefinitions == null ?
				Collections.emptyList() :
				eventDefinitions.stream()
						.map(EventDefinitionDocument::toSnapshot)
						.collect(Collectors.toList());
	}

	public static final class Builder {
		private String id;
		private List<EventDefinitionDocument> eventDefinitions;

		private Builder() {
		}

		public Builder withId(final String val) {
			id = val;
			return this;
		}

		public Builder withEventDefinitions(final List<EventDefinitionDocument> val) {
			eventDefinitions = val;
			return this;
		}

		public MetaDataDefinitionDocument build() {
			return new MetaDataDefinitionDocument(this);
		}
	}
}
