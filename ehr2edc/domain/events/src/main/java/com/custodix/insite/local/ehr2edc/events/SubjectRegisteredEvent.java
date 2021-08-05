package com.custodix.insite.local.ehr2edc.events;

import java.time.LocalDate;

public class SubjectRegisteredEvent implements AsyncSerializationSafeDomainEvent {
	private final String patientId;
	private final String namespace;
	private final String studyId;
	private final String subjectId;
	private final LocalDate date;

	private SubjectRegisteredEvent(Builder builder) {
		patientId = builder.patientId;
		namespace = builder.namespace;
		studyId = builder.studyId;
		subjectId = builder.subjectId;
		date = builder.date;
	}

	public String getPatientId() {
		return patientId;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getStudyId() {
		return studyId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public LocalDate getDate() {
		return date;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String patientId;
		private String namespace;
		private String studyId;
		private String subjectId;
		private LocalDate date;

		private Builder() {
		}

		public Builder withPatientId(String patientId) {
			this.patientId = patientId;
			return this;
		}

		public Builder withNamespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		public Builder withStudyId(String studyId) {
			this.studyId = studyId;
			return this;
		}

		public Builder withSubjectId(String subjectId) {
			this.subjectId = subjectId;
			return this;
		}

		public Builder withDate(LocalDate date) {
			this.date = date;
			return this;
		}

		public SubjectRegisteredEvent build() {
			return new SubjectRegisteredEvent(this);
		}
	}
}
