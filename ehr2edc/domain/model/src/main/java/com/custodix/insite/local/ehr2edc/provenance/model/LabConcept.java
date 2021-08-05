package com.custodix.insite.local.ehr2edc.provenance.model;

import java.util.Optional;

public final class LabConcept {
	private final ConceptCode concept;
	private final String component;
	private final String method;
	private final FastingStatus fastingStatus;
	private final String specimen;

	private LabConcept(Builder builder) {
		concept = builder.concept;
		component = builder.component;
		method = builder.method;
		fastingStatus = builder.fastingStatus;
		specimen = builder.specimen;
	}

	public static LabConcept from(
			com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept labConcept) {
		ConceptCode concept = Optional.ofNullable(labConcept.getConcept())
				.map(ConceptCode::from)
				.orElse(null);
		FastingStatus fastingStatus = Optional.ofNullable(labConcept.getFastingStatus())
				.map(FastingStatus::from)
				.orElse(null);
		return LabConcept.newBuilder()
				.withConcept(concept)
				.withComponent(labConcept.getComponent())
				.withMethod(labConcept.getMethod())
				.withFastingStatus(fastingStatus)
				.withSpecimen(labConcept.getSpecimen())
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public ConceptCode getConcept() {
		return concept;
	}

	public String getComponent() {
		return component;
	}

	public String getMethod() {
		return method;
	}

	public FastingStatus getFastingStatus() {
		return fastingStatus;
	}

	public String getSpecimen() {
		return specimen;
	}

    public static final class Builder {
		private ConceptCode concept;
		private String component;
		private String method;
		private FastingStatus fastingStatus;
		private String specimen;

		private Builder() {
		}

		public Builder withConcept(ConceptCode concept) {
			this.concept = concept;
			return this;
		}

		public Builder withComponent(String component) {
			this.component = component;
			return this;
		}

		public Builder withMethod(String method) {
			this.method = method;
			return this;
		}

		public Builder withFastingStatus(FastingStatus fastingStatus) {
			this.fastingStatus = fastingStatus;
			return this;
		}

		public Builder withSpecimen(String specimen) {
			this.specimen = specimen;
			return this;
		}

		public LabConcept build() {
			return new LabConcept(this);
		}
	}
}