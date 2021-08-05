package com.custodix.insite.mongodb.export.patient.application.command;

import java.util.Optional;

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient;
import com.custodix.insite.mongodb.export.patient.domain.model.PatientExporter;
import com.custodix.insite.mongodb.export.patient.domain.model.SubjectMigration;
import com.custodix.insite.mongodb.export.patient.domain.repository.SubjectMigrationRepository;
import com.custodix.insite.mongodb.export.patient.domain.service.ExportPatientRunner;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class ExportPatientCommand implements ExportPatient {
	private final SubjectMigrationRepository subjectMigrationRepository;
	private final ExportPatientRunner exportPatientRunner;
	private final long maxRunningPeriodInSeconds;

	public ExportPatientCommand(
			final SubjectMigrationRepository subjectMigrationRepository,
			final ExportPatientRunner exportPatientRunner,
			final long maxRunningPeriodInSeconds) {
		this.subjectMigrationRepository = subjectMigrationRepository;
		this.maxRunningPeriodInSeconds = maxRunningPeriodInSeconds;
		this.exportPatientRunner = exportPatientRunner;
	}

	@Override
	public void export(Request request) {
		PatientIdentifier patientIdentifier = request.getPatientIdentifier();
		validateSubjectMigration(patientIdentifier);
		PatientExporter.getInstance(exportPatientRunner).export(patientIdentifier);
	}

	private void validateSubjectMigration(final PatientIdentifier patientIdentifier) {
		findSubjectMigration(patientIdentifier).ifPresent(s -> s.canBeStarted(maxRunningPeriodInSeconds));
	}

	private Optional<SubjectMigration> findSubjectMigration(final PatientIdentifier patientIdentifier) {
		SubjectId subjectId = patientIdentifier.getSubjectId();
		return subjectMigrationRepository
				.findBySubjectId(subjectId);
	}
}