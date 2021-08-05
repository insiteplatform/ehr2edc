package com.custodix.insite.local.ehr2edc.query.fhir.medication;

import java.util.Optional;

import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;

public final class FhirDstu2MedicationResources<M> {
	private final M medicationResource;
	private final Medication medication;

	private FhirDstu2MedicationResources(Builder<M> builder) {
		medicationResource = builder.medicationResource;
		medication = builder.medication;
	}

	public M getMedicationResource() {
		return medicationResource;
	}

	public Optional<Medication> getMedication() {
		return Optional.ofNullable(medication);
	}

	public static <M> Builder<M> newBuilder() {
		return new Builder<>();
	}

	public static Builder<MedicationAdministration> administrationBuilder() {
		return newBuilder();
	}

	public static Builder<MedicationDispense> dispenseBuilder() {
		return newBuilder();
	}

	public static Builder<MedicationStatement> statementBuilder() {
		return newBuilder();
	}

	public static final class Builder<M> {
		private M medicationResource;
		private Medication medication;

		private Builder() {
		}

		public Builder<M> withMedicationResource(M val) {
			medicationResource = val;
			return this;
		}

		public Builder<M> withMedication(Medication val) {
			medication = val;
			return this;
		}

		public FhirDstu2MedicationResources<M> build() {
			return new FhirDstu2MedicationResources(this);
		}

	}
}
