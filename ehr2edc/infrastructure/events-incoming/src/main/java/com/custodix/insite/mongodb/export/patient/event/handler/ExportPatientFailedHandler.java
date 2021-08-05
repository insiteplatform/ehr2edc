package com.custodix.insite.mongodb.export.patient.event.handler;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.context.event.EventListener;

import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.FailSubjectRegistrationController;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientFailed;
import com.custodix.insite.mongodb.export.patient.event.controller.FailSubjectMigrationController;

public class ExportPatientFailedHandler {
	private final List<Consumer<ExportPatientFailed>> exportPatientFailedConsumers;

	public ExportPatientFailedHandler(FailSubjectMigrationController failSubjectMigrationController,
			FailSubjectRegistrationController updateSubjectRegistrationController) {
		this.exportPatientFailedConsumers = Arrays.asList(
				failSubjectMigrationController::failSubjectMigration,
				updateSubjectRegistrationController::fail
		);
	}

	@EventListener
	public void handle(final ExportPatientFailed exportPatientFailed) {
		exportPatientFailedConsumers.stream()
				.forEach(consumer ->consumer.accept(exportPatientFailed));
	}
}
