package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.clinicalfinding;

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

import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingFact;
import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingItem;
import com.custodix.insite.mongodb.export.patient.domain.repository.ConceptPathRepository;
import com.custodix.insite.mongodb.export.patient.domain.repository.ModifierRepository;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportStepFactory;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary.ObservationSummarizer;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary.ObservationSummaryCounter;
import com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse.ConceptCategorySettings;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.VitalSignDocumentRepository;

@Configuration
@EnableConfigurationProperties(ClinicalFindingExportSettings.class)
public class ClinicalFindingExportConfiguration {
	@Bean
	ClinicalFindingItemReaderFactory clinicalFindingItemReaderFactory(DataSource dataSource,
			ConceptCategorySettings conceptCategorySettings) {
		return new ClinicalFindingItemReaderFactory(dataSource, conceptCategorySettings);
	}

	@Bean
	public ItemProcessor<ClinicalFindingItem, ClinicalFindingFact> clinicalItemProcessor(
			ConceptPathRepository conceptPathRepository, ModifierRepository modifierRepository,
			ClinicalFindingExportSettings exportSettings) {
		return new ClinicalFindingItemProcessor(conceptPathRepository, modifierRepository, exportSettings);
	}

	@Bean
	public ItemWriter<ClinicalFindingFact> clinicalFindingFactWriter(
			VitalSignDocumentRepository vitalSignDocumentRepository,
			ObservationSummaryCounter observationSummaryCounter) {
		ItemWriter<ClinicalFindingFact> mongoWriter = new ClinicalFindingFactMongoWriter(vitalSignDocumentRepository);
		CompositeItemWriter<ClinicalFindingFact> compositeItemWriter = new CompositeItemWriter<>();
		compositeItemWriter.setDelegates(asList(mongoWriter, new ObservationSummarizer(observationSummaryCounter)));
		return compositeItemWriter;
	}

	@Bean
	public ExportStepFactory clinicalFindingExportStepFactory(
			@Qualifier(STEP_BUILDER_FACTORY_MONGO_MIGRATOR) StepBuilderFactory stepBuilderFactory,
			ClinicalFindingItemReaderFactory itemReaderFactory,
			ItemProcessor<ClinicalFindingItem, ClinicalFindingFact> itemProcessor,
			ItemWriter<ClinicalFindingFact> clinicalFindingFactItemWriter,
			@Value("${batch.exportpatient.factChunkSize:10000}") int chunkSize) {
		return new ClinicalFindingExportStepFactory(stepBuilderFactory, itemReaderFactory, itemProcessor,
				clinicalFindingFactItemWriter, chunkSize);
	}
}