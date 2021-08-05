package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.provenance.model.ConceptCode;
import com.custodix.insite.local.ehr2edc.provenance.model.FastingStatus;
import com.custodix.insite.local.ehr2edc.provenance.model.LabConcept;

final class ReviewedLabConceptDocument {
	private final ReviewedConceptCodeDocument concept;
	private final String component;
	private final String method;
	private final FastingStatus fastingStatus;
	private final String specimen;

	@PersistenceConstructor
	private ReviewedLabConceptDocument(ReviewedConceptCodeDocument concept, String component, String method,
                                       FastingStatus fastingStatus, String specimen) {
		this.concept = concept;
		this.component = component;
		this.method = method;
		this.fastingStatus = fastingStatus;
		this.specimen = specimen;
	}

	private ReviewedLabConceptDocument(Builder builder) {
		concept = builder.concept;
		component = builder.component;
		method = builder.method;
		fastingStatus = builder.fastingStatus;
		specimen = builder.specimen;
	}

	LabConcept restore() {
		return LabConcept.newBuilder()
				.withConcept(getRestoredConcept())
				.withComponent(component)
				.withMethod(method)
				.withFastingStatus(fastingStatus)
				.withSpecimen(specimen)
				.build();
	}

	private ConceptCode getRestoredConcept() {
		return Optional.ofNullable(concept)
				.map(ReviewedConceptCodeDocument::restore)
				.orElse(null);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ReviewedConceptCodeDocument concept;
		private String component;
		private String method;
		private FastingStatus fastingStatus;
		private String specimen;

		private Builder() {
		}

		public Builder withConcept(ReviewedConceptCodeDocument concept) {
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

		public ReviewedLabConceptDocument build() {
			return new ReviewedLabConceptDocument(this);
		}
	}
}
