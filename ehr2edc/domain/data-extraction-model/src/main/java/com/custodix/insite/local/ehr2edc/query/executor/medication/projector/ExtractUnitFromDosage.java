package com.custodix.insite.local.ehr2edc.query.executor.medication.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage;

public final class ExtractUnitFromDosage implements DosageProjector<String> {

	private static final String NAME = "Dosage unit";

	@Override
	public Optional<String> project(Optional<Dosage> value, ProjectionContext context) {
		return value.map(Dosage::getUnit);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
