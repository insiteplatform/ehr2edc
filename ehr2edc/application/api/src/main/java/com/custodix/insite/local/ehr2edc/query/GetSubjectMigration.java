package com.custodix.insite.local.ehr2edc.query;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientExporterId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectMigrationStatus;

public interface GetSubjectMigration {

	Response getSubjectMigration(@Valid @NotNull Request request);

	final class Request {
		@NotNull
		@Valid
		private final SubjectId subjectId;

		private Request(final Builder builder) {
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

			public Builder withSubjectId(final SubjectId val) {
				subjectId = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {

		private final SubjectId subjectId;
		private final SubjectMigrationStatus status;
		private final PatientExporterId patientExporterId;

		private Response(final Builder builder) {
			subjectId = builder.subjectId;
			status = builder.status;
			patientExporterId = builder.patientExporterId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public SubjectMigrationStatus getStatus() {
			return status;
		}

		public PatientExporterId getPatientExporterId() {
			return patientExporterId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private SubjectId subjectId;
			private SubjectMigrationStatus status;
			private PatientExporterId patientExporterId;

			private Builder() {
			}

			public Builder withSubjectId(final SubjectId val) {
				subjectId = val;
				return this;
			}

			public Builder withStatus(final SubjectMigrationStatus val) {
				status = val;
				return this;
			}

			public Builder withPatientExporterId(final PatientExporterId val) {
				patientExporterId = val;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}
}