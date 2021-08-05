package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.provenance.model.FastingStatus;
import com.custodix.insite.local.ehr2edc.provenance.model.LabConcept;
import com.custodix.insite.local.ehr2edc.provenance.model.ConceptCode;

final class LabConceptDocument {
	private final ConceptCodeDocument concept;
	private final String component;
	private final String method;
	private final FastingStatus fastingStatus;
	private final String specimen;

	@PersistenceConstructor
	private LabConceptDocument(ConceptCodeDocument concept, String component, String method,
			FastingStatus fastingStatus, String specimen) {
		this.concept = concept;
		this.component = component;
		this.method = method;
		this.fastingStatus = fastingStatus;
		this.specimen = specimen;
	}

	private LabConceptDocument(Builder builder) {
		concept = builder.concept;
		component = builder.component;
		method = builder.method;
		fastingStatus = builder.fastingStatus;
		specimen = builder.specimen;
	}

	public static LabConceptDocument toDocument(LabConcept labConcept) {
		ConceptCodeDocument concept = Optional.ofNullable(labConcept.getConcept())
				.map(ConceptCodeDocument::toDocument)
				.orElse(null);
		return LabConceptDocument.newBuilder()
				.withConcept(concept)
				.withComponent(labConcept.getComponent())
				.withMethod(labConcept.getMethod())
				.withFastingStatus(labConcept.getFastingStatus())
				.withSpecimen(labConcept.getSpecimen())
				.build();
	}

	public LabConcept restore() {
		ConceptCode conceptCode = Optional.ofNullable(concept)
				.map(ConceptCodeDocument::restore)
				.orElse(null);
		return LabConcept.newBuilder()
				.withConcept(conceptCode)
				.withComponent(component)
				.withMethod(method)
				.withFastingStatus(fastingStatus)
				.withSpecimen(specimen)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ConceptCodeDocument concept;
		private String component;
		private String method;
		private FastingStatus fastingStatus;
		private String specimen;

		private Builder() {
		}

		public Builder withConcept(ConceptCodeDocument concept) {
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

		public LabConceptDocument build() {
			return new LabConceptDocument(this);
		}
	}
}
