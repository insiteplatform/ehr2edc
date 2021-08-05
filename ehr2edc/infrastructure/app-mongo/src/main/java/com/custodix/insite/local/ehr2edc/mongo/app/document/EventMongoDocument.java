package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulatedForm;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

@Document(collection = "Event")
public class EventMongoDocument {
	private final String instanceId;
	private final String subjectId;
	private final LocalDate referenceDate;
	private final Instant populationTime;
	private final String eventDefinitionId;
	private final String name;
	private final List<FormDocument> forms;
	private final String edcSubjectReference;
	private final String studyId;
	private final String eventParentId;
	private final String populator;

	@PersistenceConstructor
	//CHECKSTYLE:OFF
	public EventMongoDocument(String instanceId, String subjectId, LocalDate referenceDate, Instant populationTime,
			String eventDefinitionId, String name, List<FormDocument> forms, String edcSubjectReference, String studyId,
			String eventParentId, String populator) {
		this.instanceId = instanceId;
		this.subjectId = subjectId;
		this.referenceDate = referenceDate;
		this.populationTime = populationTime;
		this.eventDefinitionId = eventDefinitionId;
		this.name = name;
		this.forms = forms;
		this.edcSubjectReference = edcSubjectReference;
		this.studyId = studyId;
		this.eventParentId = eventParentId;
		this.populator = populator;
	}
	//CHECKSTYLE:ON

	private EventMongoDocument(Builder builder) {
		instanceId = builder.instanceId;
		subjectId = builder.subjectId;
		referenceDate = builder.referenceDate;
		populationTime = builder.populationTime;
		eventDefinitionId = builder.eventDefinitionId;
		name = builder.name;
		forms = builder.forms;
		edcSubjectReference = builder.edcSubjectReference;
		studyId = builder.studyId;
		eventParentId = builder.eventParentId;
		populator = builder.populator;
	}

	public String getEventParentId() {
		return eventParentId;
	}

	public String getEdcSubjectReference() {
		return edcSubjectReference;
	}

	public String getStudyId() {
		return studyId;
	}

	public List<FormDocument> getForms() {
		return forms;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getSubjectId() {
		return subjectId;
	}

	public LocalDate getReferenceDate() {
		return referenceDate;
	}

	public Instant getPopulationTime() {
		return populationTime;
	}

	public String getEventDefinitionId() {
		return eventDefinitionId;
	}

	public String getName() {
		return name;
	}

	public Optional<String> getPopulator() {
		return Optional.ofNullable(populator);
	}

	public PopulatedEvent toEvent() {
		return PopulatedEvent.newBuilder()
				.withForms(mapToForms())
				.withInstanceId(EventId.of(instanceId))
				.withEventDefinitionId(EventDefinitionId.of(eventDefinitionId))
				.withReferenceDate(referenceDate)
				.withPopulationTime(populationTime)
				.withStudyId(StudyId.of(studyId))
				.withSubjectId(SubjectId.of(subjectId))
				.withEdcSubjectReference(EDCSubjectReference.of(edcSubjectReference))
				.withEventParentId(eventParentId)
				.withName(name)
				.withPopulator(populator == null ? null : UserIdentifier.of(populator))
				.build();
	}

	private List<PopulatedForm> mapToForms() {
		return forms.stream()
				.map(FormDocument::toForm)
				.collect(Collectors.toList());
	}

	public static EventMongoDocument from(PopulatedEvent populatedEvent) {
		return EventMongoDocument.newBuilder()
				.withEventDefinitionId(populatedEvent.getEventDefinitionId()
						.getId())
				.withReferenceDate(populatedEvent.getReferenceDate())
				.withSubjectId(populatedEvent.getSubjectId()
						.getId())
				.withInstanceId(populatedEvent.getInstanceId()
						.getId())
				.withPopulationTime(populatedEvent.getPopulationTime())
				.withForms(mapToFormDocuments(populatedEvent))
				.withStudyId(populatedEvent.getStudyId()
						.getId())
				.withEdcSubjectReference(populatedEvent.getEdcSubjectReference()
						.getId())
				.withName(populatedEvent.getName())
				.withEventParentId(populatedEvent.getEventParentId())
				.withPopulator(mapPopulator(populatedEvent))
				.build();
	}

	private static String mapPopulator(PopulatedEvent populatedEvent) {
		return populatedEvent.getPopulator()
				.map(UserIdentifier::getId)
				.orElse(null);
	}

	private static List<FormDocument> mapToFormDocuments(PopulatedEvent populatedEvent) {
		return populatedEvent.getPopulatedForms()
				.stream()
				.map(FormDocument::restoreFrom)
				.collect(Collectors.toList());
	}

	public String getInstanceId() {
		return instanceId;
	}

	public static final class Builder {
		private String subjectId;
		private LocalDate referenceDate;
		private Instant populationTime;
		private String eventDefinitionId;
		private String name;
		private List<FormDocument> forms;
		private String edcSubjectReference;
		private String studyId;
		private String eventParentId;
		private String populator;
		private String instanceId;

		private Builder() {
		}

		public Builder withSubjectId(String subjectId) {
			this.subjectId = subjectId;
			return this;
		}

		public Builder withReferenceDate(LocalDate referenceDate) {
			this.referenceDate = referenceDate;
			return this;
		}

		public Builder withPopulationTime(Instant populationTime) {
			this.populationTime = populationTime;
			return this;
		}

		public Builder withEventDefinitionId(String eventDefinitionId) {
			this.eventDefinitionId = eventDefinitionId;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withForms(List<FormDocument> forms) {
			this.forms = forms;
			return this;
		}

		public Builder withEdcSubjectReference(String edcSubjectReference) {
			this.edcSubjectReference = edcSubjectReference;
			return this;
		}

		public Builder withStudyId(String studyId) {
			this.studyId = studyId;
			return this;
		}

		public Builder withEventParentId(String eventParentId) {
			this.eventParentId = eventParentId;
			return this;
		}

		public Builder withPopulator(String val) {
			populator = val;
			return this;
		}

		public EventMongoDocument build() {
			return new EventMongoDocument(this);
		}

		public Builder withInstanceId(String instanceId) {
			this.instanceId = instanceId;
			return this;
		}
	}
}
