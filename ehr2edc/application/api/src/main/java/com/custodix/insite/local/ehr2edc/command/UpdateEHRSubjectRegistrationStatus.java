package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRRegistrationStatus;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface UpdateEHRSubjectRegistrationStatus {

	@Allow(ANYONE)
	void update(@Valid @NotNull Request request);

	final class Request {
		@Valid @NotNull
		private final SubjectId subjectId;
		@Valid @NotNull
		private final PatientCDWReference patientCDWReference;
		@Valid @NotNull
		private final EHRRegistrationStatus status;

		private Request(Builder builder) {
			subjectId = builder.subjectId;
			patientCDWReference = builder.patientCDWReference;
			status = builder.status;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public PatientCDWReference getPatientCDWReference() {
			return patientCDWReference;
		}

		public EHRRegistrationStatus getStatus() {
			return status;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private SubjectId subjectId;
			private PatientCDWReference patientCDWReference;
			private EHRRegistrationStatus status;

			private Builder() {
			}

			public Builder withSubjectId(SubjectId val) {
				subjectId = val;
				return this;
			}

			public Builder withPatientCDWReference(PatientCDWReference val) {
				patientCDWReference = val;
				return this;
			}

			public Builder withStatus(EHRRegistrationStatus val) {
				status = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}


}
