package com.custodix.insite.local.ehr2edc.query.executor.common.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ProjectionChain {

	private final List<Projector> projectors;

	private ProjectionChain(List<Projector> projectors) {
		this.projectors = projectors;
	}

	public static ProjectionChain of(List<Projector> projectors) {
		return new ProjectionChain(projectors);
	}

	public ProjectedDataPoint project(DataPoint datapoint, ProjectionContext context) {
		Supplier<List<ProjectionStep>> listProvider = ArrayList::new;
		BiConsumer<List<ProjectionStep>, Projector> projectorExecutionAccumulator = accumulateProjectorExecution(context, datapoint);
		BiConsumer<List<ProjectionStep>, List<ProjectionStep>> listCombiner = List::addAll;

		List<ProjectionStep> projectionSteps = projectors.stream()
				.collect(listProvider, projectorExecutionAccumulator, listCombiner);
		return ProjectedDataPoint.of(projectionSteps);
	}

	private BiConsumer<List<ProjectionStep>, Projector> accumulateProjectorExecution(ProjectionContext context,
			DataPoint datapoint) {
		return (projectionSteps, projector) -> {
			Object previous = getProjectorInput(datapoint, projectionSteps);
			ProjectionStep projectionStep = executeStep(projector, previous, context);
			projectionSteps.add(projectionStep);
		};
	}

	private Object getProjectorInput(DataPoint datapoint, List<ProjectionStep> projectionSteps) {
		if (projectionSteps.isEmpty()) {
			return datapoint;
		}
		return projectionSteps.get(projectionSteps.size() - 1).getOutput();
	}

	private ProjectionStep executeStep(Projector projector, Object previous, ProjectionContext context) {
		Object result = project(projector, context, Optional.ofNullable(previous)).orElse(null);
		return ProjectionStep.newBuilder()
				.withInput(previous)
				.withProjector(projector.getName())
				.withOutput(result)
				.build();
	}

	private <T, P> Optional<T> project(Projector<T, P> projector, ProjectionContext context, Optional<P> input) {
		ProjectionLogger.logStart(input, projector);
		Optional<T> result = projector.project(input, context);
		ProjectionLogger.logAfterValue(result);
		return result;
	}
}
