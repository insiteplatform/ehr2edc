package com.custodix.insite.mongodb.export.patient.main;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MongoMigratorBatchConfiguration {

	private static final String JOB_REPOSITORY_MONGO_MIGRATOR = "jobRepositoryMongoMigrator";
	private static final String TRANSACTION_MANAGER_MONGO_MIGRATOR = "transactionManagerMongoMigrator";
	private static final String MAP_JOB_REPOSITORY_FACTORY_MONGO_MIGRATOR = "mapJobRepositoryFactoryMongoMigrator";
	public static final String JOB_LAUNCHER_MONGO_MIGRATOR = "jobLauncherMongoMigrator";
	public static final String STEP_BUILDER_FACTORY_MONGO_MIGRATOR = "stepBuilderFactoryMongoMigrator";
	public static final String JOB_BUILDER_FACTORY_MONGO_MIGRATOR = "jobBuilderFactoryMongoMigrator";
	public static final String MONGO_TEMPLATE_MONGO_MIGRATOR = "mongoMigratorMongoTemplate";

	@Bean
	public ResourcelessTransactionManager transactionManagerMongoMigrator() {
		return new ResourcelessTransactionManager();
	}

	@Bean
	public MapJobRepositoryFactoryBean mapJobRepositoryFactoryMongoMigrator(
			@Qualifier(TRANSACTION_MANAGER_MONGO_MIGRATOR) ResourcelessTransactionManager transactionManager)
			throws Exception {
		MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean(transactionManager);
		factory.setValidateTransactionState(false);
		factory.afterPropertiesSet();
		return factory;
	}

	@Bean
	public JobRepository  jobRepositoryMongoMigrator(MapJobRepositoryFactoryBean factory) throws Exception {
		return factory.getObject();
	}

	@Bean
	public JobExplorer jobExplorerMongoMigrator(@Qualifier(MAP_JOB_REPOSITORY_FACTORY_MONGO_MIGRATOR) MapJobRepositoryFactoryBean factory) {
		return new SimpleJobExplorer(factory.getJobInstanceDao(), factory.getJobExecutionDao(),
				factory.getStepExecutionDao(), factory.getExecutionContextDao());
	}

	@Bean
	public SimpleJobLauncher jobLauncherMongoMigrator(@Qualifier(JOB_REPOSITORY_MONGO_MIGRATOR) JobRepository jobRepository) {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		return launcher;
	}

	@Bean
	public JobBuilderFactory jobBuilderFactoryMongoMigrator(@Qualifier(JOB_REPOSITORY_MONGO_MIGRATOR) JobRepository jobRepository) {
		return new JobBuilderFactory(jobRepository);
	}

	@Bean
	public StepBuilderFactory stepBuilderFactoryMongoMigrator(
			@Qualifier(JOB_REPOSITORY_MONGO_MIGRATOR) JobRepository jobRepository,
			@Qualifier(TRANSACTION_MANAGER_MONGO_MIGRATOR) ResourcelessTransactionManager transactionManager
	) {
		return new StepBuilderFactory(jobRepository, transactionManager);
	}
}
