package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.laboratory;

import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.MONGO_TEMPLATE_MONGO_MIGRATOR;
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
import org.springframework.data.mongodb.core.MongoTemplate;

import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryFact;
import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryObservationFactRecord;
import com.custodix.insite.mongodb.export.patient.domain.repository.ConceptPathRepository;
import com.custodix.insite.mongodb.export.patient.domain.repository.LaboratoryConceptInfoRepository;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportStepFactory;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary.ObservationSummarizer;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary.ObservationSummaryCounter;
import com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse.ConceptCategorySettings;

@Configuration
@EnableConfigurationProperties(LaboratoryExportSettings.class)
public class LaboratoryExportConfiguration {

	@Bean
	LaboratoryItemReaderFactory laboratoryItemReaderFactory(DataSource dataSource,
			ConceptCategorySettings conceptCategorySettings) {
		return new LaboratoryItemReaderFactory(dataSource, conceptCategorySettings);
	}

	@Bean
	public ItemProcessor<LaboratoryObservationFactRecord, LaboratoryFact> laboratoryItemProcessor(
			ConceptPathRepository conceptPathRepository,
			LaboratoryConceptInfoRepository laboratoryConceptInfoRepository,
			LaboratoryExportSettings laboratoryExportSettings) {
		return new LaboratoryObservationFactProcessor(conceptPathRepository, laboratoryConceptInfoRepository,
				laboratoryExportSettings);
	}

	@Bean
	public ItemWriter<LaboratoryFact> laboratoryFactWriter(
			@Qualifier(MONGO_TEMPLATE_MONGO_MIGRATOR) MongoTemplate mongoTemplate,
			ObservationSummaryCounter observationSummaryCounter) {
		CompositeItemWriter<LaboratoryFact> compositeItemWriter = new CompositeItemWriter<>();
		compositeItemWriter.setDelegates(asList(new LaboratoryFactMongoWriter(mongoTemplate),
				new ObservationSummarizer(observationSummaryCounter)));
		return compositeItemWriter;
	}

	@Bean
	public ExportStepFactory laboratoryExportStepFactory(
			@Qualifier(STEP_BUILDER_FACTORY_MONGO_MIGRATOR) StepBuilderFactory stepBuilderFactory,
			LaboratoryItemReaderFactory laboratoryItemReaderFactory,
			ItemProcessor<LaboratoryObservationFactRecord, LaboratoryFact> itemProcessor,
			ItemWriter<LaboratoryFact> laboratoryFactWriter,
			@Value("${batch.exportpatient.factChunkSize:10000}") int chunkSize) {
		return new LaboratoryExportStepFactory(stepBuilderFactory, laboratoryItemReaderFactory, itemProcessor,
				laboratoryFactWriter, chunkSize);
	}
}