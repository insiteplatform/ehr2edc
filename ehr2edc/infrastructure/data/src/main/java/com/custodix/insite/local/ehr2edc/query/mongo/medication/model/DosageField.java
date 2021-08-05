package com.custodix.insite.local.ehr2edc.query.mongo.medication.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.PersistenceConstructor;

public class DosageField {
	private BigDecimal value;
	private String unit;

	@PersistenceConstructor
	public DosageField(BigDecimal value, String unit) {
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
