package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;

public final class LabConceptField {
	private final ConceptCode concept;
	private final String component;
	private final String method;
	private final String fastingStatus;
	private final String specimen;

	@PersistenceConstructor
	private LabConceptField(ConceptCode concept, String component, String method, String fastingStatus,
			String specimen) {
		this.concept = concept;
		this.component = component;
		this.method = method;
		this.fastingStatus = fastingStatus;
		this.specimen = specimen;
	}

	private LabConceptField(Builder builder) {
		concept = builder.concept;
		component = builder.component;
		method = builder.method;
		fastingStatus = builder.fastingStatus;
		specimen = builder.specimen;
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

	public String getFastingStatus() {
		return fastingStatus;
	}

	public String getSpecimen() {
		return specimen;
	}

	public static final class Builder {
		private ConceptCode concept;
		private String component;
		private String method;
		private String fastingStatus;
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

		public Builder withFastingStatus(String fastingStatus) {
			this.fastingStatus = fastingStatus;
			return this;
		}

		public Builder withSpecimen(String specimen) {
			this.specimen = specimen;
			return this;
		}

		public LabConceptField build() {
			return new LabConceptField(this);
		}
	}
}