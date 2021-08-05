package com.custodix.insite.local.ehr2edc.command;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.infrastructure.validation.DateRange;
import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.SynchronizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

public interface RegisterSubject {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response register(@Valid @NotNull Request request);

	@JsonDeserialize(builder = Request.Builder.class)
	final class Request {
		@NotNull
		@Valid
		private final PatientCDWReference patientId;
		@SynchronizationCorrelator
		@AuthorizationCorrelator
		@NotNull
		@Valid
		private final StudyId studyId;
		@NotNull
		@Valid
		private final EDCSubjectReference edcSubjectReference;
		@NotNull
		@DateRange(min = "1950-01-01")
		private final LocalDate dateOfConsent;

		@NotNull
		private final String firstName;

		@NotNull
		private final String lastName;

		@NotNull
		@DateRange
		private final LocalDate birthDate;

		private Request(Builder builder) {
			patientId = builder.patientId;
			studyId = builder.studyId;
			edcSubjectReference = builder.edcSubjectReference;
			dateOfConsent = builder.dateOfConsent;
			firstName = builder.firstName;
			lastName = builder.lastName;
			birthDate = builder.birthDate;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Request copy) {
			Builder builder = new Builder();
			builder.patientId = copy.patientId;
			builder.studyId = copy.studyId;
			builder.edcSubjectReference = copy.edcSubjectReference;
			builder.dateOfConsent = copy.dateOfConsent;
			builder.lastName = copy.lastName;
			builder.firstName = copy.firstName;
			builder.birthDate = copy.birthDate;
			return builder;
		}

		public PatientCDWReference getPatientId() {
			return patientId;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public EDCSubjectReference getEdcSubjectReference() {
			return edcSubjectReference;
		}

		public LocalDate getDateOfConsent() {
			return dateOfConsent;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public LocalDate getBirthDate() {
			return birthDate;
		}

		@JsonPOJOBuilder
		public static final class Builder {
			private PatientCDWReference patientId;
			private StudyId studyId;
			private LocalDate dateOfConsent;
			private String firstName;
			private String lastName;
			private LocalDate birthDate;
			private EDCSubjectReference edcSubjectReference;

			private Builder() {
			}

			public Builder withPatientId(PatientCDWReference patientCDWReference) {
				this.patientId = patientCDWReference;
				return this;
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
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

			public Builder withFirstName(String val) {
				firstName = val;
				return this;
			}

			public Builder withLastName(String val) {
				lastName = val;
				return this;
			}

			public Builder withBirthDate(LocalDate val) {
				birthDate = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final SubjectId subjectId;

		private Response(Builder builder) {
			subjectId = builder.subjectId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private SubjectId subjectId;

			private Builder() {
			}

			public Builder withSubjectId(SubjectId subjectId) {
				this.subjectId = subjectId;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}
}