package com.custodix.insite.local.ehr2edc.query.executor.common.query;

import java.util.List;

public class ProjectedDataPoint {

	private final Object result;
	private final List<ProjectionStep> projectionSteps;

	private ProjectedDataPoint(Object result, List<ProjectionStep> projectionSteps) {
		this.result = result;
		this.projectionSteps = projectionSteps;
	}

	public static ProjectedDataPoint of(List<ProjectionStep> projectionSteps) {
		Object result = projectionSteps.isEmpty() ? null : projectionSteps.get(projectionSteps.size() -1).getOutput();
		return new ProjectedDataPoint(result, projectionSteps);
	}

	public Object getResult() {
		return result;
	}

	public List<ProjectionStep> getProjectionSteps() {
		return projectionSteps;
	}
}
