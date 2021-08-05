package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;

@Deprecated
public final class LastLabValueProjector implements LabValueProjector<LabValue> {

	private static final String NAME = "Last labValue (DEPRECATED)";

	@Override
	public Optional<LabValue> project(Optional<LabValue> labValue, ProjectionContext context) {
		return labValue;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
