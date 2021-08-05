package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.subjectmigration;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.mongodb.export.patient.domain.model.common.SubjectMigrationStatus;
import com.custodix.insite.mongodb.export.patient.snapshot.SubjectMigrationSnapshot;
import com.custodix.insite.mongodb.export.patient.vocabulary.SubjectMigrationId;
import com.custodix.insite.mongodb.vocabulary.PatientExporterId;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@Document(collection = SubjectMigrationDocument.COLLECTION)
public final class SubjectMigrationDocument {
	public static final String COLLECTION = "SubjectMigration";

	@Id
	/* Id cannot be final because of the following issue:
		https://jira.spring.io/browse/DATACMNS-1374
	*/
	private String id;
	private final SubjectId subjectId;
	private final SubjectMigrationStatus status;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final LocalDateTime failDate;
	private final String patientExporterId;

	@PersistenceConstructor
	private SubjectMigrationDocument(final String id,
			final SubjectId subjectId,
			final SubjectMigrationStatus status,
			final LocalDateTime startDate,
			final LocalDateTime endDate,
			final LocalDateTime failDate,
			final String patientExporterId) {
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

	public String getPatientExporterId() {
		return patientExporterId;
	}

	public LocalDateTime getFailDate() {
		return failDate;
	}

	public SubjectMigrationSnapshot toSnapshot() {
		return SubjectMigrationSnapshot.newBuilder()
				.withId(SubjectMigrationId.of(id))
				.withSubjectId(subjectId)
				.withStatus(status)
				.withStartDate(startDate)
				.withEndDate(endDate)
				.withFailDate(failDate)
				.withPatientExporterId(patientExporterId != null ? PatientExporterId.of(patientExporterId) : null)
				.build();
	}

	public static SubjectMigrationDocument fromSnapshot(SubjectMigrationSnapshot subjectMigrationSnapshot) {
		String migrationSnapshotId = subjectMigrationSnapshot.getId() == null ?
				null :
				subjectMigrationSnapshot.getId()
						.getId();
		return SubjectMigrationDocument.newBuilder()
				.withId(migrationSnapshotId)
				.withSubjectId(subjectMigrationSnapshot.getSubjectId())
				.withStatus(subjectMigrationSnapshot.getStatus())
				.withStartDate(subjectMigrationSnapshot.getStartDate())
				.withEndDate(subjectMigrationSnapshot.getEndDate())
				.withFailDate(subjectMigrationSnapshot.getFailDate())
				.withPatientExporterId(subjectMigrationSnapshot.getPatientExporterId() != null ?subjectMigrationSnapshot.getPatientExporterId().getId() : null)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String id;
		private SubjectId subjectId;
		private SubjectMigrationStatus status;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private LocalDateTime failDate;
		private String patientExporterId;

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

		public Builder withPatientExporterId(final String val) {
			patientExporterId = val;
			return this;
		}

		public SubjectMigrationDocument build() {
			return new SubjectMigrationDocument(this);
		}
	}
}
