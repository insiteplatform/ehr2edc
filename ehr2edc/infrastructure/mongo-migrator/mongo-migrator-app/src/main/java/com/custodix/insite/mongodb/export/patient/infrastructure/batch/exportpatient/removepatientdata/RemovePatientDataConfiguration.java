package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.removepatientdata;

import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.MONGO_TEMPLATE_MONGO_MIGRATOR;
import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.STEP_BUILDER_FACTORY_MONGO_MIGRATOR;

import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class RemovePatientDataConfiguration {

	@Bean
	public RemovePatientDataTasklet removePatientDataTasklet(@Qualifier(MONGO_TEMPLATE_MONGO_MIGRATOR) MongoTemplate mongoTemplate) {
		return new RemovePatientDataTasklet(mongoTemplate);
	}

	@Bean
	public RemovePatientDataStepFactory removePatientDataStepFactory(
			@Qualifier(STEP_BUILDER_FACTORY_MONGO_MIGRATOR) StepBuilderFactory stepBuilderFactory,
			RemovePatientDataTasklet removePatientDataTasklet) {
		return new RemovePatientDataStepFactory(stepBuilderFactory, removePatientDataTasklet);
	}

}
