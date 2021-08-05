package com.custodix.insite.local.ehr2edc.query.executor.common.query;

public class ProjectionStep {

	private final Object input;
	private final String projector;
	private final Object output;

	private ProjectionStep(Builder builder) {
		input = builder.input;
		projector = builder.projector;
		output = builder.output;
	}

	public Object getInput() {
		return input;
	}

	public String getProjector() {
		return projector;
	}

	public Object getOutput() {
		return output;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String projector;
		private Object output;
		private Object input;

		private Builder() {
		}

		public Builder withProjector(String projector) {
			this.projector = projector;
			return this;
		}

		public Builder withInput(Object input) {
			this.input = input;
			return this;
		}

		public Builder withOutput(Object output) {
			this.output = output;
			return this;
		}

		public ProjectionStep build() {
			return new ProjectionStep(this);
		}
	}
}
