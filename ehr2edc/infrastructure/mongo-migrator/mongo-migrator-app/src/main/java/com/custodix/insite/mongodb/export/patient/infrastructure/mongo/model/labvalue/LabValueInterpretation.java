package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue;

import org.springframework.data.annotation.PersistenceConstructor;

public final class LabValueInterpretation {
	private final Integer parsedInterpretation;
	private final String originalInterpretation;

	@PersistenceConstructor
	private LabValueInterpretation(Integer parsedInterpretation, String originalInterpretation) {
		this.parsedInterpretation = parsedInterpretation;
		this.originalInterpretation = originalInterpretation;
	}

	private LabValueInterpretation(Builder builder) {
		parsedInterpretation = builder.parsedInterpretation;
		originalInterpretation = builder.originalInterpretation;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Integer getParsedInterpretation() {
		return parsedInterpretation;
	}

	public String getOriginalInterpretation() {
		return originalInterpretation;
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

		public LabValueInterpretation build() {
			return new LabValueInterpretation(this);
		}
	}
}
