package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.provenance.model.LabValueInterpretation;

final class ReviewedLabValueInterpretationDocument {
	private final Integer parsedInterpretation;
	private final String originalInterpretation;

	@PersistenceConstructor
	private ReviewedLabValueInterpretationDocument(Integer parsedInterpretation, String originalInterpretation) {
		this.parsedInterpretation = parsedInterpretation;
		this.originalInterpretation = originalInterpretation;
	}

	private ReviewedLabValueInterpretationDocument(Builder builder) {
		parsedInterpretation = builder.parsedInterpretation;
		originalInterpretation = builder.originalInterpretation;
	}

	LabValueInterpretation restore() {
		return LabValueInterpretation.newBuilder()
				.withParsedInterpretation(parsedInterpretation)
				.withOriginalInterpretation(originalInterpretation)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private Integer parsedInterpretation;
		private String originalInterpretation;

		private Builder() {
		}

		public Builder withParsedInterpretation(Integer parsedInterpretation) {
			this.parsedInterpretation = parsedInterpretation;
			return this;
		}

		public Builder withOriginalInterpretation(String originalInterpretation) {
			this.originalInterpretation = originalInterpretation;
			return this;
		}

		public ReviewedLabValueInterpretationDocument build() {
			return new ReviewedLabValueInterpretationDocument(this);
		}
	}
}
