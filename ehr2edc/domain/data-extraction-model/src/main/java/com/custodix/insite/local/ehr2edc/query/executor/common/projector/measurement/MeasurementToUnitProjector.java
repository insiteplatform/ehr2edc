package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;

public final class MeasurementToUnitProjector implements Projector<String, Measurement> {

	private static final String NAME = "Unit";

	@Override
	public Optional<String> project(Optional<Measurement> value, ProjectionContext context) {
		return value.map(Measurement::getUnit);
	}

	@Override
	public String getName() {
		return NAME;
	}
}