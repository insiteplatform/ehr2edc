package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient;

import org.springframework.batch.core.Step;

import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public interface ExportStepFactory {
	Step buildStep(PatientIdentifier patientIdentifier);

}