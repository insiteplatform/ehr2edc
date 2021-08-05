package com.custodix.insite.local.ehr2edc.rest.patient;

import java.time.LocalDate;
import java.util.List;

import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = GetStudiesForPatientResponse.Builder.class)
final class GetStudiesForPatientResponse {
	private final List<Study> availableStudies;
	private final List<Study> registeredStudies;

	private GetStudiesForPatientResponse(Builder builder) {
		availableStudies = builder.availableStudies;
		registeredStudies = builder.registeredStudies;
	}

	public List<Study> getAvailableStudies() {
		return availableStudies;
	}

	public List<Study> getRegisteredStudies() {
		return registeredStudies;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(GetStudiesForPatientResponse copy) {
		Builder builder = new Builder();
		builder.availableStudies = copy.availableStudies;
		builder.registeredStudies = copy.registeredStudies;
		return builder;
	}

	@JsonPOJOBuilder
	public static final class Builder {
		private List<Study> availableStudies;
		private List<Study> registeredStudies;

		private Builder() {
		}

		public Builder withAvailableStudies(List<Study> availableStudies) {
			this.availableStudies = availableStudies;
			return this;
		}

		public Builder withRegisteredStudies(List<Study> registeredStudies) {
			this.registeredStudies = registeredStudies;
			return this;
		}

		public GetStudiesForPatientResponse build() {
			return new GetStudiesForPatientResponse(this);
		}
	}

	@JsonDeserialize(builder = Study.Builder.class)
	static final class Study {
		private final StudyId studyId;
		private final String name;
		private final String description;
		@JsonInclude(JsonInclude.Include.NON_NULL)
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

		public static Builder newBuilder(Study copy) {
			Builder builder = new Builder();
			builder.studyId = copy.studyId;
			builder.name = copy.name;
			builder.description = copy.description;
			builder.subject = copy.subject;
			return builder;
		}

		@JsonPOJOBuilder
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

	@JsonDeserialize(builder = Subject.Builder.class)
	static final class Subject {
		private final SubjectId subjectId;
		private final EDCSubjectReference edcSubjectReference;
		private final PatientCDWReference patientId;
		private final LocalDate dateOfConsent;

		private Subject(Builder builder) {
			subjectId = builder.subjectId;
			edcSubjectReference = builder.edcSubjectReference;
			patientId = builder.patientId;
			dateOfConsent = builder.dateOfConsent;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public EDCSubjectReference getEdcSubjectReference() {
			return edcSubjectReference;
		}

		public PatientCDWReference getPatientId() {
			return patientId;
		}

		public LocalDate getDateOfConsent() {
			return dateOfConsent;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Subject copy) {
			Builder builder = new Builder();
			builder.subjectId = copy.subjectId;
			builder.edcSubjectReference = copy.edcSubjectReference;
			builder.patientId = copy.patientId;
			builder.dateOfConsent = copy.dateOfConsent;
			return builder;
		}

		@JsonPOJOBuilder
		public static final class Builder {
			private SubjectId subjectId;
			private EDCSubjectReference edcSubjectReference;
			private PatientCDWReference patientId;
			private LocalDate dateOfConsent;

			private Builder() {
			}

			public Builder withSubjectId(SubjectId subjectId) {
				this.subjectId = subjectId;
				return this;
			}

			public Builder withEdcSubjectReference(EDCSubjectReference edcSubjectReference) {
				this.edcSubjectReference = edcSubjectReference;
				return this;
			}

			public Builder withPatientId(PatientCDWReference patientCDWReference) {
				this.patientId = patientCDWReference;
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
