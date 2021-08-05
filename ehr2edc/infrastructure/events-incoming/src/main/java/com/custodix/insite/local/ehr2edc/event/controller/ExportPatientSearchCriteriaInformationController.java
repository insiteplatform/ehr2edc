package com.custodix.insite.local.ehr2edc.event.controller;

import com.custodix.insite.local.ehr2edc.events.DatawarehouseUpdatedEvent;
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatientSearchCriteriaInformation;

public class ExportPatientSearchCriteriaInformationController {
	private final ExportPatientSearchCriteriaInformation exportPatientSearchCriteriaInformation;

	public ExportPatientSearchCriteriaInformationController(
			ExportPatientSearchCriteriaInformation exportPatientSearchCriteriaInformation) {
		this.exportPatientSearchCriteriaInformation = exportPatientSearchCriteriaInformation;
	}

	void handle(DatawarehouseUpdatedEvent event) {
		exportPatientSearchCriteriaInformation.export();
	}
}
