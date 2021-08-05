package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import com.custodix.insite.local.ehr2edc.provenance.model.ConceptCode;
import com.custodix.insite.local.ehr2edc.provenance.model.MedicationConcept;

import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Optional;

final class ReviewedMedicationConceptDocument {
	private final ReviewedConceptCodeDocument concept;
	private final String name;

	@PersistenceConstructor
	private ReviewedMedicationConceptDocument(ReviewedConceptCodeDocument concept, String name) {
		this.concept = concept;
		this.name = name;
	}

	private ReviewedMedicationConceptDocument(Builder builder) {
		concept = builder.concept;
		name = builder.name;
	}

	MedicationConcept restore() {
		return MedicationConcept.newBuilder()
				.withConcept(getRestoredConceptCode())
				.withName(name)
				.build();
	}

	private ConceptCode getRestoredConceptCode() {
		return Optional.ofNullable(concept)
				.map(ReviewedConceptCodeDocument::restore)
				.orElse(null);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ReviewedConceptCodeDocument concept;
		private String name;

		private Builder() {
		}

		public Builder withConcept(ReviewedConceptCodeDocument concept) {
			this.concept = concept;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public ReviewedMedicationConceptDocument build() {
			return new ReviewedMedicationConceptDocument(this);
		}
	}
}
