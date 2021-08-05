package com.custodix.insite.local.ehr2edc.query.mongo.medication.model;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;

public final class MedicationConceptField {
	private final ConceptCode concept;
	private final String name;

	@PersistenceConstructor
	public MedicationConceptField(ConceptCode concept, String name) {
		this.concept = concept;
		this.name = name;
	}

	public ConceptCode getConcept() {
		return concept;
	}

	private MedicationConceptField(Builder builder) {
		concept = builder.concept;
		name = builder.name;
	}

	public String getName() {
		return name;
	}

	public Builder toBuilder() {
		Builder builder = new Builder();
		builder.concept = concept;
		return builder;
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

		public MedicationConceptField build() {
			return new MedicationConceptField(this);
		}
	}
}
