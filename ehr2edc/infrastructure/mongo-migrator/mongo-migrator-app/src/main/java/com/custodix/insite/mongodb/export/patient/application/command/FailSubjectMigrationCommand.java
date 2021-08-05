package com.custodix.insite.mongodb.export.patient.application.command;

import com.custodix.insite.mongodb.export.patient.application.api.FailSubjectMigration;
import com.custodix.insite.mongodb.export.patient.domain.model.SubjectMigration;
import com.custodix.insite.mongodb.export.patient.domain.repository.SubjectMigrationRepository;

public class FailSubjectMigrationCommand implements FailSubjectMigration {

	private final SubjectMigrationRepository subjectMigrationRepository;

	public FailSubjectMigrationCommand(final SubjectMigrationRepository subjectMigrationRepository) {
		this.subjectMigrationRepository = subjectMigrationRepository;
	}

	@Override
	public void fail(final Request request) {
		SubjectMigration subjectMigration = subjectMigrationRepository.getBySubjectId(request.getSubjectId());
		subjectMigration.fail(request.getFailDate());
		subjectMigrationRepository.save(subjectMigration);
	}
}
