package com.custodix.insite.local.ehr2edc.query.executor.medication.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;

public final class ExtractDosageFromMedication implements MedicationProjector<Dosage> {

	private static final String NAME = "Dosage";

	@Override
	public Optional<Dosage> project(Optional<Medication> value, ProjectionContext context) {
		return value.map(Medication::getDosage);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
