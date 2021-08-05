package com.custodix.insite.local.ehr2edc.mongo.app.document;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionStep;

public final class ProjectionStepDocument {

	private final Object input;
	private final String projector;
	private final Object output;

	@PersistenceConstructor
	private ProjectionStepDocument(Object input, String projector, Object output) {
		this.input = input;
		this.projector = projector;
		this.output = output;
	}

	public static ProjectionStepDocument from(ProjectionStep step) {
		return new ProjectionStepDocument(
				step.getInput(),
				step.getProjector(),
				step.getOutput()
		);
	}

	public ProjectionStep restore() {
		return ProjectionStep.newBuilder()
				.withInput(input)
				.withProjector(projector)
				.withOutput(output)
				.build();
	}
}
