package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.provenance.model.LabValueInterpretation;

final class LabValueInterpretationDocument {
	private final Integer parsedInterpretation;
	private final String originalInterpretation;

	@PersistenceConstructor
	private LabValueInterpretationDocument(Integer parsedInterpretation, String originalInterpretation) {
		this.parsedInterpretation = parsedInterpretation;
		this.originalInterpretation = originalInterpretation;
	}

	private LabValueInterpretationDocument(Builder builder) {
		parsedInterpretation = builder.parsedInterpretation;
		originalInterpretation = builder.originalInterpretation;
	}

	public static LabValueInterpretationDocument toDocument(LabValueInterpretation qualitativeResult) {
		return LabValueInterpretationDocument.newBuilder()
				.withParsedInterpretation(qualitativeResult.getParsedInterpretation())
				.withOriginalInterpretation(qualitativeResult.getOriginalInterpretation())
				.build();
	}

	public LabValueInterpretation restore() {
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

		public LabValueInterpretationDocument build() {
			return new LabValueInterpretationDocument(this);
		}
	}
}
