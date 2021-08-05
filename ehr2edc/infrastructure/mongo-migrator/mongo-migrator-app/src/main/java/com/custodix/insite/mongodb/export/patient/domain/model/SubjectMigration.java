package com.custodix.insite.mongodb.export.patient.domain.model;

import java.time.LocalDateTime;

import com.custodix.insite.mongodb.export.patient.domain.exceptions.DomainException;
import com.custodix.insite.mongodb.export.patient.domain.exceptions.SubjectMigrationAlreadyStartedException;
import com.custodix.insite.mongodb.export.patient.domain.model.common.SubjectMigrationStatus;
import com.custodix.insite.mongodb.export.patient.snapshot.SubjectMigrationSnapshot;
import com.custodix.insite.mongodb.export.patient.vocabulary.SubjectMigrationId;
import com.custodix.insite.mongodb.vocabulary.PatientExporterId;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public final class SubjectMigration {

	private SubjectMigrationId id;
	private final SubjectId subjectId;
	private SubjectMigrationStatus status;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime failDate;
	private PatientExporterId patientExporterId;

	private SubjectMigration(final SubjectId subjectId) {
		this.subjectId = subjectId;
		this.status = SubjectMigrationStatus.CREATED;
	}

	private SubjectMigration(final SubjectMigrationSnapshot subjectMigrationSnapshot) {
		this.id = subjectMigrationSnapshot.getId();
		this.subjectId = subjectMigrationSnapshot.getSubjectId();
		this.status = subjectMigrationSnapshot.getStatus();
		this.startDate = subjectMigrationSnapshot.getStartDate();
		this.endDate = subjectMigrationSnapshot.getEndDate();
		this.failDate = subjectMigrationSnapshot.getFailDate();
		this.patientExporterId = subjectMigrationSnapshot.getPatientExporterId();
	}

	public static SubjectMigration getInstanceFor(final SubjectId subjectId) {
		return new SubjectMigration(subjectId);
	}

	public SubjectMigrationSnapshot toSnapshot() {
		return SubjectMigrationSnapshot.newBuilder()
				.withId(id)
				.withSubjectId(subjectId)
				.withStatus(status)
				.withEndDate(endDate)
				.withStartDate(startDate)
				.withFailDate(failDate)
				.withPatientExporterId(patientExporterId)
				.build();
	}

	public static SubjectMigration restoreFrom(SubjectMigrationSnapshot subjectMigrationSnapshot) {
		return new SubjectMigration(subjectMigrationSnapshot);
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

	public void end(LocalDateTime endDate) {
		validateForEnding();
		this.endDate = endDate;
		this.status = SubjectMigrationStatus.ENDED;
	}

	private void validateForEnding() {
		if(!status.equals(SubjectMigrationStatus.STARTED)) {
			throw new DomainException(String.format(
							"Subject migration for subject id '%s' has currently the status '%s' and cannot be ended",
							subjectId.getId(),
							status
					));
		}
	}

	public void start(long maxRunningPeriodInSeconds, LocalDateTime startDate, final PatientExporterId patientExporterId) {
		if(isNotActive()) {
			startMigration(startDate, patientExporterId);
		} else {
			restartMigration(maxRunningPeriodInSeconds, startDate, patientExporterId);
		}
	}

	public void fail(LocalDateTime failDate) {
		this.status = SubjectMigrationStatus.FAILED;
		this.failDate = failDate;
	}

	public void canBeStarted(long maxRunningPeriodInSeconds) {
		validateForRestartingMigration(maxRunningPeriodInSeconds);
	}

	private boolean isNotActive() {
		return status.equals(SubjectMigrationStatus.CREATED);
	}

	private void startMigration(final LocalDateTime startDate, final PatientExporterId patientExporterId) {
		this.startDate = startDate;
		this.endDate = null;
		this.failDate = null;
		this.status = SubjectMigrationStatus.STARTED;
		this.patientExporterId = patientExporterId;
	}

	private void restartMigration(long maxRunningPeriodInSeconds, LocalDateTime startDate, final PatientExporterId patientExporterId) {
		validateForRestartingMigration(maxRunningPeriodInSeconds);
		this.startDate = startDate;
		this.endDate = null;
		this.failDate = null;
		this.status = SubjectMigrationStatus.STARTED;
		this.patientExporterId = patientExporterId;
	}

	private void validateForRestartingMigration(long maxRunningPeriodInSeconds) {
		if(status.equals(SubjectMigrationStatus.STARTED) && hasNotExceed(maxRunningPeriodInSeconds)) {
			throw new SubjectMigrationAlreadyStartedException(String.format(
					"Migration cannot be started because an existing migration is running for subject is '%s' and "
							+ "has not exceed the maximum running period.",
					getSubjectId().getId()));
		}
	}
	private boolean hasNotExceed(final long maxRunningPeriodInSeconds) {
		return DomainTime.now().minusSeconds(maxRunningPeriodInSeconds).isBefore(this.startDate);
	}
}
