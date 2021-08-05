package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class BooleanToIntegerProjector implements Projector<ProjectedValue<Integer>, ProjectedValue<Boolean> > {

	private static final String NAME = "Boolean to integer";

	@Override
	public Optional<ProjectedValue<Integer>> project(Optional<ProjectedValue<Boolean>> value, ProjectionContext context) {
		return value.map(v -> new ProjectedValue<>(booleanToInteger(v.getValue()), v.getUnit(), v.getCode()));
	}

	@Override
	public String getName() {
		return NAME;
	}

	private Integer booleanToInteger(Boolean booleanValue) {
		return booleanValue ? 1 : 0;
	}
}
