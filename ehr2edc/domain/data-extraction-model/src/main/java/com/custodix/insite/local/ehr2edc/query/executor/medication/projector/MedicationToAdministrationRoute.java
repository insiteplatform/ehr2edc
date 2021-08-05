package com.custodix.insite.local.ehr2edc.query.executor.medication.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;

public final class MedicationToAdministrationRoute implements MedicationProjector<String> {

	private static final String NAME = "Administration route";

	@Override
	public Optional<String> project(Optional<Medication> value, ProjectionContext context) {
		return value.map(Medication::getAdministrationRoute);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
