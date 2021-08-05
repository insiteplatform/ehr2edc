package com.custodix.insite.local.ehr2edc.ehr.mongo.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface StartSubjectRegistration {

	@Allow(ANYONE)
	void start(@Valid @NotNull Request request);

	final class Request {
		@NotNull
		@Valid
		private PatientCDWReference patientCDWReference;
		@NotNull
		@Valid
		private SubjectId subjectId;

		private Request(Builder builder) {
			patientCDWReference = builder.patientCDWReference;
			subjectId = builder.subjectId;
		}

		public PatientCDWReference getPatientCDWReference() {
			return patientCDWReference;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private PatientCDWReference patientCDWReference;
			private SubjectId subjectId;

			private Builder() {
			}

			public Builder withPatientCDWReference(PatientCDWReference val) {
				patientCDWReference = val;
				return this;
			}

			public Builder withSubjectId(SubjectId val) {
				subjectId = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
