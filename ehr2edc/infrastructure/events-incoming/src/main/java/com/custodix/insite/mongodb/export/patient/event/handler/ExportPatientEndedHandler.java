package com.custodix.insite.mongodb.export.patient.event.handler;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.context.event.EventListener;

import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.SucceedSubjectRegistrationController;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientEnded;
import com.custodix.insite.mongodb.export.patient.event.controller.EndSubjectMigrationController;

public class ExportPatientEndedHandler {

	private final List<Consumer<ExportPatientEnded>> exportPatientEndedConsumers;

	public ExportPatientEndedHandler(EndSubjectMigrationController endSubjectMigrationController,
			SucceedSubjectRegistrationController succeedSubjectRegistrationController) {
		this.exportPatientEndedConsumers = asList(
				endSubjectMigrationController::endSubjectMigration,
				succeedSubjectRegistrationController::succeed);
	}

	@EventListener
	public void handle(final ExportPatientEnded event) {
		exportPatientEndedConsumers.stream()
				.forEach(consumer ->consumer.accept(event));
	}

}
