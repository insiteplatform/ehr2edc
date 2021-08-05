package com.custodix.insite.mongodb.export.patient.application.api;

import java.util.List;

import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public interface GetActiveSubjects {

	Response getAll();

	final class Response {
		List<PatientIdentifier> patientIdentifiers;

		private Response(Builder builder) {
			patientIdentifiers = builder.patientIdentifiers;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public List<PatientIdentifier> getPatientIdentifiers() {
			return patientIdentifiers;
		}

		public static final class Builder {
			private List<PatientIdentifier> patientIdentifiers;

			private Builder() {
			}

			public Builder withPatientIdentifiers(final List<PatientIdentifier> val) {
				patientIdentifiers = val;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}
}
