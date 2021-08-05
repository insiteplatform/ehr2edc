package com.custodix.insite.mongodb.export.patient.event.handler;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.context.event.EventListener;

import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.StartSubjectRegistrationController;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientStarting;
import com.custodix.insite.mongodb.export.patient.event.controller.StartSubjectMigrationController;

public class ExportPatientStartingHandler {

	private final List<Consumer<ExportPatientStarting>> exportPatientStartedConsumers;

	public ExportPatientStartingHandler(StartSubjectMigrationController startSubjectMigrationController,
			StartSubjectRegistrationController startSubjectRegistrationController) {
		this.exportPatientStartedConsumers = asList(
				startSubjectMigrationController::startSubjectMigration,
				startSubjectRegistrationController::start);
	}

	@EventListener
	public void handle(final ExportPatientStarting event) {
		exportPatientStartedConsumers.stream()
				.forEach(consumer ->consumer.accept(event));
	}

}
