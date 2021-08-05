package com.custodix.insite.mongodb.export.patient.domain.repository;

import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public interface PatientRepository {
	boolean patientExists(PatientIdentifier patientIdentifier);
}