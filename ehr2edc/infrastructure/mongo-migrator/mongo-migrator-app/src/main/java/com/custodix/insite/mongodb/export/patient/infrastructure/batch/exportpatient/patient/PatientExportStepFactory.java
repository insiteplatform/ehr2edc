package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patient;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

import com.custodix.insite.mongodb.export.patient.domain.model.PatientFact;
import com.custodix.insite.mongodb.export.patient.domain.model.PatientRecord;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportStepFactory;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

final class PatientExportStepFactory implements ExportStepFactory {
	private final StepBuilderFactory stepBuilderFactory;
	private final PatientItemReaderFactory patientItemReaderFactory;
	private final PatientRecordProcessor patientRecordProcessor;
	private final ItemWriter<PatientFact> patientFactItemWriter;

	PatientExportStepFactory(StepBuilderFactory stepBuilderFactory, PatientItemReaderFactory patientItemReaderFactory,
			PatientRecordProcessor patientRecordProcessor, ItemWriter<PatientFact> patientFactItemWriter) {
		this.stepBuilderFactory = stepBuilderFactory;
		this.patientItemReaderFactory = patientItemReaderFactory;
		this.patientRecordProcessor = patientRecordProcessor;
		this.patientFactItemWriter = patientFactItemWriter;
	}

	@Override
	public Step buildStep(PatientIdentifier patientIdentifier) {
		ItemReader<PatientRecord> reader = patientItemReaderFactory.buildItemReader(patientIdentifier);
		return stepBuilderFactory.get("importPatient").<PatientRecord, PatientFact>chunk(1).reader(reader)
				.processor(patientRecordProcessor)
				.writer(patientFactItemWriter)
				.build();
	}
}