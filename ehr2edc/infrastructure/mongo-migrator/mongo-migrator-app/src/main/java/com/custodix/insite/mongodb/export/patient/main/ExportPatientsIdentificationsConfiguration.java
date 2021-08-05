package com.custodix.insite.mongodb.export.patient.main;

import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.JOB_BUILDER_FACTORY_MONGO_MIGRATOR;
import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.JOB_LAUNCHER_MONGO_MIGRATOR;
import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.MONGO_TEMPLATE_MONGO_MIGRATOR;
import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.STEP_BUILDER_FACTORY_MONGO_MIGRATOR;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatientSearchCriteriaInformation;
import com.custodix.insite.mongodb.export.patient.application.command.ExportPatientSearchCriteriaInformationCommand;
import com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation.PatientSearchCriteriaInformation;
import com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation.PatientSearchCriteriaInformationRecord;
import com.custodix.insite.mongodb.export.patient.domain.service.ExportPatientSearchCriteriaInformationRunner;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patientsearchcriteriainformation.*;

@Import({ MongoMigratorBatchConfiguration.class })
@Configuration
public class ExportPatientsIdentificationsConfiguration {
	@Bean
	ExportPatientSearchCriteriaInformation exportPatientsIds(
			ExportPatientSearchCriteriaInformationRunner exportPatientsIdsRunner) {
		return new ExportPatientSearchCriteriaInformationCommand(exportPatientsIdsRunner);
	}

	@Bean
	ExportPatientSearchCriteriaInformationRunner exportPatientsIdsRunner(
			@Qualifier(JOB_BUILDER_FACTORY_MONGO_MIGRATOR) JobBuilderFactory jobBuilderFactory,
			@Qualifier(JOB_LAUNCHER_MONGO_MIGRATOR) JobLauncher jobLauncher,
			PatientSearchCriteriaInformationStepFactory patientSearchCriteriaInformationStepFactory) {
		return new BatchExportPatientSearchCriteriaInformationRunner(jobBuilderFactory, jobLauncher,
				patientSearchCriteriaInformationStepFactory);
	}

	@Bean
	PatientSearchCriteriaInformationStepFactory patientsIdsStepFactory(
			@Qualifier(STEP_BUILDER_FACTORY_MONGO_MIGRATOR) StepBuilderFactory stepBuilderFactory,
			PatientSearchCriteriaInformationReaderFactory patientSearchCriteriaInformationReaderFactory,
			ItemProcessor<PatientSearchCriteriaInformationRecord, PatientSearchCriteriaInformation> patientIdRecordProcessor,
			ItemWriter<PatientSearchCriteriaInformation> patientIdentificationItemWriter) {
		return new PatientSearchCriteriaInformationStepFactory(stepBuilderFactory, patientSearchCriteriaInformationReaderFactory, patientIdRecordProcessor,
				patientIdentificationItemWriter);
	}

	@Bean
	PatientSearchCriteriaInformationReaderFactory patientsIdsReaderFactory(DataSource dataSource) {
		return new PatientSearchCriteriaInformationReaderFactory(dataSource);
	}

	@Bean
	ItemProcessor<PatientSearchCriteriaInformationRecord, PatientSearchCriteriaInformation> patientIdRecordProcessor() {
		return new PatientSearchCriteriaInformationRecordProcessor();
	}

	@Bean
	ItemWriter<PatientSearchCriteriaInformation> patientIdentificationItemWriter(
			@Qualifier(MONGO_TEMPLATE_MONGO_MIGRATOR) MongoTemplate mongoTemplate) {
		return new PatientSearchCriteriaInformationMongoWriter(mongoTemplate);
	}
}
