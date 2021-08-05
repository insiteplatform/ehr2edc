package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.concept.ConceptCode;

public final class MedicationConcept {
	private final ConceptCode concept;
	private final String name;

	@PersistenceConstructor
	private MedicationConcept(ConceptCode concept, String name) {
		this.concept = concept;
		this.name = name;
	}

	private MedicationConcept(Builder builder) {
		concept = builder.concept;
		name = builder.name;
	}

	public static Builder newBuilder(MedicationConcept copy) {
		Builder builder = new Builder();
		builder.concept = copy.getConcept();
		builder.name = copy.getName();
		return builder;
	}

	public Builder toBuilder() {
		Builder builder = new Builder();
		builder.concept = getConcept();
		return builder;
	}

	public String getName() {
		return name;
	}

	public ConceptCode getConcept() {
		return concept;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ConceptCode concept;
		private String name;

		private Builder() {
		}

		public Builder withConcept(ConceptCode concept) {
			this.concept = concept;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public MedicationConcept build() {
			return new MedicationConcept(this);
		}
	}
}
