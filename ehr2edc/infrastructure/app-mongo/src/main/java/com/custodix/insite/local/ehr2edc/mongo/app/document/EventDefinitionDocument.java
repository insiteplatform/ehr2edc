package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.snapshots.EventDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.FormDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;

public final class EventDefinitionDocument {

	private final String id;
	private final String parentId;
	private final String name;
	private final List<FormDefinitionDocument> formDefinitions;

	@PersistenceConstructor
	private EventDefinitionDocument(String id, String parentId, String name,
			List<FormDefinitionDocument> formDefinitions) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.formDefinitions = formDefinitions;
	}

	private EventDefinitionDocument(final Builder builder) {
		id = builder.id;
		parentId = builder.parentId;
		name = builder.name;
		formDefinitions = builder.formDefinitions;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	static List<EventDefinitionDocument> fromSnapshots(List<EventDefinitionSnapshot> eventDefinitions) {
		if (eventDefinitions == null) {
			return Collections.emptyList();
		}
		return eventDefinitions.stream()
				.map(s -> new EventDefinitionDocument(s.getId()
						.getId(), s.getParentId(), s.getName(),
						FormDefinitionDocument.fromSnapshot(s.getFormDefinitionSnapshots())))
				.collect(Collectors.toList());
	}

	public String getId() {
		return id;
	}

	public String getParentId() {
		return parentId;
	}

	public String getName() {
		return name;
	}

	public List<FormDefinitionDocument> getFormDefinitionSnapshots() {
		return formDefinitions;
	}

	public EventDefinitionSnapshot toSnapshot() {
		return EventDefinitionSnapshot.newBuilder()
				.withFormDefinitions(toFormDefinitionSnapshots())
				.withId(EventDefinitionId.of(id))
				.withParentId(parentId)
				.withName(name)
				.build();
	}

	private List<FormDefinitionSnapshot> toFormDefinitionSnapshots() {
		return formDefinitions == null ?
				Collections.emptyList() :
				formDefinitions.stream()
						.map(FormDefinitionDocument::toSnapshot)
						.collect(Collectors.toList());
	}

	public static final class Builder {
		private String id;
		private String parentId;
		private String name;
		private List<FormDefinitionDocument> formDefinitions;

		private Builder() {
		}

		public Builder withId(final String val) {
			id = val;
			return this;
		}

		public Builder withParentId(final String val) {
			parentId = val;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withFormDefinitions(final List<FormDefinitionDocument> val) {
			formDefinitions = val;
			return this;
		}

		public EventDefinitionDocument build() {
			return new EventDefinitionDocument(this);
		}
	}
}
