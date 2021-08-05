package com.custodix.insite.local.ehr2edc.mongo.app.review;

import com.custodix.insite.local.ehr2edc.submitted.SubmittedLabeledValue;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.List;
import java.util.stream.Collectors;

public final class ReviewedLabeledValueDocument {

	private final String value;
	private final List<ReviewedLabelDocument> labels;

	@PersistenceConstructor
	ReviewedLabeledValueDocument(String value, List<ReviewedLabelDocument> labels) {
		this.value = value;
		this.labels = labels;
	}

	private ReviewedLabeledValueDocument(Builder builder) {
		value = builder.value;
		labels = builder.labels;
	}

	public String getValue() {
		return value;
	}

	public List<ReviewedLabelDocument> getLabels() {
		return labels;
	}

	static ReviewedLabeledValueDocument toDocument(SubmittedLabeledValue reviewedLabeledValue) {
		return ReviewedLabeledValueDocument.newBuilder()
				.withValue(reviewedLabeledValue.getValue())
				.withLabels(reviewedLabeledValue.getLabels().stream().map(ReviewedLabelDocument::toDocument).collect(Collectors.toList()))
				.build();
	}

	SubmittedLabeledValue toReviewedLabeledValue() {
		return SubmittedLabeledValue.newBuilder()
				.withValue(value)
				.withLabels(labels.stream().map(ReviewedLabelDocument::toReviewedLabel).collect(Collectors.toList()))
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String value;
		private List<ReviewedLabelDocument> labels;

		private Builder() {
		}

		public Builder withValue(String val) {
			value = val;
			return this;
		}

		public Builder withLabels(List<ReviewedLabelDocument> val) {
			labels = val;
			return this;
		}

		public ReviewedLabeledValueDocument build() {
			return new ReviewedLabeledValueDocument(this);
		}
	}
}
