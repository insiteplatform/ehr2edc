package com.custodix.insite.mongodb.export.patient.application.command;

import com.custodix.insite.mongodb.export.patient.application.annotation.Command;
import com.custodix.insite.mongodb.export.patient.application.api.EndSubjectMigration;
import com.custodix.insite.mongodb.export.patient.domain.model.SubjectMigration;
import com.custodix.insite.mongodb.export.patient.domain.repository.SubjectMigrationRepository;

@Command
public class EndSubjectMigrationCommand implements EndSubjectMigration {
	private final SubjectMigrationRepository subjectMigrationRepository;

	public EndSubjectMigrationCommand(final SubjectMigrationRepository subjectMigrationRepository) {
		this.subjectMigrationRepository = subjectMigrationRepository;
	}

	@Override
	public void end(final Request request) {
		SubjectMigration subjectMigration = subjectMigrationRepository.getBySubjectId(request.getSubjectId());
		subjectMigration.end(request.getEndDate());
		subjectMigrationRepository.save(subjectMigration);
	}
}
