package com.custodix.insite.mongodb.export.patient.event.controller;

import com.custodix.insite.mongodb.export.patient.application.api.EndSubjectMigration;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientEnded;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public class EndSubjectMigrationController {

	private final EndSubjectMigration endSubjectMigration;

	public EndSubjectMigrationController(final EndSubjectMigration endSubjectMigration) {
		this.endSubjectMigration = endSubjectMigration;
	}

	public void endSubjectMigration(final ExportPatientEnded event) {
		endSubjectMigration.end(toRequest(event));
	}

	private EndSubjectMigration.Request toRequest(final ExportPatientEnded event) {
		return EndSubjectMigration.Request.newBuilder()
				.withSubjectId(SubjectId.of(event.getSubjectId()))
				.withEndDate(event.getDate())
				.build();
	}
}
