package com.custodix.insite.local.ehr2edc.vocabulary;

import java.time.LocalDate;
import java.util.Objects;

public class PatientSearchCriteria {

	private final PatientCDWReference patientCDWReference;
	private final String lastName;
	private final String firstName;
	private final LocalDate birthDate;

	private PatientSearchCriteria(Builder builder) {
		patientCDWReference = builder.patientCDWReference;
		lastName = builder.lastName;
		firstName = builder.firstName;
		birthDate = builder.birthDate;
	}

	public PatientCDWReference getPatientCDWReference() {
		return patientCDWReference;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private PatientCDWReference patientCDWReference;
		private String lastName;
		private String firstName;
		private LocalDate birthDate;

		private Builder() {
		}

		public Builder withPatientCDWReference(PatientCDWReference val) {
			patientCDWReference = val;
			return this;
		}

		public Builder withLastName(String val) {
			lastName = val;
			return this;
		}

		public Builder withFirstName(String val) {
			firstName = val;
			return this;
		}

		public Builder withBirthDate(LocalDate val) {
			birthDate = val;
			return this;
		}

		public PatientSearchCriteria build() {
			return new PatientSearchCriteria(this);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final PatientSearchCriteria that = (PatientSearchCriteria) o;
		return Objects.equals(patientCDWReference, that.patientCDWReference) &&
				Objects.equals(lastName, that.lastName) &&
				Objects.equals(firstName, that.firstName) &&
				Objects.equals(birthDate, that.birthDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(patientCDWReference, lastName, firstName, birthDate);
	}
}
