package com.custodix.insite.mongodb.export.patient.infrastructure.embedded;

import com.custodix.insite.mongodb.export.patient.domain.model.labvalue.LaboratoryConceptInfo;

class LaboratoryConceptInfoMapping {
	final String code;
	final LaboratoryConceptInfo laboratoryConceptInfo;

	LaboratoryConceptInfoMapping(String code, LaboratoryConceptInfo laboratoryConceptInfo) {
		this.code = code;
		this.laboratoryConceptInfo = laboratoryConceptInfo;
	}

	String getCode() {
		return code;
	}

	LaboratoryConceptInfo getLaboratoryConceptInfo() {
		return laboratoryConceptInfo;
	}
}