package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector;

import java.time.LocalDateTime;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;

public class VitalSignToDateProjector implements VitalSignProjector<LocalDateTime> {

	private static final String NAME = "Effective date";

	@Override
	public Optional<LocalDateTime> project(Optional<VitalSign> value, ProjectionContext context) {
		return value.map(VitalSign::getEffectiveDateTime);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
