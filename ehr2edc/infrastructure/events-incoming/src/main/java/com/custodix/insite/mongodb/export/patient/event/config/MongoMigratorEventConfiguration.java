package com.custodix.insite.mongodb.export.patient.event.config;

import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.FailSubjectRegistrationController;
import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.StartSubjectRegistrationController;
import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.SucceedSubjectRegistrationController;
import com.custodix.insite.mongodb.export.patient.application.api.EndSubjectMigration;
import com.custodix.insite.mongodb.export.patient.application.api.FailSubjectMigration;
import com.custodix.insite.mongodb.export.patient.application.api.StartSubjectMigration;
import com.custodix.insite.mongodb.export.patient.event.controller.EndSubjectMigrationController;
import com.custodix.insite.mongodb.export.patient.event.controller.FailSubjectMigrationController;
import com.custodix.insite.mongodb.export.patient.event.controller.StartSubjectMigrationController;
import com.custodix.insite.mongodb.export.patient.event.handler.ExportPatientEndedHandler;
import com.custodix.insite.mongodb.export.patient.event.handler.ExportPatientFailedHandler;
import com.custodix.insite.mongodb.export.patient.event.handler.ExportPatientStartingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoMigratorEventConfiguration {

	@Bean
	public StartSubjectMigrationController startSubjectMigrationController(StartSubjectMigration startSubjectMigration) {
		return new StartSubjectMigrationController(startSubjectMigration);
	}

	@Bean
	public EndSubjectMigrationController endSubjectMigrationController(EndSubjectMigration endSubjectMigration) {
		return new EndSubjectMigrationController(endSubjectMigration);
	}

	@Bean
	public FailSubjectMigrationController failSubjectMigrationController(FailSubjectMigration failSubjectMigration) {
		return new FailSubjectMigrationController(failSubjectMigration);
	}

	@Bean
	public ExportPatientEndedHandler exportPatientEndedHandler(EndSubjectMigrationController endSubjectMigrationController,
			SucceedSubjectRegistrationController succeedSubjectRegistrationController) {
		return new ExportPatientEndedHandler(endSubjectMigrationController, succeedSubjectRegistrationController);
	}

	@Bean
	public ExportPatientFailedHandler exportPatientFailedHandler(FailSubjectMigrationController failSubjectMigrationController,
			FailSubjectRegistrationController updateSubjectRegistrationController) {
		return new ExportPatientFailedHandler(failSubjectMigrationController, updateSubjectRegistrationController);
	}

	@Bean
	public ExportPatientStartingHandler exportPatientStartingHandler(StartSubjectMigrationController startSubjectMigrationController,
			StartSubjectRegistrationController startSubjectRegistrationController) {
		return new ExportPatientStartingHandler(startSubjectMigrationController, startSubjectRegistrationController);
	}
}
