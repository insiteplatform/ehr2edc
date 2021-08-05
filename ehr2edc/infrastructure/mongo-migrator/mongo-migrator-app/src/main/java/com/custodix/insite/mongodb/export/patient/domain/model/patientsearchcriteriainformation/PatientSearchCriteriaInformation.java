package com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation;

import java.time.LocalDate;

import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PatientSearchCriteriaInformation.Builder.class)
public class PatientSearchCriteriaInformation {
	private final PatientIdentifier patientIdentifier;
	private final LocalDate birthDate;

	private PatientSearchCriteriaInformation(Builder builder) {
		patientIdentifier = builder.patientIdentifier;
		birthDate = builder.birthDate;
	}

	public PatientIdentifier getPatientIdentifier() {
		return patientIdentifier;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private PatientIdentifier patientIdentifier;
		private LocalDate birthDate;

		private Builder() {
		}

		public Builder withPatientIdentifier(PatientIdentifier val) {
			patientIdentifier = val;
			return this;
		}

		public Builder withBirthDate(LocalDate val) {
			birthDate = val;
			return this;
		}

		public PatientSearchCriteriaInformation build() {
			return new PatientSearchCriteriaInformation(this);
		}
	}
}
