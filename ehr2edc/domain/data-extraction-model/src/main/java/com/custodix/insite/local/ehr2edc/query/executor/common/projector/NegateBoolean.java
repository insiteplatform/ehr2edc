package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class NegateBoolean implements Projector<ProjectedValue<Boolean>, ProjectedValue<Boolean> > {

	private static final String NAME = "Negate";

	@Override
	public Optional<ProjectedValue<Boolean>> project(Optional<ProjectedValue<Boolean>> projectedValue,
			ProjectionContext context) {
		return projectedValue.map(value -> new ProjectedValue<>(!value.getValue(), value.getUnit(), value.getCode()));
	}

	@Override
	public String getName() {
		return NAME;
	}
}
