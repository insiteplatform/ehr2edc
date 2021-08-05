package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model;

import org.springframework.data.annotation.PersistenceConstructor;

public final class LabValueInterpretationField {
	private final Integer parsedInterpretation;
	private final String originalInterpretation;

	@PersistenceConstructor
	private LabValueInterpretationField(Integer parsedInterpretation, String originalInterpretation) {
		this.parsedInterpretation = parsedInterpretation;
		this.originalInterpretation = originalInterpretation;
	}

	private LabValueInterpretationField(Builder builder) {
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

		public LabValueInterpretationField build() {
			return new LabValueInterpretationField(this);
		}
	}
}
