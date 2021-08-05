package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;

public final class LabValueToMeasurementProjector implements LabValueProjector<Measurement> {

	private static final String NAME = "Measurement";

	@Override
	public Optional<Measurement> project(Optional<LabValue> value, ProjectionContext context) {
		return value.map(LabValue::getQuantitativeResult);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
