package com.custodix.insite.local.ehr2edc.query.mongo.subjectmigrator.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.local.ehr2edc.snapshots.SubjectMigrationSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientExporterId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectMigrationId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectMigrationStatus;

@Document(collection = SubjectMigrationDocument.COLLECTION)
public final class SubjectMigrationDocument {
	public static final String COLLECTION = "SubjectMigration";

	@Id
	private final String id;
	private final SubjectId subjectId;
	private final SubjectMigrationStatus status;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final LocalDateTime failDate;
	private final PatientExporterId patientExporterId;

	@PersistenceConstructor
	private SubjectMigrationDocument(final String id,
			final SubjectId subjectId,
			final SubjectMigrationStatus status,
			final LocalDateTime startDate,
			final LocalDateTime endDate,
			final LocalDateTime failDate,
			final PatientExporterId patientExporterId) {
		this.id = id;
		this.subjectId = subjectId;
		this.status = status;
		this.startDate = startDate;
		this.endDate = endDate;
		this.failDate = failDate;
		this.patientExporterId = patientExporterId;
	}

	private SubjectMigrationDocument(final Builder builder) {
		id = builder.id;
		subjectId = builder.subjectId;
		status = builder.status;
		startDate = builder.startDate;
		endDate = builder.endDate;
		failDate = builder.failDate;
		patientExporterId = builder.patientExporterId;
	}

	public static SubjectMigrationDocument fromSnapshot(SubjectMigrationSnapshot subjectMigrationSnapshot) {
		return new SubjectMigrationDocument(
				subjectMigrationSnapshot.getId().getId(),
				subjectMigrationSnapshot.getSubjectId(),
				subjectMigrationSnapshot.getStatus(),
				subjectMigrationSnapshot.getStartDate(),
				subjectMigrationSnapshot.getEndDate(),
				subjectMigrationSnapshot.getFailDate(),
				subjectMigrationSnapshot.getPatientExporterId());
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public SubjectMigrationSnapshot toSnapshot() {
		return SubjectMigrationSnapshot.newBuilder()
				.withId(SubjectMigrationId.of(id))
				.withSubjectId(subjectId)
				.withStatus(status)
				.withStartDate(startDate)
				.withEndDate(endDate)
				.withPatientExporterId(patientExporterId)
				.build();
	}

	public String getId() {
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

	public PatientExporterId getPatientExporterId() {
		return patientExporterId;
	}

	public static final class Builder {
		private String id;
		private SubjectId subjectId;
		private SubjectMigrationStatus status;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private LocalDateTime failDate;
		private PatientExporterId patientExporterId;

		private Builder() {
		}

		public Builder withId(final String val) {
			id = val;
			return this;
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

		public SubjectMigrationDocument build() {
			return new SubjectMigrationDocument(this);
		}
	}
}
