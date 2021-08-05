package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import com.custodix.insite.local.ehr2edc.provenance.model.Dosage;

import org.springframework.data.annotation.PersistenceConstructor;

import java.math.BigDecimal;

final class ReviewedDosageDocument {
	private final BigDecimal value;
	private final String unit;

	@PersistenceConstructor
	private ReviewedDosageDocument(BigDecimal value, String unit) {
		this.value = value;
		this.unit = unit;
	}

	private ReviewedDosageDocument(Builder builder) {
		value = builder.value;
		unit = builder.unit;
	}

	Dosage restore() {
		return Dosage.newBuilder()
				.withValue(value)
				.withUnit(unit)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
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

		public ReviewedDosageDocument build() {
			return new ReviewedDosageDocument(this);
		}
	}
}
