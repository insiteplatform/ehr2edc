package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.medication;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;

import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationFact;
import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationItem;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportStepFactory;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

class MedicationExportStepFactory implements ExportStepFactory {
	private final StepBuilderFactory stepBuilderFactory;
	private final MedicationItemReaderFactory medicationItemReaderFactory;
	private final ItemProcessor<MedicationItem, MedicationFact> factItemProcessor;
	private final ItemWriter<MedicationFact> factItemWriter;
	private final int chunkSize;

	MedicationExportStepFactory(StepBuilderFactory stepBuilderFactory,
			MedicationItemReaderFactory medicationItemReaderFactory,
			ItemProcessor<MedicationItem, MedicationFact> factItemProcessor, ItemWriter<MedicationFact> factItemWriter,
			int chunkSize) {
		this.stepBuilderFactory = stepBuilderFactory;
		this.medicationItemReaderFactory = medicationItemReaderFactory;
		this.factItemProcessor = factItemProcessor;
		this.factItemWriter = factItemWriter;
		this.chunkSize = chunkSize;
	}

	@Override
	public Step buildStep(PatientIdentifier patientIdentifier) {
		return stepBuilderFactory.get("importMedicationFacts").<MedicationItem, MedicationFact>chunk(chunkSize).reader(
				medicationItemReaderFactory.buildItemReader(patientIdentifier))
				.processor(factItemProcessor)
				.writer(factItemWriter)
				.build();
	}
}