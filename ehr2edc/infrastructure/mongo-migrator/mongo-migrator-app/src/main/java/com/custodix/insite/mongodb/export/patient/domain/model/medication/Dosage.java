package com.custodix.insite.mongodb.export.patient.domain.model.medication;

import java.math.BigDecimal;

public final class Dosage {
	private final BigDecimal value;
	private final String unit;

	private Dosage(Builder builder) {
		value = builder.value;
		unit = builder.unit;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public BigDecimal getValue() {
		return value;
	}

	public String getUnit() {
		return unit;
	}

	public static final class Builder {
		private BigDecimal value;
		private String unit;

		private Builder() {
		}

		public Builder withValue(BigDecimal value) {
			this.value = value;
			return this;
		}

		public Builder withUnit(String unit) {
			this.unit = unit;
			return this;
		}

		public Dosage build() {
			return new Dosage(this);
		}
	}
}
