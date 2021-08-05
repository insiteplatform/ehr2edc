package com.custodix.insite.local.ehr2edc.event.config;

import com.custodix.insite.local.ehr2edc.command.CreateSubjectInEDC;
import com.custodix.insite.local.ehr2edc.command.RecordSubjectRegistrationChange;
import com.custodix.insite.local.ehr2edc.command.UpdateEHRSubjectRegistrationStatus;
import com.custodix.insite.local.ehr2edc.ehr.epic.event.config.EHREpicEventConfiguration;
import com.custodix.insite.local.ehr2edc.ehr.fhir.event.config.EHRFhirEventConfiguration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.event.config.EHRMongoEventConfiguration;
import com.custodix.insite.local.ehr2edc.event.controller.DatawarehouseUpdatedEventHandler;
import com.custodix.insite.local.ehr2edc.event.controller.ExportPatientIdsController;
import com.custodix.insite.local.ehr2edc.event.controller.ExportSubjectsController;
import com.custodix.insite.local.ehr2edc.event.controller.SubjectActivationController;
import com.custodix.insite.local.ehr2edc.event.controller.SubjectCreatedEventController;
import com.custodix.insite.local.ehr2edc.event.controller.SubjectDeactivationController;
import com.custodix.insite.local.ehr2edc.event.controller.SubjectRegisteredEventHandler;
import com.custodix.insite.local.ehr2edc.event.controller.SubjectRegistrationController;
import com.custodix.insite.local.ehr2edc.event.controller.UpdateEHRSubjectRegistrationStatusController;
import com.custodix.insite.local.ehr2edc.event.handler.EHR2EDCAsyncEventHandler;
import com.custodix.insite.local.ehr2edc.event.handler.SubjectDeregisteredEventHandler;
import com.custodix.insite.mongodb.export.patient.application.api.ActivateSubject;
import com.custodix.insite.mongodb.export.patient.application.api.DeactivateSubject;
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatientSearchCriteriaInformation;
import com.custodix.insite.mongodb.export.patient.application.api.ExportSubjects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
		EHRMongoEventConfiguration.class,
		EHRFhirEventConfiguration.class,
		EHREpicEventConfiguration.class
})
public class EHR2EDCEventConfiguration {

	@Bean
	SubjectActivationController subjectActivationController(ActivateSubject activateSubject) {
		return new SubjectActivationController(activateSubject);
	}

	@Bean
	SubjectDeactivationController subjectDeactivationController(DeactivateSubject deactivateSubject) {
		return new SubjectDeactivationController(deactivateSubject);
	}

	@Bean
	SubjectRegistrationController subjectRegistrationController(
			RecordSubjectRegistrationChange recordSubjectRegistrationChange) {
		return new SubjectRegistrationController(recordSubjectRegistrationChange);
	}

	@Bean
	SubjectRegisteredEventHandler subjectRegisteredEventHandler(
			SubjectRegistrationController subjectRegistrationController,
			SubjectActivationController subjectActivationController) {
		return new SubjectRegisteredEventHandler(subjectRegistrationController, subjectActivationController);
	}

	@Bean
	SubjectDeregisteredEventHandler subjectDeregisteredEventHandler(
			SubjectRegistrationController subjectRegistrationController,
			SubjectDeactivationController subjectDeactivationController) {
		return new SubjectDeregisteredEventHandler(subjectRegistrationController, subjectDeactivationController);
	}

	@Bean
	SubjectCreatedEventController subjectCreatedEventController(CreateSubjectInEDC createSubjectInEDC) {
		return new SubjectCreatedEventController(createSubjectInEDC);
	}

	@Bean
	ExportPatientIdsController exportPatientIdsController(
			ExportPatientSearchCriteriaInformation exportPatientSearchCriteriaInformation) {
		return new ExportPatientIdsController(exportPatientSearchCriteriaInformation);
	}

	@Bean
	ExportSubjectsController exportSubjectsController(ExportSubjects exportSubjects) {
		return new ExportSubjectsController(exportSubjects);
	}

	@Bean
	DatawarehouseUpdatedEventHandler datawarehouseUpdatedEventController(
			ExportPatientIdsController exportPatientIdsController, ExportSubjectsController exportSubjectsController) {
		return new DatawarehouseUpdatedEventHandler(exportPatientIdsController, exportSubjectsController);
	}

	@Bean
	EHR2EDCAsyncEventHandler ehr2edcAsyncEventHandler(
			SubjectCreatedEventController subjectCreatedEventController,
			DatawarehouseUpdatedEventHandler datawarehouseUpdatedEventHandler,
			UpdateEHRSubjectRegistrationStatusController updateEHRSubjectRegistrationStatusController) {
		return new EHR2EDCAsyncEventHandler(subjectCreatedEventController,
				datawarehouseUpdatedEventHandler, updateEHRSubjectRegistrationStatusController);
	}

	@Bean
	UpdateEHRSubjectRegistrationStatusController updateEHRSubjectRegistrationStatusController(
			UpdateEHRSubjectRegistrationStatus updateEHRSubjectRegistrationStatus) {
		return new UpdateEHRSubjectRegistrationStatusController(updateEHRSubjectRegistrationStatus);
	}
}
