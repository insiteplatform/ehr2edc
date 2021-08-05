package com.custodix.insite.mongodb.export.patient.event.controller;

import com.custodix.insite.mongodb.export.patient.application.api.StartSubjectMigration;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientStarting;
import com.custodix.insite.mongodb.vocabulary.PatientExporterId;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class StartSubjectMigrationController {

	private final StartSubjectMigration startSubjectMigration;

	public StartSubjectMigrationController(final StartSubjectMigration startSubjectMigration) {
		this.startSubjectMigration = startSubjectMigration;
	}

	public void startSubjectMigration(final ExportPatientStarting event) {
		startSubjectMigration.start(toRequest(event));
	}

	private StartSubjectMigration.Request toRequest(final ExportPatientStarting event) {
		return StartSubjectMigration.Request.newBuilder()
				.withSubjectId(SubjectId.of(event.getSubjectId()))
				.withStartDate(event.getDate())
				.withPatientExporterId(PatientExporterId.of(event.getPatientExporterId()))
				.build();
	}
}
