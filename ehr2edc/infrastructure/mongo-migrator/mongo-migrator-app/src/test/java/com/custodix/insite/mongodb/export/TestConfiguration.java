package com.custodix.insite.mongodb.export;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.custodix.insite.mongodb.export.patient.domain.model.TestEventPublisher;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class TestConfiguration {

	/**
	 * Overrides springs's default job repository,
	 * since it attempts to make use of the datasource of our embedded database
	 */
	@Bean
	JobRepository jobRepository() throws Exception {
		PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();
		return new MapJobRepositoryFactoryBean(transactionManager).getObject();
	}

	@Bean
	JobLauncher jobLauncher(JobRepository jobRepository) {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		return jobLauncher;
	}

	@Bean
	EmbeddedDatabase embeddedDatabase() throws IOException {
		EmbeddedDatabase database = EmbeddedDatabase.getEmbeddedDatabase();
		database.refreshDatabase();
		return database;
	}

	@Bean
	DataSource dataSource(EmbeddedDatabase embeddedDatabase) {
		HikariConfig config = basicConfig();
		configureBlueModel(config, embeddedDatabase);

		return new HikariDataSource(config);
	}

	@Bean
	JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	TestEventPublisher eventPublisher() {
		return new TestEventPublisher();
	}

	private HikariConfig basicConfig() {
		HikariConfig config = new HikariConfig();
		config.setAutoCommit(false);
		return config;
	}

	private void configureBlueModel(HikariConfig config, final EmbeddedDatabase embeddedDatabase) {
		config.setJdbcUrl(embeddedDatabase.getJdbcUrlDemoData());
		config.setUsername("i2b2demodata");
		config.setPassword("demouser");
		config.setMaximumPoolSize(2);
		config.setIdleTimeout(30000);
		config.setConnectionTestQuery("/* ping */ SELECT 1");
		config.setMaxLifetime(1800_000);
		config.setConnectionTimeout(30000);
	}
}