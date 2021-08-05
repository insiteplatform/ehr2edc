package com.custodix.insite.mongodb.export.patient.domain.service;

import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public interface ExportPatientRunner {
	void run(PatientIdentifier patientIdentifier);
}
