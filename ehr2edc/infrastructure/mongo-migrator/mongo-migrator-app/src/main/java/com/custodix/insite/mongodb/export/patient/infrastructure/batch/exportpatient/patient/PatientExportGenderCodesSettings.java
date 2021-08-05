package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patient;

import java.util.List;

public interface PatientExportGenderCodesSettings {
	List<String> getMaleCodes();

	List<String> getFemaleCodes();
}
