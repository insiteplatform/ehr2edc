package com.custodix.insite.local.ehr2edc.mongo.app.audit;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.local.ehr2edc.mongo.app.document.FormDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.review.ReviewedEventDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceDataPointDocumentFactory;
import com.custodix.insite.local.ehr2edc.populator.PopulatedForm;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

@Document(collection = ReviewContextDocument.COLLECTION)
public class ReviewContextDocument {
	public static final String COLLECTION = "reviewContextMongoSnapshot_v2";
	private final String id;
	private final Instant reviewDate;
	private final List<FormDocument> populatedForms;
	private final ReviewedEventDocument reviewedEvent;
	private final String submittedXml;
	private final String reviewerUserId;

	@PersistenceConstructor
	ReviewContextDocument(String id, Instant reviewDate, List<FormDocument> populatedForms,
			ReviewedEventDocument reviewedEvent, String submittedXml, String reviewerUserId) {
		this.id = id;
		this.reviewDate = reviewDate;
		this.populatedForms = populatedForms;
		this.reviewedEvent = reviewedEvent;
		this.submittedXml = submittedXml;
		this.reviewerUserId = reviewerUserId;
	}

	private ReviewContextDocument(Builder builder) {
		id = builder.id;
		reviewDate = builder.reviewDate;
		populatedForms = builder.populatedForms;
		reviewedEvent = builder.reviewedEvent;
		submittedXml = builder.submittedXml;
		reviewerUserId = builder.reviewerUserId;
	}

	public static Builder newBuilder(ReviewContextDocument copy) {
		Builder builder = new Builder();
		builder.id = copy.getId();
		builder.reviewDate = copy.getReviewDate();
		builder.populatedForms = copy.getPopulatedForms();
		builder.reviewedEvent = copy.getReviewedEvent();
		builder.submittedXml = copy.getSubmittedXml();
		builder.reviewerUserId = copy.getReviewerUserId();
		return builder;
	}

	public String getId() {
		return id;
	}

	public Instant getReviewDate() {
		return reviewDate;
	}

	public List<FormDocument> getPopulatedForms() {
		return populatedForms;
	}

	public ReviewedEventDocument getReviewedEvent() {
		return reviewedEvent;
	}

	public String getSubmittedXml() {
		return submittedXml;
	}

	public String getReviewerUserId() {
		return reviewerUserId;
	}

	public SubmittedEvent toSubmittedEvent() {
		return SubmittedEvent.newBuilder()
				.withId(SubmittedEventId.of(id))
				.withSubmittedDate(reviewDate)
				.withPopulatedForms(toPopulatedForms())
				.withStudyId(StudyId.of(reviewedEvent.getStudyId()))
				.withSubjectId(SubjectId.of(reviewedEvent.getSubjectId()))
				.withSubmittedForms(reviewedEvent.toReviewedForms())
				.withEventDefinitionId(EventDefinitionId.of(reviewedEvent.getEventDefinitionId()))
				.withEventParentId(reviewedEvent.getEventParentId())
				.withPopulatedEventId(EventId.of(reviewedEvent.getPopulatedEventId()))
				.withSubmitter(UserIdentifier.of(reviewerUserId))
				.withReferenceDate(reviewedEvent.getReferenceDate())
				.withPopulationTime(reviewedEvent.getPopulationTime())
				.withPopulator(reviewedEvent.getPopulator().map(UserIdentifier::of).orElse(null))
				.build();
	}

	private List<PopulatedForm> toPopulatedForms() {
		return populatedForms.stream()
				.map(FormDocument::toForm)
				.collect(toList());
	}

	public static ReviewContextDocument create(SubmittedEvent submittedEvent,
			ReviewedProvenanceDataPointDocumentFactory reviewedProvenanceDataPointDocumentFactory) {
		return newBuilder().withId(submittedEvent.getId()
				.getId())
				.withReviewDate(submittedEvent.getSubmittedDate())
				.withPopulatedForms(toFormSnapshots(submittedEvent))
				.withReviewedEvent(ReviewedEventDocument.of(submittedEvent, reviewedProvenanceDataPointDocumentFactory))
				.withReviewerUserId(submittedEvent.getSubmitter().getId())
				.build();
	}

	private static List<FormDocument> toFormSnapshots(SubmittedEvent submittedEvent) {
		return submittedEvent.getPopulatedForms()
				.stream()
				.map(FormDocument::restoreFrom)
				.collect(toList());
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String id;
		private Instant reviewDate;
		private List<FormDocument> populatedForms;
		private ReviewedEventDocument reviewedEvent;
		private String submittedXml;
		private String reviewerUserId;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withReviewDate(Instant reviewDate) {
			this.reviewDate = reviewDate;
			return this;
		}

		public Builder withPopulatedForms(List<FormDocument> populatedForms) {
			this.populatedForms = populatedForms;
			return this;
		}

		public Builder withReviewedEvent(ReviewedEventDocument reviewedEvent) {
			this.reviewedEvent = reviewedEvent;
			return this;
		}

		public Builder withSubmittedXml(String submittedXml) {
			this.submittedXml = submittedXml;
			return this;
		}

		public Builder withReviewerUserId(String val) {
			reviewerUserId = val;
			return this;
		}

		public ReviewContextDocument build() {
			return new ReviewContextDocument(this);
		}
	}
}
