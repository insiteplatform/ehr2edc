package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.removepatientdata;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;

import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportStepFactory;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public class RemovePatientDataStepFactory implements ExportStepFactory {
	public static final String REMOVE_PATIENT_DATA_STEP = "removePatientData";

	private final StepBuilderFactory stepBuilderFactory;
	private final RemovePatientDataTasklet removePatientDataTasklet;

	public RemovePatientDataStepFactory(final StepBuilderFactory stepBuilderFactory, final RemovePatientDataTasklet removePatientDataTasklet) {
		this.stepBuilderFactory = stepBuilderFactory;
		this.removePatientDataTasklet = removePatientDataTasklet;
	}

	@Override
	public Step buildStep(final PatientIdentifier patientIdentifier) {
		return stepBuilderFactory.get(REMOVE_PATIENT_DATA_STEP).tasklet(removePatientDataTasklet)
				.build();
	}
}
