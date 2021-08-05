package com.custodix.insite.mongodb.export.patient.application.api;

import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public interface ExportPatient {
	void export(Request request);

	@JsonDeserialize(builder = Request.Builder.class)
	final class Request {
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