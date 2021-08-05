package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient;

import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.JOB_BUILDER_FACTORY_MONGO_MIGRATOR;
import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.JOB_LAUNCHER_MONGO_MIGRATOR;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient;
import com.custodix.insite.mongodb.export.patient.application.command.ExportPatientCommand;
import com.custodix.insite.mongodb.export.patient.domain.repository.PatientNamespaceRepository;
import com.custodix.insite.mongodb.export.patient.domain.repository.PatientRepository;
import com.custodix.insite.mongodb.export.patient.domain.repository.SubjectMigrationRepository;
import com.custodix.insite.mongodb.export.patient.domain.service.ExportPatientRunner;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.clinicalfinding.ClinicalFindingExportConfiguration;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.laboratory.LaboratoryExportConfiguration;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.medication.MedicationExportConfiguration;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary.ObservationSummaryCounter;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary.ObservationSummaryWriter;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patient.PatientExportConfiguration;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.removepatientdata.RemovePatientDataConfiguration;
import com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse.*;
import com.custodix.insite.mongodb.export.patient.infrastructure.embedded.EmbeddedConceptConfiguration;
import com.custodix.insite.mongodb.export.patient.infrastructure.scheduling.MongoMigratorTimeConfiguration;
import com.custodix.insite.mongodb.export.patient.main.MongoIndexConfiguration;
import com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration;
import com.custodix.insite.mongodb.export.patient.main.SubjectMigrationConfiguration;

@Configuration
@Import({ MongoMigratorBatchConfiguration.class,
		  RemovePatientDataConfiguration.class,
		  PatientExportConfiguration.class,
		  LaboratoryExportConfiguration.class,
		  EmbeddedConceptConfiguration.class,
		  ClinicalFindingExportConfiguration.class,
		  MedicationExportConfiguration.class,
		  MongoMigratorTimeConfiguration.class,
		  MongoIndexConfiguration.class,
		  SubjectMigrationConfiguration.class })
@EnableConfigurationProperties({ JdbcProperties.class,
								 ModifierSettings.class,
								 ConceptCategorySettingsConfiguration.class })
public class ExportPatientRunnerConfiguration {

	@Bean
	@ConditionalOnMissingBean(JdbcTemplate.class)
	public JdbcTemplate jdbcTemplate(DataSource dataSource, JdbcProperties properties) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		JdbcProperties.Template template = properties.getTemplate();
		jdbcTemplate.setFetchSize(template.getFetchSize());
		jdbcTemplate.setMaxRows(template.getMaxRows());
		if (template.getQueryTimeout() != null) {
			jdbcTemplate.setQueryTimeout((int) template.getQueryTimeout()
					.getSeconds());
		}
		return jdbcTemplate;
	}

	@Bean
	public DataWarehouseConceptPathRepository conceptPathRepository(JdbcTemplate jdbcTemplate) {
		return new DataWarehouseConceptPathRepository(jdbcTemplate);
	}

	@Bean
	public DataWarehouseModifierRepository modifierRepository(JdbcTemplate jdbcTemplate,
			ModifierSettings modifierSettings) {
		return new DataWarehouseModifierRepository(jdbcTemplate, modifierSettings);
	}

	@Bean
	public PatientRepository patientRepository(JdbcTemplate jdbcTemplate) {
		return new DataWarehousePatientRepository(jdbcTemplate);
	}

	@Bean
	public PatientNamespaceRepository patientNamespaceRepository(JdbcTemplate jdbcTemplate) {
		return new DataWarehousePatientNamespaceRepository(jdbcTemplate);
	}

	@Bean
	public ExportPatientRunner exportPatientRunner(
			@Qualifier(JOB_BUILDER_FACTORY_MONGO_MIGRATOR) JobBuilderFactory jobBuilderFactory,
			@Qualifier(JOB_LAUNCHER_MONGO_MIGRATOR) JobLauncher jobLauncher,
			List<ExportStepFactory> exportStepFactories, PatientRepository patientRepository,
			ObservationSummaryWriter observationSummaryWriter) {
		return new BatchExportPatientRunner(jobBuilderFactory, jobLauncher, exportStepFactories, patientRepository,
				observationSummaryWriter);
	}

	@Bean
	public ExportPatient exportPatient(ExportPatientRunner exportPatientRunner,
			SubjectMigrationRepository subjectMigrationRepository,
			@Value("${export.patient.maximum.running.period.in.seconds:14400}") long maxRunningPeriodInSeconds) {
		return new ExportPatientCommand(subjectMigrationRepository, exportPatientRunner, maxRunningPeriodInSeconds);
	}

	@Bean
	ObservationSummaryCounter observationSummaryCounter() {
		return new ObservationSummaryCounter();
	}

	@Bean
	ObservationSummaryWriter observationSummaryWriter(ObservationSummaryCounter observationSummaryCounter,
			@Qualifier("mongoMigratorMongoTemplate") MongoTemplate mongoTemplate) {
		return new ObservationSummaryWriter(observationSummaryCounter, mongoTemplate);
	}
}