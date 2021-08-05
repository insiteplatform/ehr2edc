package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class SetProjectedValueUnit implements Projector<ProjectedValue, ProjectedValue> {
	private static final String NAME = "Set unit";
	private final String unit;

	public SetProjectedValueUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public Optional<ProjectedValue> project(Optional<ProjectedValue> value, ProjectionContext context) {
		return value.map(v -> new ProjectedValue<>(v.getValue(), unit, v.getCode()));
	}

	@Override
	public String getName() {
		return NAME;
	}
}
