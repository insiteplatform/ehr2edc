package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;

public class VitalSignToMeasurementProjector implements VitalSignProjector<Measurement> {

	private static final String NAME = "Measurement";

	@Override
	public Optional<Measurement> project(Optional<VitalSign> value, ProjectionContext context) {
		return value.map(VitalSign::getMeasurement);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
