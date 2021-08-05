package com.custodix.insite.local.ehr2edc.mongo.app.review;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceDataPointDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceDataPointDocumentFactory;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.submitted.*;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedItemId;

public final class ReviewedItemMongoSnapshot {
	private final String instanceId;
	private final String id;
	private final ReviewedLabeledValueDocument labeledValue;
	private final ReviewedProvenanceDataPointDocument dataPoint;
	private final ReviewedMeasurementUnitReferenceMongoSnapShot unit;
	private final boolean key;
	private final ReviewedItemLabelMongoSnapshot label;
	private final String populatedItemId;
	private final boolean submittedToEDC;

	//CHECKSTYLE:OFF
	@PersistenceConstructor
	ReviewedItemMongoSnapshot(String instanceId, String id, ReviewedLabeledValueDocument labeledValue,
			ReviewedProvenanceDataPointDocument dataPoint, ReviewedMeasurementUnitReferenceMongoSnapShot unit,
			Boolean key, ReviewedItemLabelMongoSnapshot label, String populatedItemId, Boolean submittedToEDC) {
		this.instanceId = instanceId;
		this.id = id;
		this.labeledValue = labeledValue;
		this.dataPoint = dataPoint;
		this.unit = unit;
		this.key = Optional.ofNullable(key)
				.orElse(false);
		this.label = label;
		this.populatedItemId = populatedItemId;
		this.submittedToEDC = submittedToEDC == null || submittedToEDC;
	}
	//CHECKSTYLE:ON

	private ReviewedItemMongoSnapshot(final Builder builder) {
		instanceId = builder.instanceId;
		id = builder.id;
		labeledValue = builder.labeledValue;
		dataPoint = builder.dataPoint;
		unit = builder.unit;
		key = builder.key;
		label = builder.label;
		populatedItemId = builder.populatedItemId;
		submittedToEDC = builder.submittedToEDC;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getId() {
		return id;
	}

	public ReviewedLabeledValueDocument getLabeledValue() {
		return labeledValue;
	}

	public ReviewedMeasurementUnitReferenceMongoSnapShot getUnit() {
		return unit;
	}

	public String getPopulatedItemId() {
		return populatedItemId;
	}

	public static ReviewedItemMongoSnapshot of(SubmittedItem reviewedItem,
			ReviewedProvenanceDataPointDocumentFactory reviewedProvenanceDataPointDocumentFactory) {
		ReviewedProvenanceDataPointDocument dataPoint = reviewedProvenanceDataPointDocumentFactory.create(
				reviewedItem.getDataPoint());
		return newBuilder().withInstanceId(reviewedItem.getInstanceId()
				.getId())
				.withId(reviewedItem.getId()
						.getId())
				.withLabeledValue(ReviewedLabeledValueDocument.toDocument(reviewedItem.getLabeledValue()))
				.withKey(reviewedItem.isKey())
				.withUnit(reviewedItem.getReviewedMeasurementUnitReference()
						.map(ReviewedItemMongoSnapshot::toSnapshot)
						.orElse(null))
				.withLabel(reviewedItem.getLabel()
						.map(ReviewedItemMongoSnapshot::toSnapshot)
						.orElse(null))
				.withPopulatedItemId(reviewedItem.getPopulatedItemId()
						.getId())
				.withSubmittedToEDC(reviewedItem.isSubmittedToEDC())
				.withDataPoint(dataPoint)
				.build();
	}

	private static ReviewedMeasurementUnitReferenceMongoSnapShot toSnapshot(
			final SubmittedMeasurementUnitReference reviewedMeasurementUnitReference) {
		return ReviewedMeasurementUnitReferenceMongoSnapShot.newBuilder()
				.withId(reviewedMeasurementUnitReference.getId())
				.withSubmittedToEDC(reviewedMeasurementUnitReference.isSubmittedToEDC())
				.build();
	}

	private static ReviewedItemLabelMongoSnapshot toSnapshot(final SubmittedItemLabel label) {
		return ReviewedItemLabelMongoSnapshot.newBuilder()
				.withName(label.getName())
				.withQuestion(toSnapshot(label.getQuestion()))
				.build();
	}

	private static ReviewedQuestionMongoSnapshot toSnapshot(SubmittedQuestion question) {
		return Optional.ofNullable(question)
				.map(ReviewedQuestionMongoSnapshot::fromSnapshot)
				.orElse(null);
	}

	public boolean isKey() {
		return key;
	}

	SubmittedItem toReviewedItem() {

		return SubmittedItem.newBuilder()
				.withInstanceId(SubmittedItemId.of(instanceId))
				.withId(ItemDefinitionId.of(id))
				.withKey(key)
				.withSubmittedMeasurementUnitReference(Optional.ofNullable(unit)
						.map(ReviewedMeasurementUnitReferenceMongoSnapShot::toReviewedMeasurementUnitReference)
						.orElse(null))
				.withLabel(Optional.ofNullable(label)
						.map(ReviewedItemLabelMongoSnapshot::toReviewedItemLabel)
						.orElse(null))
				.withPopulatedItemId(ItemId.of(populatedItemId))
				.withSubmittedToEDC(submittedToEDC)
				.withValue(getReviewedLabeledValue())
				.withDataPoint(restoreDataPoint())
				.build();
	}

	private ProvenanceDataPoint restoreDataPoint() {
		return dataPoint != null ? dataPoint.restore() : null;
	}

	private SubmittedLabeledValue getReviewedLabeledValue() {
		SubmittedLabeledValue.Builder reviewedLabeledValueBuilder = SubmittedLabeledValue.newBuilder()
				.withValue(labeledValue.getValue());

		if (labeledValue.getLabels() != null) {
			reviewedLabeledValueBuilder.withLabels(labeledValue.getLabels()
					.stream()
					.map(ReviewedLabelDocument::toReviewedLabel)
					.collect(Collectors.toList()));
		}

		return reviewedLabeledValueBuilder.build();
	}

	public static final class Builder {
		private String instanceId;
		private String id;
		private ReviewedLabeledValueDocument labeledValue;
		private ReviewedProvenanceDataPointDocument dataPoint;
		private ReviewedMeasurementUnitReferenceMongoSnapShot unit;
		private boolean key;
		private ReviewedItemLabelMongoSnapshot label;
		private String populatedItemId;
		private boolean submittedToEDC;

		private Builder() {
		}

		public Builder withInstanceId(String instanceId) {
			this.instanceId = instanceId;
			return this;
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withSubmittedToEDC(boolean value) {
			this.submittedToEDC = value;
			return this;
		}

		public Builder withLabeledValue(final ReviewedLabeledValueDocument val) {
			labeledValue = val;
			return this;
		}

		public Builder withDataPoint(final ReviewedProvenanceDataPointDocument val) {
			dataPoint = val;
			return this;
		}

		public Builder withUnit(final ReviewedMeasurementUnitReferenceMongoSnapShot val) {
			unit = val;
			return this;
		}

		public Builder withKey(boolean key) {
			this.key = key;
			return this;
		}

		public Builder withLabel(final ReviewedItemLabelMongoSnapshot val) {
			label = val;
			return this;
		}

		public Builder withPopulatedItemId(String populatedItemId) {
			this.populatedItemId = populatedItemId;
			return this;
		}

		public ReviewedItemMongoSnapshot build() {
			return new ReviewedItemMongoSnapshot(this);
		}
	}
}
