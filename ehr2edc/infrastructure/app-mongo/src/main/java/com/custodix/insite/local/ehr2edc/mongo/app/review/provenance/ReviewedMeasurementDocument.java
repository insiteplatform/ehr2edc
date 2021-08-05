package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import java.math.BigDecimal;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.provenance.model.Measurement;

final class ReviewedMeasurementDocument {
	private final BigDecimal lowerLimit;
	private final BigDecimal upperLimit;
	private final BigDecimal value;
	private final String unit;

	@PersistenceConstructor
	private ReviewedMeasurementDocument(BigDecimal lowerLimit, BigDecimal upperLimit, BigDecimal value, String unit) {
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.value = value;
		this.unit = unit;
	}

	private ReviewedMeasurementDocument(Builder builder) {
		lowerLimit = builder.lowerLimit;
		upperLimit = builder.upperLimit;
		value = builder.value;
		unit = builder.unit;
	}

	Measurement restore() {
		return Measurement.newBuilder()
				.withLowerLimit(lowerLimit)
				.withUpperLimit(upperLimit)
				.withValue(value)
				.withUnit(unit)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
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

		public ReviewedMeasurementDocument build() {
			return new ReviewedMeasurementDocument(this);
		}
	}
}
