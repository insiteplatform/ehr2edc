package com.custodix.insite.local.ehr2edc.snapshots;

import java.time.LocalDateTime;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientExporterId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectMigrationId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectMigrationStatus;

public final class SubjectMigrationSnapshot {

	private final SubjectMigrationId id;
	private final SubjectId subjectId;
	private final SubjectMigrationStatus status;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final LocalDateTime failDate;
	private final PatientExporterId patientExporterId;

	private SubjectMigrationSnapshot(final Builder builder) {
		id = builder.id;
		subjectId = builder.subjectId;
		status = builder.status;
		startDate = builder.startDate;
		endDate = builder.endDate;
		failDate = builder.failDate;
		patientExporterId = builder.patientExporterId;
	}

	public SubjectMigrationId getId() {
		return id;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public SubjectMigrationStatus getStatus() {
		return status;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public LocalDateTime getFailDate() {
		return failDate;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public PatientExporterId getPatientExporterId() {
		return patientExporterId;
	}

	public static final class Builder {
		private SubjectId subjectId;
		private SubjectMigrationStatus status;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private LocalDateTime failDate;
		private PatientExporterId patientExporterId;
		private SubjectMigrationId id;

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

		public Builder withStartDate(final LocalDateTime val) {
			startDate = val;
			return this;
		}

		public Builder withEndDate(final LocalDateTime val) {
			endDate = val;
			return this;
		}

		public Builder withFailDate(final LocalDateTime val) {
			failDate = val;
			return this;
		}

		public Builder withPatientExporterId(final PatientExporterId val) {
			patientExporterId = val;
			return this;
		}

		public SubjectMigrationSnapshot build() {
			return new SubjectMigrationSnapshot(this);
		}

		public Builder withId(final SubjectMigrationId val) {
			id = val;
			return this;
		}
	}
}
