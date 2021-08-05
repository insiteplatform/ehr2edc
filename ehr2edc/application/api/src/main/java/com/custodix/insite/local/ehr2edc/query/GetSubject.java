package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public interface GetSubject {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response getSubject(@Valid @NotNull Request request);

	final class Request {
		@AuthorizationCorrelator
		@NotNull
		@Valid
		private final StudyId studyId;
		@NotNull
		@Valid
		private final SubjectId subjectId;

		private Request(Builder builder) {
			studyId = builder.studyId;
			subjectId = builder.subjectId;
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
			private StudyId studyId;
			private SubjectId subjectId;

			private Builder() {
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

	final class Response {
		private final Subject subject;

		private Response(Builder builder) {
			subject = builder.subject;
		}

		public Subject getSubject() {
			return subject;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private Subject subject;

			private Builder() {
			}

			public Builder withSubject(Subject subject) {
				this.subject = subject;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class Subject {
		private final PatientCDWReference patientId;
		private final SubjectId subjectId;
		private final EDCSubjectReference edcSubjectReference;
		private final LocalDate dateOfConsent;
		private final LocalDate dateOfConsentWithdrawn;
		private final DeregisterReason dataCaptureStopReason;

		private Subject(Builder builder) {
			patientId = builder.patientId;
			subjectId = builder.subjectId;
			edcSubjectReference = builder.edcSubjectReference;
			dateOfConsent = builder.dateOfConsent;
			dateOfConsentWithdrawn = builder.dateOfConsentWithdrawn;
			dataCaptureStopReason = builder.dataCaptureStopReason;
		}

		public PatientCDWReference getPatientId() {
			return patientId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public EDCSubjectReference getEdcSubjectReference() {
			return edcSubjectReference;
		}

		public LocalDate getDateOfConsent() {
			return dateOfConsent;
		}

		public LocalDate getDateOfConsentWithdrawn() {
			return dateOfConsentWithdrawn;
		}

		public DeregisterReason getDataCaptureStopReason() {
			return dataCaptureStopReason;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private PatientCDWReference patientId;
			private SubjectId subjectId;
			private EDCSubjectReference edcSubjectReference;
			private LocalDate dateOfConsent;
			private LocalDate dateOfConsentWithdrawn;
			private DeregisterReason dataCaptureStopReason;

			private Builder() {
			}

			public Builder withPatientId(PatientCDWReference patientCDWReference) {
				this.patientId = patientCDWReference;
				return this;
			}

			public Builder withSubjectId(SubjectId subjectId) {
				this.subjectId = subjectId;
				return this;
			}

			public Builder withEdcSubjectReference(EDCSubjectReference edcSubjectReference) {
				this.edcSubjectReference = edcSubjectReference;
				return this;
			}

			public Builder withDateOfConsent(LocalDate dateOfConsent) {
				this.dateOfConsent = dateOfConsent;
				return this;
			}

			public Builder withDateOfConsentWithdrawn(LocalDate dateOfConsentWithdrawn) {
				this.dateOfConsentWithdrawn = dateOfConsentWithdrawn;
				return this;
			}

			public Builder withDataCaptureStopReason(DeregisterReason dataCaptureStopReason) {
				this.dataCaptureStopReason = dataCaptureStopReason;
				return this;
			}

			public Subject build() {
				return new Subject(this);
			}
		}
	}
}