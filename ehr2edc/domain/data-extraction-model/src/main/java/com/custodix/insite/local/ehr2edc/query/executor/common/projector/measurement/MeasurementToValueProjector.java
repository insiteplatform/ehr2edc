package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement;

import java.math.BigDecimal;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;

public final class MeasurementToValueProjector implements Projector<BigDecimal, Measurement> {

	private static final String NAME = "Value";

	@Override
	public Optional<BigDecimal> project(Optional<Measurement> value, ProjectionContext context) {
		return value.map(Measurement::getValue);
	}

	@Override
	public String getName() {
		return NAME;
	}
}