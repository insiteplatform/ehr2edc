package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patientsearchcriteriainformation;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

import com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation.PatientSearchCriteriaInformation;
import com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation.PatientSearchCriteriaInformationRecord;

public class PatientSearchCriteriaInformationStepFactory {
	private final StepBuilderFactory stepBuilderFactory;
	private final PatientSearchCriteriaInformationReaderFactory patientSearchCriteriaInformationReaderFactory;
	private final ItemProcessor<PatientSearchCriteriaInformationRecord, PatientSearchCriteriaInformation> itemProcessor;
	private final ItemWriter<PatientSearchCriteriaInformation> itemWriter;

	public PatientSearchCriteriaInformationStepFactory(StepBuilderFactory stepBuilderFactory,
			PatientSearchCriteriaInformationReaderFactory patientSearchCriteriaInformationReaderFactory,
			ItemProcessor<PatientSearchCriteriaInformationRecord,
			PatientSearchCriteriaInformation> itemProcessor,
			ItemWriter<PatientSearchCriteriaInformation> itemWriter) {
		this.stepBuilderFactory = stepBuilderFactory;
		this.patientSearchCriteriaInformationReaderFactory = patientSearchCriteriaInformationReaderFactory;
		this.itemProcessor = itemProcessor;
		this.itemWriter = itemWriter;
	}

	public Step buildStep() {
		ItemReader<PatientSearchCriteriaInformationRecord> reader = patientSearchCriteriaInformationReaderFactory.buildItemReader();
		return stepBuilderFactory.get("exportPatientSearchCriteriaInformation").<PatientSearchCriteriaInformationRecord, PatientSearchCriteriaInformation>chunk(10000)
				.reader(reader)
				.processor(itemProcessor)
				.writer(itemWriter)
				.build();
	}
}
