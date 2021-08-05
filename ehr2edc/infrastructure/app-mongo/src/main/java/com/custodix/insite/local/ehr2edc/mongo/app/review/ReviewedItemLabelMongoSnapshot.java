package com.custodix.insite.local.ehr2edc.mongo.app.review;

import com.custodix.insite.local.ehr2edc.submitted.SubmittedItemLabel;
import org.springframework.data.annotation.PersistenceConstructor;

public final class ReviewedItemLabelMongoSnapshot {
	private final String name;
	private final ReviewedQuestionMongoSnapshot question;

	@PersistenceConstructor
	private ReviewedItemLabelMongoSnapshot(final String name, final ReviewedQuestionMongoSnapshot question) {
		this.name = name;
		this.question = question;
	}

	private ReviewedItemLabelMongoSnapshot(final Builder builder) {
		name = builder.name;
		question = builder.question;
	}

	public String getName() {
		return name;
	}

	public ReviewedQuestionMongoSnapshot getQuestion() {
		return question;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public SubmittedItemLabel toReviewedItemLabel() {
		SubmittedItemLabel.Builder builder = SubmittedItemLabel.newBuilder()
				.withName(getName());
		if(question != null) {
			builder.withQuestion(question.toReviewQuestion());
		}
		return builder.build();
	}

	public static final class Builder {
		private String name;
		private ReviewedQuestionMongoSnapshot question;

		private Builder() {
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withQuestion(final ReviewedQuestionMongoSnapshot val) {
			question = val;
			return this;
		}

		public ReviewedItemLabelMongoSnapshot build() {
			return new ReviewedItemLabelMongoSnapshot(this);
		}
	}
}
