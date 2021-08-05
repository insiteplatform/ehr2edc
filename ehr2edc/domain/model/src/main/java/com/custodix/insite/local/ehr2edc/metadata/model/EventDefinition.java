package com.custodix.insite.local.ehr2edc.metadata.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.FormDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;

public final class EventDefinition {

	private EventDefinitionId id;
	private String parentId;
	private String name;
	private List<FormDefinition> formDefinitions;

	private EventDefinition(Builder builder) {
		id = builder.id;
		parentId = builder.parentId;
		name = builder.name;
		formDefinitions = builder.formDefinitions;
	}

	private EventDefinition(EventDefinitionId id, String name, String parentId, List<FormDefinition> formDefinitions) {
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.formDefinitions = formDefinitions;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public EventDefinitionSnapshot toSnapshot() {
		return EventDefinitionSnapshot.newBuilder()
				.withId(id)
				.withName(name)
				.withParentId(parentId)
				.withFormDefinitions(formDefinitionsToSnapshot())
				.build();
	}

	private List<FormDefinitionSnapshot> formDefinitionsToSnapshot() {
		return formDefinitions == null ?
				null :
				formDefinitions.stream()
						.map(FormDefinition::toSnapshot)
						.collect(Collectors.toList());
	}

	static List<EventDefinition> restoreFrom(List<EventDefinitionSnapshot> eventDefinitions) {
		return eventDefinitions == null ?
				new ArrayList<>() :
				eventDefinitions.stream()
						.map(EventDefinition::restoreFrom)
						.collect(Collectors.toList());
	}

	public static EventDefinition restoreFrom(EventDefinitionSnapshot eventDefinition) {
		return new EventDefinition(eventDefinition.getId(), eventDefinition.getName(), eventDefinition.getParentId(),
				FormDefinition.restoreFrom(eventDefinition.getFormDefinitionSnapshots()));
	}

	public EventDefinitionId getId() {
		return id;
	}

	public String getParentId() {
		return parentId;
	}

	public String getName() {
		return name;
	}

	public List<FormDefinition> getFormDefinitions() {
		return formDefinitions;
	}

	public Optional<FormDefinition> getForm(FormDefinitionId formDefinitionId) {
		return formDefinitions.stream()
				.filter(form -> form.getId()
						.equals(formDefinitionId))
				.findFirst();
	}

	public static final class Builder {
		private EventDefinitionId id;
		private String parentId;
		private String name;
		private List<FormDefinition> formDefinitions;

		private Builder() {
		}

		public Builder withId(EventDefinitionId id) {
			this.id = id;
			return this;
		}

		public Builder withParentId(String parentId) {
			this.parentId = parentId;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withFormDefinitions(List<FormDefinition> formDefinitions) {
			this.formDefinitions = formDefinitions;
			return this;
		}

		public EventDefinition build() {
			return new EventDefinition(this);
		}
	}
}
