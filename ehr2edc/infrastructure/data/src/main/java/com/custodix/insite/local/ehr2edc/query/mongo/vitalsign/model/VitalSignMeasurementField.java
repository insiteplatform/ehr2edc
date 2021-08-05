package com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.PersistenceConstructor;

public final class VitalSignMeasurementField {
	private final BigDecimal lowerLimit;
	private final BigDecimal upperLimit;
	private final BigDecimal value;
	private final String unit;

	@PersistenceConstructor
	private VitalSignMeasurementField(BigDecimal lowerLimit, BigDecimal upperLimit, BigDecimal value, String unit) {
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.value = value;
		this.unit = unit;
	}

	private VitalSignMeasurementField(Builder builder) {
		lowerLimit = builder.lowerLimit;
		upperLimit = builder.upperLimit;
		value = builder.value;
		unit = builder.unit;
	}

	public BigDecimal getLowerLimit() {
		return lowerLimit;
	}

	public BigDecimal getUpperLimit() {
		return upperLimit;
	}

	public BigDecimal getValue() {
		return value;
	}

	public String getUnit() {
		return unit;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(VitalSignMeasurementField copy) {
		Builder builder = new Builder();
		builder.lowerLimit = copy.getLowerLimit();
		builder.upperLimit = copy.getUpperLimit();
		builder.value = copy.getValue();
		builder.unit = copy.getUnit();
		return builder;
	}

	public static final class Builder {
		private BigDecimal lowerLimit;
		private BigDecimal upperLimit;
		private BigDecimal value;
		private String unit;

		private Builder() {
		}

		public Builder withLowerLimit(BigDecimal lowerLimit) {
			this.lowerLimit = lowerLimit;
			return this;
		}

		public Builder withUpperLimit(BigDecimal upperLimit) {
			this.upperLimit = upperLimit;
			return this;
		}

		public Builder withValue(BigDecimal value) {
			this.value = value;
			return this;
		}

		public Builder withUnit(String unit) {
			this.unit = unit;
			return this;
		}

		public VitalSignMeasurementField build() {
			return new VitalSignMeasurementField(this);
		}
	}
}
