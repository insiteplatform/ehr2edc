package com.custodix.insite.mongodb.export.patient.application.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public interface ActivateSubject {
	void activate(@Valid Request request);

	final class Request {
		@Valid
		@NotNull
		private final PatientIdentifier patientIdentifier;

		private Request(Builder builder) {
			patientIdentifier = builder.patientIdentifier;
		}

		public PatientIdentifier getPatientIdentifier() {
			return patientIdentifier;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private PatientIdentifier patientIdentifier;

			private Builder() {
			}

			public Builder withPatientIdentifier(PatientIdentifier val) {
				patientIdentifier = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}