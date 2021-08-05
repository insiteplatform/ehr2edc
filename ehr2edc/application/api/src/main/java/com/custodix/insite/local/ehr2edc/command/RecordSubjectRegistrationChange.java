package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ANYONE;

import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.DeregisterReason;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface RecordSubjectRegistrationChange {

	@Allow(ANYONE)
	void register(@Valid @NotNull RegistrationRequest request);

	@Allow(ANYONE)
	void deregister(@Valid @NotNull DeregistrationRequest request);

	final class RegistrationRequest {
		@NotNull
		@Valid
		private final PatientCDWReference patientId;
		@NotNull
		@Valid
		private final StudyId studyId;
		@NotNull
		@Valid
		private final SubjectId subjectId;
		@NotNull
		private final LocalDate date;

		private RegistrationRequest(Builder builder) {
			patientId = builder.patientId;
			studyId = builder.studyId;
			subjectId = builder.subjectId;
			date = builder.date;
		}

		public PatientCDWReference getPatientId() {
			return patientId;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public LocalDate getDate() {
			return date;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(RegistrationRequest copy) {
			Builder builder = new Builder();
			builder.patientId = copy.patientId;
			builder.studyId = copy.studyId;
			builder.subjectId = copy.subjectId;
			builder.date = copy.date;
			return builder;
		}

		public static final class Builder {
			private PatientCDWReference patientId;
			private StudyId studyId;
			private SubjectId subjectId;
			private LocalDate date;

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

			public Builder withDate(LocalDate date) {
				this.date = date;
				return this;
			}

			public RegistrationRequest build() {
				return new RegistrationRequest(this);
			}
		}
	}

	final class DeregistrationRequest {
		@NotNull
		@Valid
		private final PatientCDWReference patientId;
		@NotNull
		@Valid
		private final StudyId studyId;
		@NotNull
		@Valid
		private final SubjectId subjectId;
		@NotNull
		private final LocalDate date;
		@NotNull
		private final DeregisterReason reason;

		private DeregistrationRequest(Builder builder) {
			patientId = builder.patientId;
			studyId = builder.studyId;
			subjectId = builder.subjectId;
			date = builder.date;
			reason = builder.reason;
		}

		public PatientCDWReference getPatientId() {
			return patientId;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public LocalDate getDate() {
			return date;
		}

		public DeregisterReason getReason() {
			return reason;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(DeregistrationRequest copy) {
			Builder builder = new Builder();
			builder.patientId = copy.patientId;
			builder.studyId = copy.studyId;
			builder.subjectId = copy.subjectId;
			builder.date = copy.date;
			builder.reason = copy.reason;
			return builder;
		}

		public static final class Builder {
			private PatientCDWReference patientId;
			private StudyId studyId;
			private SubjectId subjectId;
			private LocalDate date;
			private DeregisterReason reason;

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

			public Builder withDate(LocalDate date) {
				this.date = date;
				return this;
			}

			public Builder withReason(DeregisterReason reason) {
				this.reason = reason;
				return this;
			}

			public DeregistrationRequest build() {
				return new DeregistrationRequest(this);
			}
		}
	}

}
