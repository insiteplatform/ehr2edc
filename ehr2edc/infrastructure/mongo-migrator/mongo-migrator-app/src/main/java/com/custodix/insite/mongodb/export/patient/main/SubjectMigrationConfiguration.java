package com.custodix.insite.mongodb.export.patient.main;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.mongodb.export.patient.application.api.EndSubjectMigration;
import com.custodix.insite.mongodb.export.patient.application.api.FailSubjectMigration;
import com.custodix.insite.mongodb.export.patient.application.api.StartSubjectMigration;
import com.custodix.insite.mongodb.export.patient.application.command.EndSubjectMigrationCommand;
import com.custodix.insite.mongodb.export.patient.application.command.FailSubjectMigrationCommand;
import com.custodix.insite.mongodb.export.patient.application.command.StartSubjectMigrationCommand;
import com.custodix.insite.mongodb.export.patient.domain.repository.SubjectMigrationRepository;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.SubjectMigrationDocumentRepository;
import com.custodix.insite.mongodb.export.patient.infrastructure.subjectmigration.SubjectMigrationMongoRepository;

@Configuration
public class SubjectMigrationConfiguration {

	@Bean
	public SubjectMigrationRepository subjectMigrationRepository(
			SubjectMigrationDocumentRepository subjectMigrationDocumentRepository) {
		return new SubjectMigrationMongoRepository(subjectMigrationDocumentRepository);
	}

	@Bean
	public StartSubjectMigration startSubjectMigration(SubjectMigrationRepository subjectMigrationRepository,
			@Value("${export.patient.maximum.running.period.in.seconds:14400}") long maxRunningPeriodInSeconds) {
		return new StartSubjectMigrationCommand(subjectMigrationRepository, maxRunningPeriodInSeconds);
	}

	@Bean
	public EndSubjectMigration endSubjectMigration(SubjectMigrationRepository subjectMigrationRepository) {
		return new EndSubjectMigrationCommand(subjectMigrationRepository);
	}

	@Bean
	public FailSubjectMigration failSubjectMigration(SubjectMigrationRepository subjectMigrationRepository) {
		return new FailSubjectMigrationCommand(subjectMigrationRepository);
	}
}
