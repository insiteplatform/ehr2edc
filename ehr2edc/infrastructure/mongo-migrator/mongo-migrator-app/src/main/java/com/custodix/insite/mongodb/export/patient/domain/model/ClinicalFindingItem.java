package com.custodix.insite.mongodb.export.patient.domain.model;

import java.util.ArrayList;
import java.util.List;

public class ClinicalFindingItem {
	private final ClinicalFactRecord clinicalFactRecord;
	private final List<String> modifierCodes;

	public ClinicalFindingItem(ClinicalFactRecord clinicalFactRecord) {
		this.clinicalFactRecord = clinicalFactRecord;
		this.modifierCodes = new ArrayList<>();
	}

	public void addModifierCode(String modifierCode) {
		modifierCodes.add(modifierCode);
	}

	public ClinicalFactRecord getClinicalFactRecord() {
		return clinicalFactRecord;
	}

	public List<String> getModifierCodes() {
		return modifierCodes;
	}
}
