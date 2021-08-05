package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import java.math.BigDecimal;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.provenance.model.Dosage;

final class DosageDocument {
	private final BigDecimal value;
	private final String unit;

	@PersistenceConstructor
	private DosageDocument(BigDecimal value, String unit) {
		this.value = value;
		this.unit = unit;
	}

	private DosageDocument(Builder builder) {
		value = builder.value;
		unit = builder.unit;
	}

	public static DosageDocument toDocument(Dosage dosage) {
		return DosageDocument.newBuilder()
				.withValue(dosage.getValue())
				.withUnit(dosage.getUnit())
				.build();
	}

	public Dosage restore() {
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

		public DosageDocument build() {
			return new DosageDocument(this);
		}
	}
}
