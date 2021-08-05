package com.custodix.insite.mongodb.export.patient.domain.model.medication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MedicationItem {
	private MedicationRecord medicationRecord;
	private final List<String> modifierCodes;
	private Dosage dosage;

	public MedicationItem(MedicationRecord clinicalFactRecord) {
		this.medicationRecord = clinicalFactRecord;
		this.modifierCodes = new ArrayList<>();
	}

	public void addModifierCode(String modifierCode) {
		modifierCodes.add(modifierCode);
	}

	public MedicationRecord getMedicationRecord() {
		return medicationRecord;
	}

	public void addDosage(String unit, BigDecimal value) {
		this.dosage = Dosage.newBuilder()
				.withUnit(unit)
				.withValue(value)
				.build();
	}

	public Dosage getDosage() {
		return dosage;
	}

	public List<String> getModifierCodes() {
		return modifierCodes;
	}
}
