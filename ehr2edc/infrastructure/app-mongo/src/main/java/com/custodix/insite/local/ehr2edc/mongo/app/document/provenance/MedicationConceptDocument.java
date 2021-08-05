package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.provenance.model.ConceptCode;
import com.custodix.insite.local.ehr2edc.provenance.model.MedicationConcept;

final class MedicationConceptDocument {
	private final ConceptCodeDocument concept;
	private final String name;

	@PersistenceConstructor
	private MedicationConceptDocument(ConceptCodeDocument concept, String name) {
		this.concept = concept;
		this.name = name;
	}

	private MedicationConceptDocument(Builder builder) {
		concept = builder.concept;
		name = builder.name;
	}

	public static MedicationConceptDocument toDocument(MedicationConcept medicationConcept) {
		ConceptCodeDocument concept = Optional.ofNullable(medicationConcept.getConcept())
				.map(ConceptCodeDocument::toDocument)
				.orElse(null);
		return MedicationConceptDocument.newBuilder()
				.withConcept(concept)
				.withName(medicationConcept.getName())
				.build();
	}

	public MedicationConcept restore() {
		ConceptCode conceptCode = Optional.ofNullable(concept)
				.map(ConceptCodeDocument::restore)
				.orElse(null);
		return MedicationConcept.newBuilder()
				.withConcept(conceptCode)
				.withName(name)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ConceptCodeDocument concept;
		private String name;

		private Builder() {
		}

		public Builder withConcept(ConceptCodeDocument concept) {
			this.concept = concept;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public MedicationConceptDocument build() {
			return new MedicationConceptDocument(this);
		}
	}
}
