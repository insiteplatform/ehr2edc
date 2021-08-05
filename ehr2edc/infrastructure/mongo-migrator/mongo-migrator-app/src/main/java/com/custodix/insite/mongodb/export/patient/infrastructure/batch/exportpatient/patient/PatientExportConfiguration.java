package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patient;

import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.MONGO_TEMPLATE_MONGO_MIGRATOR;
import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.STEP_BUILDER_FACTORY_MONGO_MIGRATOR;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.custodix.insite.mongodb.export.patient.domain.model.PatientFact;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportStepFactory;

@Configuration
@EnableConfigurationProperties(PatientExportGenderCodesSettingsProperties.class)
public class PatientExportConfiguration {

    @Bean
    PatientItemReaderFactory patientItemReaderFactory(DataSource dataSource) {
        return new PatientItemReaderFactory(dataSource);
    }

    @Bean
    PatientRecordProcessor patientRecordProcessor(PatientExportGenderCodesSettings patientExportGenderCodesSettings) {
        return new PatientRecordProcessor(patientExportGenderCodesSettings);
    }

    @Bean
	public PatientFactMongoWriter patientFactWriter(@Qualifier(MONGO_TEMPLATE_MONGO_MIGRATOR) MongoTemplate mongoTemplate) {
		return new PatientFactMongoWriter(mongoTemplate);
    }

    @Bean
    ExportStepFactory patientExportStepFactory(
            @Qualifier(STEP_BUILDER_FACTORY_MONGO_MIGRATOR) StepBuilderFactory stepBuilderFactory,
            PatientItemReaderFactory patientItemReaderFactory, PatientRecordProcessor patientRecordProcessor,
            ItemWriter<PatientFact> itemWriter) {
        return new PatientExportStepFactory(stepBuilderFactory, patientItemReaderFactory, patientRecordProcessor,
                itemWriter);
    }
}