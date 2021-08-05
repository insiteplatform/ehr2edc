package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class AgeToNumericalProjector implements Projector<Long, Age> {

	private static final String NAME = "Numerical value";

	@Override
	public Optional<Long> project(Optional<Age> value, ProjectionContext context) {
		return value.map(Age::numerical);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
