package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class ComposeProjectedValueProjection implements Projector<ProjectedValue, ProjectedValue> {
	private static final String NAME = "Compose ProjectedValue";
	private final Projector projector;

	public ComposeProjectedValueProjection(Projector projector) {
		this.projector = projector;
	}

	@Override
	public Optional<ProjectedValue> project(Optional<ProjectedValue> value, ProjectionContext context) {
		return value.flatMap(v -> doProject(v, context));
	}

	@Override
	public String getName() {
		return NAME;
	}

	private Optional<ProjectedValue> doProject(ProjectedValue projectedValue, ProjectionContext projectionContext) {
		Object originalValue = projectedValue.getValue();
		Optional newValue = projector.project(Optional.ofNullable(originalValue), projectionContext);
		return newValue.map(v -> new ProjectedValue<>(v, projectedValue.getUnit(), projectedValue.getCode()));
	}
}
