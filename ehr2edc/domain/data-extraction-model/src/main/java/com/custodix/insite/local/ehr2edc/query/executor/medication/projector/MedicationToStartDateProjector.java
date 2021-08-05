package com.custodix.insite.local.ehr2edc.query.executor.medication.projector;

import java.time.LocalDateTime;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;

public final class MedicationToStartDateProjector implements MedicationProjector<LocalDateTime> {

	private static final String NAME = "Start date";

	@Override
	public Optional<LocalDateTime> project(Optional<Medication> value, ProjectionContext context) {
		return value.map(Medication::getStartDate);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
