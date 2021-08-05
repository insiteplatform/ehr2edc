package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.laboratory;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryFact;
import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryObservationFactRecord;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportStepFactory;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

class LaboratoryExportStepFactory implements ExportStepFactory {

	private final StepBuilderFactory stepBuilderFactory;
	private final LaboratoryItemReaderFactory laboratoryItemReaderFactory;
	private final ItemProcessor<LaboratoryObservationFactRecord, LaboratoryFact> itemProcessor;
	private final ItemWriter<LaboratoryFact> itemWriter;
	private final int chunkSize;

	LaboratoryExportStepFactory(StepBuilderFactory stepBuilderFactory,
			LaboratoryItemReaderFactory laboratoryItemReaderFactory,
			ItemProcessor<LaboratoryObservationFactRecord, LaboratoryFact> itemProcessor,
			ItemWriter<LaboratoryFact> itemWriter, int chunkSize) {
		this.stepBuilderFactory = stepBuilderFactory;
		this.laboratoryItemReaderFactory = laboratoryItemReaderFactory;
		this.itemProcessor = itemProcessor;
		this.itemWriter = itemWriter;
		this.chunkSize = chunkSize;
	}

	@Override
	public Step buildStep(PatientIdentifier patientIdentifier) {
		ItemReader<LaboratoryObservationFactRecord> itemReader = laboratoryItemReaderFactory.buildItemReader(
				patientIdentifier);
		return stepBuilderFactory.get("importLaboratoryFacts").<LaboratoryObservationFactRecord, LaboratoryFact>chunk(
				chunkSize).reader(
				itemReader)
				.processor(itemProcessor)
				.writer(itemWriter)
				.build();
	}
}