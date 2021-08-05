package com.custodix.insite.mongodb.export.patient.event.controller;

import com.custodix.insite.mongodb.export.patient.application.api.FailSubjectMigration;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientFailed;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class FailSubjectMigrationController {

	private final FailSubjectMigration failSubjectMigration;

	public FailSubjectMigrationController(final FailSubjectMigration failSubjectMigration) {
		this.failSubjectMigration = failSubjectMigration;
	}

	public void failSubjectMigration(final ExportPatientFailed event) {
		failSubjectMigration.fail(toRequest(event));
	}

	private FailSubjectMigration.Request toRequest(final ExportPatientFailed event) {
		return FailSubjectMigration.Request.newBuilder()
				.withSubjectId(SubjectId.of(event.getSubjectId()))
				.withFailDate(event.getDate())
				.build();
	}
}
