package com.custodix.insite.local.ehr2edc.query.executor.medication.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;

@Deprecated
public final class LastMedicationProjector implements MedicationProjector<Medication>{

	private static final String NAME = "Last medication (DEPRECATED)";

	@Override
	public Optional<Medication> project(Optional<Medication> medication, ProjectionContext context) {
		return medication;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
