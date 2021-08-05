package com.custodix.insite.mongodb.export.patient.domain.model.demographic;

import java.util.Optional;

public final class PatientGender implements DemographicFact {

	private static final String DEMOGRAPHIC_TYPE = "GENDER";

	private final String sourceValue;
	private final Gender value;

	private PatientGender(Builder builder) {
		sourceValue = builder.sourceValue;
		value = builder.interpretedValue;
	}

	public String getSourceValue() {
		return sourceValue;
	}

	public Gender getInterpretedValue() {
		return value;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public String factType() {
		return DEMOGRAPHIC_TYPE;
	}

	@Override
	public String factValue() {
		return Optional.ofNullable(value)
				.map(Gender::toString)
				.orElse("");
	}

	public static final class Builder {
		private String sourceValue;
		private Gender interpretedValue;

		private Builder() {
		}

		public Builder withSourceValue(String val) {
			sourceValue = val;
			return this;
		}

		public Builder withInterpretedValue(Gender val) {
			interpretedValue = val;
			return this;
		}

		public PatientGender build() {
			return new PatientGender(this);
		}
	}

	public enum Gender {
		MALE,
		FEMALE,
		UNKNOWN;

		@Override
		public String toString() {
			return this.name()
					.toLowerCase();
		}
	}
}