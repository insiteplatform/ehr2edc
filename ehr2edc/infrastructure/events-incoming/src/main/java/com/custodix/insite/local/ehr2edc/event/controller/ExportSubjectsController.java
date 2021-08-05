package com.custodix.insite.local.ehr2edc.event.controller;

import com.custodix.insite.local.ehr2edc.events.DatawarehouseUpdatedEvent;
import com.custodix.insite.mongodb.export.patient.application.api.ExportSubjects;

public class ExportSubjectsController {
	private final ExportSubjects exportSubjects;

	public ExportSubjectsController(ExportSubjects exportSubjects) {
		this.exportSubjects = exportSubjects;
	}

	void handle(DatawarehouseUpdatedEvent event) {
		exportSubjects.export();
	}
}
