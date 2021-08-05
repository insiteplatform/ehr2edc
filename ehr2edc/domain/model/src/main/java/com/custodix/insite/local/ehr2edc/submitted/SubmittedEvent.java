package com.custodix.insite.local.ehr2edc.submitted;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.populator.PopulatedForm;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public class SubmittedEvent {
	private final SubmittedEventId id;
	private final StudyId studyId;
	private final SubjectId subjectId;
	private final List<SubmittedForm> submittedForms;
	private final EventDefinitionId eventDefinitionId;
	private final String eventParentId;
	private final EventId populatedEventId;
	private final Instant submittedDate;
	private final List<PopulatedForm> populatedForms;
	private final UserIdentifier submitter;
	private final Instant populationTime;
	private final LocalDate referenceDate;
	private final UserIdentifier populator;

	private SubmittedEvent(Builder builder) {
		id = builder.id;
		studyId = builder.studyId;
		subjectId = builder.subjectId;
		submittedForms = builder.submittedForms;
		eventDefinitionId = builder.eventDefinitionId;
		eventParentId = builder.eventParentId;
		populatedEventId = builder.populatedEventId;
		submittedDate = builder.submittedDate;
		populatedForms = builder.populatedForms;
		submitter = builder.submitter;
		populationTime = builder.populationTime;
		referenceDate = builder.referenceDate;
		populator = builder.populator;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public List<SubmittedForm> getSubmittedForms() {
		return submittedForms;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public StudyId getStudyId() {
		return studyId;
	}

	public EventDefinitionId getEventDefinitionId() {
		return eventDefinitionId;
	}

	public String getEventParentId() {
		return eventParentId;
	}

	public EventId getPopulatedEventId() {
		return populatedEventId;
	}

	public Instant getSubmittedDate() {
		return submittedDate;
	}

	public List<PopulatedForm> getPopulatedForms() {
		return populatedForms;
	}

	public SubmittedEventId getId() {
		return id;
	}

	public UserIdentifier getSubmitter() {
		return submitter;
	}

	public Optional<UserIdentifier> getPopulator() {
		return Optional.ofNullable(populator);
	}

	public SubmittedForm getSubmittedForm(FormDefinitionId formDefinitionId) {
		return submittedForms.stream()
				.filter(f -> formDefinitionId.equals(f.getFormDefinitionId()))
				.findFirst()
				.orElseThrow(() -> DomainException.of("study.submitted.form.definition.unknown", formDefinitionId.getId(),
						id.getId()));
	}

	public SubmittedItemGroup getSubmittedItemGroup(FormDefinitionId formDefinitionId,
			ItemGroupDefinitionId itemGroupDefinitionId) {
		return getSubmittedForm(formDefinitionId).getSubmittedItemGroup(itemGroupDefinitionId);
	}

	public SubmittedItem getSubmittedItem(FormDefinitionId formDefinitionId,
			ItemGroupDefinitionId itemGroupDefinitionId, ItemDefinitionId itemDefinitionId) {
		return getSubmittedForm(formDefinitionId).getSubmittedItem(itemGroupDefinitionId, itemDefinitionId);
	}

	public ProvenanceDataPoint getSubmittedItemProvenance(SubmittedItemId submittedItemId) {
		SubmittedItem item = getSubmittedItem(submittedItemId);
		return Optional.ofNullable(item.getDataPoint())
				.orElseThrow(() -> DomainException.of("study.submitted.item.provenance.unknown", id.getId(),
						submittedItemId.getId()));
	}

	private Optional<SubmittedItem> findSubmittedItem(SubmittedItemId submittedItemId) {
		return submittedForms.stream()
				.map(f -> f.findSubmittedItem(submittedItemId))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
	}

	private SubmittedItem getSubmittedItem(SubmittedItemId submittedItemId) {
		return findSubmittedItem(submittedItemId).orElseThrow(
				() -> DomainException.of("study.submitted.item.unknown", id.getId(), submittedItemId.getId()));
	}

	public Instant getPopulationTime() {
		return populationTime;
	}

	public LocalDate getReferenceDate() {
		return referenceDate;
	}

	public static final class Builder {
		private SubmittedEventId id;
		private StudyId studyId;
		private SubjectId subjectId;
		private List<SubmittedForm> submittedForms;
		private EventDefinitionId eventDefinitionId;
		private String eventParentId;
		private EventId populatedEventId;
		private Instant submittedDate;
		private List<PopulatedForm> populatedForms;
		private UserIdentifier submitter;
		private Instant populationTime;
		private LocalDate referenceDate;
		private UserIdentifier populator;

		private Builder() {
		}

		public Builder withId(final SubmittedEventId val) {
			id = val;
			return this;
		}

		public Builder withStudyId(final StudyId val) {
			studyId = val;
			return this;
		}

		public Builder withSubjectId(final SubjectId val) {
			subjectId = val;
			return this;
		}

		public Builder withSubmittedForms(final List<SubmittedForm> val) {
			submittedForms = val;
			return this;
		}

		public Builder withEventDefinitionId(final EventDefinitionId val) {
			eventDefinitionId = val;
			return this;
		}

		public Builder withEventParentId(final String val) {
			eventParentId = val;
			return this;
		}

		public Builder withPopulatedEventId(final EventId val) {
			populatedEventId = val;
			return this;
		}

		public Builder withSubmittedDate(final Instant val) {
			submittedDate = val;
			return this;
		}

		public Builder withPopulatedForms(final List<PopulatedForm> val) {
			populatedForms = val;
			return this;
		}

		public Builder withSubmitter(final UserIdentifier val) {
			submitter = val;
			return this;
		}

		public Builder withPopulationTime(Instant val) {
			populationTime = val;
			return this;
		}

		public Builder withReferenceDate(LocalDate val) {
			referenceDate = val;
			return this;
		}

		public Builder withPopulator(UserIdentifier val) {
			populator = val;
			return this;
		}

		public SubmittedEvent build() {
			return new SubmittedEvent(this);
		}
	}
}