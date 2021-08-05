package com.custodix.insite.mongodb.export.patient.infrastructure.embedded;

import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.JOB_BUILDER_FACTORY_MONGO_MIGRATOR;
import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.JOB_LAUNCHER_MONGO_MIGRATOR;
import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.STEP_BUILDER_FACTORY_MONGO_MIGRATOR;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddedConceptConfiguration {
	@Bean
	LaboratoryConceptInfoFetcher laboratoryConceptInfoFetcher(
			@Qualifier(JOB_BUILDER_FACTORY_MONGO_MIGRATOR) JobBuilderFactory jobBuilderFactory,
			@Qualifier(JOB_LAUNCHER_MONGO_MIGRATOR) JobLauncher jobLauncher,
			@Qualifier(STEP_BUILDER_FACTORY_MONGO_MIGRATOR) StepBuilderFactory stepBuilderFactory
	) {
		return new LaboratoryConceptInfoFetcher(jobBuilderFactory, stepBuilderFactory, jobLauncher);
	}

	@Bean(initMethod = "init")
	public EmbeddedLaboratoryConceptInfoRepository laboratoryConceptInfoRepository(
			LaboratoryConceptInfoFetcher laboratoryConceptInfoFetcher) {
		return new EmbeddedLaboratoryConceptInfoRepository(laboratoryConceptInfoFetcher);
	}
}