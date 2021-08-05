package com.custodix.insite.local.ehr2edc.ehr.fhir.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface FhirSubjectRegistration {

	@Allow(ANYONE)
	void register(@Valid  @NotNull FhirSubjectRegistration.Request request);

	final class Request {
		@NotNull
		@Valid
		private final PatientCDWReference patientId;
		@NotNull
		@Valid
		private final StudyId studyId;
		@NotNull
		@Valid
		private final SubjectId subjectId;

		private Request(Builder builder) {
			patientId = builder.patientId;
			studyId = builder.studyId;
			subjectId = builder.subjectId;
		}

		public PatientCDWReference getPatientReference() {
			return patientId;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Request copy) {
			Builder builder = new Builder();
			builder.patientId = copy.patientId;
			builder.studyId = copy.studyId;
			builder.subjectId = copy.subjectId;
			return builder;
		}

		public static final class Builder {
			private PatientCDWReference patientId;
			private StudyId studyId;
			private SubjectId subjectId;

			private Builder() {
			}

			public Builder withPatientId(PatientCDWReference patientId) {
				this.patientId = patientId;
				return this;
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Builder withSubjectId(SubjectId subjectId) {
				this.subjectId = subjectId;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
