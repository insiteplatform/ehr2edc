package com.custodix.insite.local.ehr2edc.query.executor.medication.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.MedicationConcept;

public final class MedicationToNameProjector implements MedicationProjector<String> {

	private static final String NAME = "Name";

	@Override
	public Optional<String> project(Optional<Medication> value, ProjectionContext context) {
		return value.map(Medication::getMedicationConcept)
				.map(MedicationConcept::getName);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
