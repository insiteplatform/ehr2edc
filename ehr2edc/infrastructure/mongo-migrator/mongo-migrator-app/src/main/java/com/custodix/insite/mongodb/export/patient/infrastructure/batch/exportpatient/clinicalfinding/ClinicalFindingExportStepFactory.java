package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.clinicalfinding;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;

import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingFact;
import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingItem;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportStepFactory;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

class ClinicalFindingExportStepFactory implements ExportStepFactory {

	private final StepBuilderFactory stepBuilderFactory;
	private final ClinicalFindingItemReaderFactory itemReaderFactory;
	private final ItemProcessor<ClinicalFindingItem, ClinicalFindingFact> itemProcessor;
	private final int chunkSize;
	private final ItemWriter<ClinicalFindingFact> factItemWriter;

	ClinicalFindingExportStepFactory(StepBuilderFactory stepBuilderFactory,
			ClinicalFindingItemReaderFactory itemReaderFactory,
			ItemProcessor<ClinicalFindingItem, ClinicalFindingFact> itemProcessor,
			ItemWriter<ClinicalFindingFact> factItemWriter, int chunkSize) {
		this.stepBuilderFactory = stepBuilderFactory;
		this.itemReaderFactory = itemReaderFactory;
		this.itemProcessor = itemProcessor;
		this.factItemWriter = factItemWriter;
		this.chunkSize = chunkSize;
	}

	@Override
	public Step buildStep(PatientIdentifier patientIdentifier) {
		return stepBuilderFactory.get("importClinicalFacts").<ClinicalFindingItem, ClinicalFindingFact>chunk(
				chunkSize).reader(itemReaderFactory.buildItemReader(patientIdentifier))
				.processor(itemProcessor)
				.writer(factItemWriter)
				.build();
	}
}