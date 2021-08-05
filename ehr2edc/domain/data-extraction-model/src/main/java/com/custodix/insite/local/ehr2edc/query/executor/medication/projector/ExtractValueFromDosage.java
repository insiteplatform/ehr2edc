package com.custodix.insite.local.ehr2edc.query.executor.medication.projector;

import java.math.BigDecimal;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage;

public final class ExtractValueFromDosage implements DosageProjector<BigDecimal> {

	private static final String NAME = "Dosage value";

	@Override
	public Optional<BigDecimal> project(Optional<Dosage> value, ProjectionContext context) {
		return value.map(Dosage::getValue);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
