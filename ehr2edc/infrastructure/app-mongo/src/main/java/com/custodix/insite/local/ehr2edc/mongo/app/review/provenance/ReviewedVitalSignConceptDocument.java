package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.provenance.model.ConceptCode;
import com.custodix.insite.local.ehr2edc.provenance.model.VitalSignConcept;

final class ReviewedVitalSignConceptDocument {
	private final ReviewedConceptCodeDocument concept;
	private final String component;
	private final String location;
	private final String laterality;
	private final String position;

	@PersistenceConstructor
	private ReviewedVitalSignConceptDocument(ReviewedConceptCodeDocument concept, String component, String location,
                                             String laterality, String position) {
		this.concept = concept;
		this.component = component;
		this.location = location;
		this.laterality = laterality;
		this.position = position;
	}

	private ReviewedVitalSignConceptDocument(Builder builder) {
		concept = builder.concept;
		component = builder.component;
		location = builder.location;
		laterality = builder.laterality;
		position = builder.position;
	}

	VitalSignConcept restore() {
		ConceptCode conceptCode = Optional.ofNullable(concept)
				.map(ReviewedConceptCodeDocument::restore)
				.orElse(null);
		return VitalSignConcept.newBuilder()
				.withConcept(conceptCode)
				.withComponent(component)
				.withLocation(location)
				.withLaterality(laterality)
				.withPosition(position)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ReviewedConceptCodeDocument concept;
		private String component;
		private String location;
		private String laterality;
		private String position;

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

		public Builder withLocation(String location) {
			this.location = location;
			return this;
		}

		public Builder withLaterality(String laterality) {
			this.laterality = laterality;
			return this;
		}

		public Builder withPosition(String position) {
			this.position = position;
			return this;
		}

		public ReviewedVitalSignConceptDocument build() {
			return new ReviewedVitalSignConceptDocument(this);
		}
	}
}
