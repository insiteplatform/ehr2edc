package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication;

import java.math.BigDecimal;

import org.springframework.data.annotation.PersistenceConstructor;

public class Dosage {
	private BigDecimal value;
	private String unit;

	@PersistenceConstructor
	public Dosage(BigDecimal value, String unit) {
		this.value = value;
		this.unit = unit;
	}

	public BigDecimal getValue() {
		return value;
	}

	public String getUnit() {
		return unit;
	}
}
