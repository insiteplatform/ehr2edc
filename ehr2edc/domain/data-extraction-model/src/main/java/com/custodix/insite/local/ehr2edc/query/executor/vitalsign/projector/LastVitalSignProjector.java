package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;

@Deprecated
public final class LastVitalSignProjector implements VitalSignProjector<VitalSign>{

	private static final String NAME = "Last vitalSign (DEPRECATED)";

	@Override
	public Optional<VitalSign> project(Optional<VitalSign> vitalSign, ProjectionContext context) {
		return vitalSign;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
