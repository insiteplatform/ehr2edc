package com.custodix.insite.local.ehr2edc.metadata.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.MetaDataDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;

public final class MetaDataDefinition {
	private String id;
	private List<EventDefinition> eventDefinitions;

	private MetaDataDefinition(Builder builder) {
		id = builder.id;
		eventDefinitions = builder.eventDefinitions;
	}

	private MetaDataDefinition(String id, List<EventDefinition> eventDefinitions) {
		this.id = id;
		this.eventDefinitions = eventDefinitions;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static MetaDataDefinition restoreFrom(MetaDataDefinitionSnapshot metadata) {
		return new MetaDataDefinition(metadata.getId(), EventDefinition.restoreFrom(metadata.getEventDefinitions()));
	}

	public MetaDataDefinitionSnapshot toSnapshot() {
		return MetaDataDefinitionSnapshot.newBuilder()
				.withEventDefinitions(eventDefinitionsToSnapshot())
				.withId(id)
				.build();
	}

	private List<EventDefinitionSnapshot> eventDefinitionsToSnapshot() {
		return eventDefinitions == null ?
				null :
				eventDefinitions.stream()
						.map(EventDefinition::toSnapshot)
						.collect(Collectors.toList());
	}

	public String getId() {
		return id;
	}

	public List<EventDefinition> getEventDefinitions() {
		return eventDefinitions;
	}

	public FormDefinition getFormDefinition(FormDefinitionId formDefinitionId) {
		return eventDefinitions.stream()
				.map(event -> event.getForm(formDefinitionId))
				.flatMap(opt -> opt.map(Stream::of)
						.orElse(Stream.empty()))
				.findFirst()
				.orElseThrow(
						() -> new IllegalArgumentException("No formdefinition with id " + formDefinitionId + " is present in metadata"));
	}

	public Optional<EventDefinition> findEventDefinition(EventDefinitionId eventDefinitionId) {
		return eventDefinitions.stream()
				.filter(eventDefinition -> eventDefinition.getId()
						.equals(eventDefinitionId))
				.findFirst();
	}

	public static final class Builder {
		private String id;
		private List<EventDefinition> eventDefinitions;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withEventDefinitions(List<EventDefinition> eventDefinitions) {
			this.eventDefinitions = eventDefinitions;
			return this;
		}

		public MetaDataDefinition build() {
			return new MetaDataDefinition(this);
		}
	}
}
