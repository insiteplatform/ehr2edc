package com.custodix.insite.local.ehr2edc.provenance.model;

import java.util.Optional;

public final class MedicationConcept {
	private final ConceptCode concept;
	private final String name;

	private MedicationConcept(Builder builder) {
		concept = builder.concept;
		name = builder.name;
	}

	public static MedicationConcept from(
			com.custodix.insite.local.ehr2edc.query.executor.medication.domain.MedicationConcept medicationConcept) {
		ConceptCode concept = Optional.ofNullable(medicationConcept.getConcept())
				.map(ConceptCode::from)
				.orElse(null);
		return MedicationConcept.newBuilder()
				.withConcept(concept)
				.withName(medicationConcept.getName())
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public ConceptCode getConcept() {
		return concept;
	}

	public String getName() {
		return name;
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
