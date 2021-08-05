package com.custodix.insite.local.ehr2edc.ehr.mongo.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface SubjectMigration {

	@Allow(ANYONE)
	void migrate(@Valid  @NotNull Request request);

	final class Request {
		@NotNull
		@Valid
		private final PatientCDWReference patientCDWReference;
		@NotNull
		@Valid
		private final StudyId studyId;
		@NotNull
		@Valid
		private final SubjectId subjectId;

		private Request(Builder builder) {
			patientCDWReference = builder.patientCDWReference;
			studyId = builder.studyId;
			subjectId = builder.subjectId;
		}

		public PatientCDWReference getPatientCDWReference() {
			return patientCDWReference;
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

		public static final class Builder {
			private PatientCDWReference patientCDWReference;
			private StudyId studyId;
			private SubjectId subjectId;

			private Builder() {
			}

			public Builder withPatientCDWReference(PatientCDWReference patientId) {
				this.patientCDWReference = patientId;
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
