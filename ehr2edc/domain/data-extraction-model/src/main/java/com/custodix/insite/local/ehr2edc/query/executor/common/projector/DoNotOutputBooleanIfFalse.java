package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class DoNotOutputBooleanIfFalse implements Projector<ProjectedValue<Boolean>, ProjectedValue<Boolean> > {

	private static final String NAME = "No output if false";

	@Override
	public Optional<ProjectedValue<Boolean>> project(Optional<ProjectedValue<Boolean>> value, ProjectionContext context) {
		return value.filter(ProjectedValue::getValue);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
