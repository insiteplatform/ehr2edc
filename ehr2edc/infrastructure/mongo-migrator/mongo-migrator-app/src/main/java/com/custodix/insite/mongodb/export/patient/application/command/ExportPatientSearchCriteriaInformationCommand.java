package com.custodix.insite.mongodb.export.patient.application.command;

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatientSearchCriteriaInformation;
import com.custodix.insite.mongodb.export.patient.domain.service.ExportPatientSearchCriteriaInformationRunner;

public class ExportPatientSearchCriteriaInformationCommand implements ExportPatientSearchCriteriaInformation {
	private final ExportPatientSearchCriteriaInformationRunner exportPatientSearchCriteriaInformationRunner;

	public ExportPatientSearchCriteriaInformationCommand(
			ExportPatientSearchCriteriaInformationRunner exportPatientSearchCriteriaInformationRunner) {
		this.exportPatientSearchCriteriaInformationRunner = exportPatientSearchCriteriaInformationRunner;
	}

	@Override
	public void export() {
		exportPatientSearchCriteriaInformationRunner.run();
	}
}
