package com.custodix.insite.local.ehr2edc.provenance.model;

public final class LabValueInterpretation {
	private final Integer parsedInterpretation;
	private final String originalInterpretation;

	private LabValueInterpretation(Builder builder) {
		parsedInterpretation = builder.parsedInterpretation;
		originalInterpretation = builder.originalInterpretation;
	}

	public static LabValueInterpretation from(
			com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValueInterpretation labValueInterpretation) {
		return LabValueInterpretation.newBuilder()
				.withParsedInterpretation(labValueInterpretation.getParsedInterpretation())
				.withOriginalInterpretation(labValueInterpretation.getOriginalInterpretation())
				.build();
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
