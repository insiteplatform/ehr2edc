package com.custodix.insite.local.ehr2edc.mongo.app.review;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceDataPointDocumentFactory;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedForm;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

public final class ReviewedEventDocument {
	private final String studyId;
	private final String subjectId;
	private final Collection<ReviewedFormMongoSnapshot> reviewedForms;
	private final String eventDefinitionId;
	private final String eventParentId;
	private final String populatedEventId;
	private final LocalDate referenceDate;
	private final Instant populationTime;
	private final String populator;

	//CHECKSTYLE:OFF
	@PersistenceConstructor
	ReviewedEventDocument(String studyId, String subjectId, Collection<ReviewedFormMongoSnapshot> reviewedForms,
			String eventDefinitionId, String eventParentId, String populatedEventId, LocalDate referenceDate,
			Instant populationTime, String populator) {
		this.studyId = studyId;
		this.subjectId = subjectId;
		this.reviewedForms = reviewedForms;
		this.eventDefinitionId = eventDefinitionId;
		this.eventParentId = eventParentId;
		this.populatedEventId = populatedEventId;
		this.referenceDate = referenceDate;
		this.populationTime = populationTime;
		this.populator = populator;
	}
	//CHECKSTYLE:ON

	private ReviewedEventDocument(Builder builder) {
		studyId = builder.studyId;
		subjectId = builder.subjectId;
		reviewedForms = builder.reviewedForms;
		eventDefinitionId = builder.eventDefinitionId;
		eventParentId = builder.eventParentId;
		populatedEventId = builder.populatedEventId;
		referenceDate = builder.referenceDate;
		populationTime = builder.populationTime;
		populator = builder.populator;
	}

	public static ReviewedEventDocument of(
			SubmittedEvent reviewedEvent,
			ReviewedProvenanceDataPointDocumentFactory reviewedProvenanceDataPointDocumentFactory) {
		return ReviewedEventDocument.newBuilder()
				.withStudyId(reviewedEvent.getStudyId().getId())
				.withSubjectId(reviewedEvent.getSubjectId().getId())
				.withEventDefinitionId(reviewedEvent.getEventDefinitionId().getId())
				.withEventParentId(reviewedEvent.getEventParentId())
				.withReviewedForms(toReviewedFormMongoSnapshots(reviewedEvent, reviewedProvenanceDataPointDocumentFactory))
				.withPopulatedEventId(reviewedEvent.getPopulatedEventId().getId())
				.withPopulationTime(reviewedEvent.getPopulationTime())
				.withReferenceDate(reviewedEvent.getReferenceDate())
				.withPopulator(reviewedEvent.getPopulator().map(UserIdentifier::getId).orElse(null))
				.build();
	}

	private static List<ReviewedFormMongoSnapshot> toReviewedFormMongoSnapshots(
			SubmittedEvent reviewedEvent,
			ReviewedProvenanceDataPointDocumentFactory reviewedProvenanceDataPointDocumentFactory) {
		return reviewedEvent.getSubmittedForms().stream()
				.map(f -> ReviewedFormMongoSnapshot.of(f, reviewedProvenanceDataPointDocumentFactory))
				.collect(toList());
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Collection<ReviewedFormMongoSnapshot> getReviewedForms() {
		return reviewedForms;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public String getStudyId() {
		return studyId;
	}

	public String getEventDefinitionId() {
		return eventDefinitionId;
	}

	public String getEventParentId() {
		return eventParentId;
	}

	public String getPopulatedEventId() {
		return populatedEventId;
	}

	public Optional<String> getPopulator() {
		return Optional.ofNullable(populator);
	}

	public List<SubmittedForm> toReviewedForms() {
		return reviewedForms.stream()
				.map(ReviewedFormMongoSnapshot::toReviewedForm)
				.collect(toList());
	}

	public LocalDate getReferenceDate() {
		return referenceDate;
	}

	public Instant getPopulationTime() {
		return populationTime;
	}

	public static final class Builder {
		private String studyId;
		private String subjectId;
		private Collection<ReviewedFormMongoSnapshot> reviewedForms;
		private String eventDefinitionId;
		private String eventParentId;
		private String populatedEventId;
		private LocalDate referenceDate;
		private Instant populationTime;
		private String populator;

		private Builder() {
		}

		public Builder withStudyId(String studyId) {
			this.studyId = studyId;
			return this;
		}

		public Builder withSubjectId(String subjectId) {
			this.subjectId = subjectId;
			return this;
		}

		public Builder withReviewedForms(Collection<ReviewedFormMongoSnapshot> reviewedForms) {
			this.reviewedForms = reviewedForms;
			return this;
		}

		public Builder withEventDefinitionId(String eventDefinitionId) {
			this.eventDefinitionId = eventDefinitionId;
			return this;
		}

		public Builder withEventParentId(String eventParentId) {
			this.eventParentId = eventParentId;
			return this;
		}

		public Builder withPopulatedEventId(String populatedEventId) {
			this.populatedEventId = populatedEventId;
			return this;
		}

		public Builder withReferenceDate(LocalDate val) {
			referenceDate = val;
			return this;
		}

		public Builder withPopulationTime(Instant val) {
			populationTime = val;
			return this;
		}

		public Builder withPopulator(String val) {
			populator = val;
			return this;
		}

		public ReviewedEventDocument build() {
			return new ReviewedEventDocument(this);
		}
	}
}