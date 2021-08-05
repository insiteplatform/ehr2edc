package com.custodix.insite.mongodb.export.patient.application.command;

import com.custodix.insite.mongodb.export.patient.application.api.ExportSubjects;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportsubjects.ExportSubjectsRunner;

public class ExportSubjectsCommand implements ExportSubjects {
	private final ExportSubjectsRunner exportSubjectsRunner;

	public ExportSubjectsCommand(ExportSubjectsRunner exportSubjectsRunner) {
		this.exportSubjectsRunner = exportSubjectsRunner;
	}

	@Override
	public void export() {
		exportSubjectsRunner.execute();
	}
}
