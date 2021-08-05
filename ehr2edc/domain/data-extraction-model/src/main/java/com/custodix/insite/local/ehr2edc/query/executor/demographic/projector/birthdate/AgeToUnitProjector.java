package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class AgeToUnitProjector implements Projector<String, Age> {

	private static final String NAME = "Units";

	@Override
	public Optional<String> project(Optional<Age> value, ProjectionContext context) {
		return value.map(Age::unit);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
