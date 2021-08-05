package com.custodix.insite.mongodb.export.patient.application.command;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.custodix.insite.mongodb.export.patient.application.api.StartSubjectMigration;
import com.custodix.insite.mongodb.export.patient.domain.model.SubjectMigration;
import com.custodix.insite.mongodb.export.patient.domain.repository.SubjectMigrationRepository;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@Validated
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StartSubjectMigrationCommand implements StartSubjectMigration {

	private final SubjectMigrationRepository subjectMigrationRepository;
	private final long maxRunningPeriodInSeconds;

	public StartSubjectMigrationCommand(final SubjectMigrationRepository subjectMigrationRepository, final long maxRunningPeriodInSeconds) {
		this.subjectMigrationRepository = subjectMigrationRepository;
		this.maxRunningPeriodInSeconds = maxRunningPeriodInSeconds;
	}

	@Override
	public void start(final Request request) {
		SubjectMigration subjectMigration = getSubjectMigration(request.getSubjectId());
		subjectMigration.start(maxRunningPeriodInSeconds, request.getStartDate(), request.getPatientExporterId());
		subjectMigrationRepository.save(subjectMigration);
	}

	private SubjectMigration getSubjectMigration(final SubjectId subjectId) {
		return subjectMigrationRepository
				.findBySubjectId(subjectId)
				.orElseGet(() -> SubjectMigration.getInstanceFor(subjectId));
	}
}
