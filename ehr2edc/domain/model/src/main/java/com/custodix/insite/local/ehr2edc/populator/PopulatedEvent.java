package com.custodix.insite.local.ehr2edc.populator;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

import com.custodix.insite.local.ehr2edc.DomainTime;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolation;
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedForm;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public final class PopulatedEvent {
	private final List<PopulatedForm> populatedForms;
	private final LocalDate referenceDate;
	private final EventId instanceId;
	private final SubjectId subjectId;
	private final Instant populationTime;
	private final EventDefinitionId eventDefinitionId;
	private final String name;
	private final EDCSubjectReference edcSubjectReference;
	private final StudyId studyId;
	private final String eventParentId;
	private final UserIdentifier populator;

	private PopulatedEvent(Builder builder) {
		populatedForms = builder.populatedForms;
		referenceDate = builder.referenceDate;
		instanceId = builder.instanceId;
		subjectId = builder.subjectId;
		populationTime = builder.populationTime;
		eventDefinitionId = builder.eventDefinitionId;
		name = builder.name;
		edcSubjectReference = builder.edcSubjectReference;
		studyId = builder.studyId;
		eventParentId = builder.eventParentId;
		populator = builder.populator;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getName() {
		return name;
	}

	public String getEventParentId() {
		return eventParentId;
	}

	public EDCSubjectReference getEdcSubjectReference() {
		return edcSubjectReference;
	}

	public StudyId getStudyId() {
		return studyId;
	}

	public EventId getInstanceId() {
		return instanceId;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public Instant getPopulationTime() {
		return populationTime;
	}

	public EventDefinitionId getEventDefinitionId() {
		return eventDefinitionId;
	}

	public Optional<UserIdentifier> getPopulator() {
		return Optional.ofNullable(populator);
	}

	public List<PopulatedForm> getPopulatedForms() {
		return populatedForms == null ? Collections.emptyList() : populatedForms;
	}

	public LocalDate getReferenceDate() {
		return referenceDate;
	}

	public long countPopulatedDataPoints() {
		return getPopulatedForms().stream()
				.mapToLong(PopulatedForm::getItemCount)
				.sum();
	}

	public ProvenanceDataPoint getItemProvenance(ItemId itemId) {
		PopulatedItem item = getItem(itemId);
		return Optional.ofNullable(item.getDataPoint())
				.orElseThrow(() -> DomainException.of("study.subject.item.provenance.unknown", subjectId.getId(),
						itemId.getId()));
	}

	public SubmittedEvent review(Map<FormId, PopulatedFormSelection> groupSelections, GetCurrentUser getCurrentUser,
			Consumer<SubmittedEvent> reviewedEventConsumer) {
		final List<SubmittedForm> submittedForms = reviewForms(groupSelections);
		final SubmittedEvent submittedEvent =  SubmittedEvent.newBuilder()
				.withId(SubmittedEventId.newId())
				.withStudyId(studyId)
				.withSubjectId(subjectId)
				.withSubmittedForms(submittedForms)
				.withEventDefinitionId(eventDefinitionId)
				.withEventParentId(eventParentId)
				.withPopulatedEventId(instanceId)
				.withSubmittedDate(DomainTime.now())
				.withPopulatedForms(populatedForms)
				.withSubmitter(getCurrentUser.getUserId())
				.withPopulationTime(populationTime)
				.withReferenceDate(referenceDate)
				.withPopulator(populator)
				.build();

		reviewedEventConsumer.accept(submittedEvent);

		return submittedEvent;
	}

	private List<SubmittedForm> reviewForms(Map<FormId, PopulatedFormSelection> groupSelections) {
		List<SubmittedForm> submittedForms = toReviewedForms(groupSelections);
		validate(submittedForms);
		return submittedForms;
	}

	private List<SubmittedForm> toReviewedForms(Map<FormId, PopulatedFormSelection> groupSelections) {
		return this.populatedForms.stream()
				.filter(form -> groupSelections.containsKey(form.getInstanceId()))
				.map(form -> toReviewedForm(form, groupSelections))
				.collect(toList());
	}

	private SubmittedForm toReviewedForm(PopulatedForm populatedForm, Map<FormId, PopulatedFormSelection> groupSelections) {
		FormId formId = populatedForm.getInstanceId();
		PopulatedFormSelection groupSelection = groupSelections.get(formId);
		return populatedForm.toReviewedForm(groupSelection);
	}

	private void validate(
			List<SubmittedForm> reviewedForms) {
		List<UseCaseConstraintViolation> useCaseConstraintViolations = validateAllForms(reviewedForms);
		if (hasConstraintViolations(useCaseConstraintViolations)) {
			throw new UseCaseConstraintViolationException(useCaseConstraintViolations);
		}
	}

	private List<UseCaseConstraintViolation> validateAllForms(
			List<SubmittedForm> reviewedForms) {
		return reviewedForms.stream()
				.map(SubmittedForm::validateForSubmission)
				.flatMap(Collection::stream)
				.collect(toList());
	}

	private boolean hasConstraintViolations(List<UseCaseConstraintViolation> useCaseConstraintViolations) {
		return !useCaseConstraintViolations.isEmpty();
	}

	private PopulatedItem getItem(ItemId itemId) {
		return populatedForms.stream()
				.map(f -> f.findItemById(itemId))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst()
				.orElseThrow(() -> DomainException.of("study.subject.item.unknown", subjectId.getId(), itemId.getId()));
	}

	public static final class Builder {
		private List<PopulatedForm> populatedForms;
		private LocalDate referenceDate;
		private EventId instanceId;
		private SubjectId subjectId;
		private Instant populationTime;
		private EventDefinitionId eventDefinitionId;
		private String name;
		private EDCSubjectReference edcSubjectReference;
		private StudyId studyId;
		private String eventParentId;
		private UserIdentifier populator;

		private Builder() {
		}

		public Builder withForms(List<PopulatedForm> populatedForms) {
			this.populatedForms = populatedForms;
			return this;
		}

		public Builder withReferenceDate(LocalDate referenceDate) {
			this.referenceDate = referenceDate;
			return this;
		}

		public Builder withInstanceId(EventId instanceId) {
			this.instanceId = instanceId;
			return this;
		}

		public Builder withSubjectId(SubjectId subjectId) {
			this.subjectId = subjectId;
			return this;
		}

		public Builder withPopulationTime(Instant populationTime) {
			this.populationTime = populationTime;
			return this;
		}

		public Builder withEventDefinitionId(EventDefinitionId eventDefinitionId) {
			this.eventDefinitionId = eventDefinitionId;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withEdcSubjectReference(EDCSubjectReference edcSubjectReference) {
			this.edcSubjectReference = edcSubjectReference;
			return this;
		}

		public Builder withStudyId(StudyId studyId) {
			this.studyId = studyId;
			return this;
		}

		public Builder withEventParentId(String eventParentId) {
			this.eventParentId = eventParentId;
			return this;
		}

		public Builder withPopulator(UserIdentifier val) {
			populator = val;
			return this;
		}

		public PopulatedEvent build() {
			return new PopulatedEvent(this);
		}
	}
}