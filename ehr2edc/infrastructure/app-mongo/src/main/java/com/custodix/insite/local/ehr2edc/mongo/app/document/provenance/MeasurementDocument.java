package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import java.math.BigDecimal;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.provenance.model.Measurement;

final class MeasurementDocument {
	private final BigDecimal lowerLimit;
	private final BigDecimal upperLimit;
	private final BigDecimal value;
	private final String unit;

	@PersistenceConstructor
	private MeasurementDocument(BigDecimal lowerLimit, BigDecimal upperLimit, BigDecimal value, String unit) {
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.value = value;
		this.unit = unit;
	}

	private MeasurementDocument(Builder builder) {
		lowerLimit = builder.lowerLimit;
		upperLimit = builder.upperLimit;
		value = builder.value;
		unit = builder.unit;
	}

	public static MeasurementDocument toDocument(Measurement quantitativeResult) {
		return MeasurementDocument.newBuilder()
				.withLowerLimit(quantitativeResult.getLowerLimit())
				.withUpperLimit(quantitativeResult.getUpperLimit())
				.withValue(quantitativeResult.getValue())
				.withUnit(quantitativeResult.getUnit())
				.build();
	}

	public Measurement restore() {
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

		public MeasurementDocument build() {
			return new MeasurementDocument(this);
		}
	}
}
