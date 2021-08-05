package com.custodix.insite.local.ehr2edc.jackson.mixin.projector;

import com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ProjectMedicationMixin {
	@JsonProperty
	private MedicationField field;

	@JsonCreator
	private ProjectMedicationMixin(@JsonProperty("field") MedicationField field) {

	}
}
