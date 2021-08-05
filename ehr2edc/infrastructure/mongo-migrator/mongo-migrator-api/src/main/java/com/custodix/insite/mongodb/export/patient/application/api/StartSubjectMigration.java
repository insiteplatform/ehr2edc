package com.custodix.insite.mongodb.export.patient.application.api;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.mongodb.vocabulary.PatientExporterId;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public interface StartSubjectMigration {

	void start(@Valid Request request);

	final class Request {
		@Valid
		@NotNull
		private final SubjectId subjectId;
		@NotNull
		private final LocalDateTime startDate;
		@Valid
		@NotNull
		private final PatientExporterId patientExporterId;

		private Request(final Builder builder) {
			subjectId = builder.subjectId;
			startDate = builder.startDate;
			patientExporterId = builder.patientExporterId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public LocalDateTime getStartDate() {
			return startDate;
		}

		public PatientExporterId getPatientExporterId() {
			return patientExporterId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private SubjectId subjectId;
			private LocalDateTime startDate;
			private PatientExporterId patientExporterId;

			private Builder() {
			}

			public Builder withSubjectId(final SubjectId val) {
				subjectId = val;
				return this;
			}

			public Builder withStartDate(final LocalDateTime val) {
				startDate = val;
				return this;
			}

			public Builder withPatientExporterId(final PatientExporterId val) {
				patientExporterId = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}
}
