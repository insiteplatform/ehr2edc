package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.vitalstatus;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class VitalStatusToDeceasedBooleanProjector implements Projector<Boolean, VitalStatus>{

	private static final String NAME = "Vital status to boolean deceased";

	@Override
	public Optional<Boolean> project(Optional<VitalStatus> value, ProjectionContext context) {
		return value.flatMap(VitalStatus::isDeceased);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
