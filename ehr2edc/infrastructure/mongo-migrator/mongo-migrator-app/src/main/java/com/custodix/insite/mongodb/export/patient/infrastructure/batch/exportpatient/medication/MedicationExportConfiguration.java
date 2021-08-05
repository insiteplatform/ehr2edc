package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.medication;

import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.STEP_BUILDER_FACTORY_MONGO_MIGRATOR;
import static java.util.Arrays.asList;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationFact;
import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationItem;
import com.custodix.insite.mongodb.export.patient.domain.repository.ConceptPathRepository;
import com.custodix.insite.mongodb.export.patient.domain.repository.ModifierRepository;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportStepFactory;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary.ObservationSummarizer;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary.ObservationSummaryCounter;
import com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse.ConceptCategorySettings;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.MedicationDocumentRepository;

@Configuration
@EnableConfigurationProperties(MedicationExportSettings.class)
public class MedicationExportConfiguration {

	@Bean
	MedicationItemReaderFactory medicationItemReaderFactory(DataSource dataSource,
			ConceptCategorySettings conceptCategorySettings) {
		return new MedicationItemReaderFactory(dataSource, conceptCategorySettings);
	}

	@Bean
	ItemProcessor<MedicationItem, MedicationFact> medicationItemProcessor(ConceptPathRepository conceptPathRepository,
			MedicationExportSettings medicationExportSettings, ModifierRepository modifierRepository) {
		return new MedicationFactItemProcessor(conceptPathRepository, medicationExportSettings, modifierRepository);
	}

	@Bean
	ItemWriter<MedicationFact> medicationFactWriter(MedicationDocumentRepository medicationDocumentRepository,
			ObservationSummaryCounter observationSummaryCounter) {
		CompositeItemWriter<MedicationFact> compositeItemWriter = new CompositeItemWriter<>();
		compositeItemWriter.setDelegates(asList(new MedicationFactMongoWriter(medicationDocumentRepository),
				new ObservationSummarizer(observationSummaryCounter)));
		return compositeItemWriter;
	}

	@Bean
	public ExportStepFactory medicationExportStepFactory(
			@Qualifier(STEP_BUILDER_FACTORY_MONGO_MIGRATOR) StepBuilderFactory stepBuilderFactory,
			MedicationItemReaderFactory medicationItemReaderFactory,
			ItemProcessor<MedicationItem, MedicationFact> medicationItemProcessor,
			ItemWriter<MedicationFact> factItemWriter,
			@Value("${batch.exportpatient.factChunkSize:10000}") int chunkSize) {
		return new MedicationExportStepFactory(stepBuilderFactory, medicationItemReaderFactory, medicationItemProcessor,
				factItemWriter, chunkSize);
	}
}