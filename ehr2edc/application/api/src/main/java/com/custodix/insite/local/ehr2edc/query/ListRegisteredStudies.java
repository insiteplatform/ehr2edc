package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.AUTHENTICATED;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface ListRegisteredStudies {

	@Allow(AUTHENTICATED)
	Response registeredStudies(@Valid @NotNull Request request);

	final class Request {
		@NotNull
		@Valid
		private final PatientCDWReference patientCDWReference;

		private Request(Builder builder) {
			patientCDWReference = builder.patientCDWReference;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public PatientCDWReference getPatientCDWReference() {
			return patientCDWReference;
		}

		public static final class Builder {
			private PatientCDWReference patientCDWReference;

			private Builder() {
			}

			public Builder withPatientCDWReference(PatientCDWReference patientCDWReference) {
				this.patientCDWReference = patientCDWReference;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final List<Study> registeredStudies;

		private Response(Builder builder) {
			registeredStudies = builder.registeredStudies;
		}

		public List<Study> getRegisteredStudies() {
			return registeredStudies;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private List<Study> registeredStudies = new ArrayList<>();

			private Builder() {
			}

			public Builder withRegisteredStudies(List<Study> registeredStudies) {
				this.registeredStudies = registeredStudies;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class Study {
		private final StudyId studyId;
		private final String name;
		private final String description;
		private final Subject subject;

		private Study(Builder builder) {
			studyId = builder.studyId;
			name = builder.name;
			description = builder.description;
			subject = builder.subject;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public Subject getSubject() {
			return subject;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private StudyId studyId;
			private String name;
			private String description;
			private Subject subject;

			private Builder() {
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Builder withName(String name) {
				this.name = name;
				return this;
			}

			public Builder withDescription(String description) {
				this.description = description;
				return this;
			}

			public Builder withSubject(Subject subject) {
				this.subject = subject;
				return this;
			}

			public Study build() {
				return new Study(this);
			}
		}
	}

	final class Subject {
		private final PatientCDWReference patientCDWReference;
		private final SubjectId subjectId;
		private final EDCSubjectReference edcSubjectReference;
		private final LocalDate dateOfConsent;

		private Subject(Builder builder) {
			patientCDWReference = builder.patientCDWReference;
			subjectId = builder.subjectId;
			edcSubjectReference = builder.edcSubjectReference;
			dateOfConsent = builder.dateOfConsent;
		}

		public PatientCDWReference getPatientCDWReference() {
			return patientCDWReference;
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

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private PatientCDWReference patientCDWReference;
			private SubjectId subjectId;
			private EDCSubjectReference edcSubjectReference;
			private LocalDate dateOfConsent;

			private Builder() {
			}

			public Builder withPatientCDWReference(PatientCDWReference patientCDWReference) {
				this.patientCDWReference = patientCDWReference;
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

			public Subject build() {
				return new Subject(this);
			}
		}
	}
}
