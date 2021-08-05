package com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;

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

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String method;
		private FastingStatus fastingStatus;
		private String specimen;
		private ConceptCode concept;
		private String component;

		private Builder() {
		}

		public Builder withMethod(String val) {
			method = val;
			return this;
		}

		public Builder withFastingStatus(FastingStatus val) {
			fastingStatus = val;
			return this;
		}

		public Builder withSpecimen(String val) {
			specimen = val;
			return this;
		}

		public LabConcept build() {
			return new LabConcept(this);
		}

		public Builder withConcept(ConceptCode val) {
			concept = val;
			return this;
		}

		public Builder withComponent(String val) {
			component = val;
			return this;
		}
	}
}