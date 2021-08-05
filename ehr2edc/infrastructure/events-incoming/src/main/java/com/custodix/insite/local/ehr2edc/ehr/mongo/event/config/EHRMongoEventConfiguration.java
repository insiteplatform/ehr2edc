package com.custodix.insite.local.ehr2edc.ehr.mongo.event.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.ehr.mongo.command.FailSubjectRegistration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.StartSubjectRegistration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.SubjectMigration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.SucceedSubjectRegistration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.FailSubjectRegistrationController;
import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.StartSubjectRegistrationController;
import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.SubjectMigrationController;
import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.SucceedSubjectRegistrationController;
import com.custodix.insite.local.ehr2edc.ehr.mongo.event.handler.EHRAsyncEventHandler;

@Configuration
public class EHRMongoEventConfiguration {
	@Bean
	public SubjectMigrationController subjectMigrationController(SubjectMigration subjectMigration) {
		return new SubjectMigrationController(subjectMigration);
	}

	@Bean
	EHRAsyncEventHandler ehrAsyncEventHandler(SubjectMigrationController subjectMigrationController) {
		return new EHRAsyncEventHandler(subjectMigrationController);
	}

	@Bean
	public SucceedSubjectRegistrationController succeedSubjectRegistrationController(SucceedSubjectRegistration succeedSubjectRegistration) {
		return new SucceedSubjectRegistrationController(succeedSubjectRegistration);
	}

	@Bean
	public FailSubjectRegistrationController failSubjectRegistrationController(FailSubjectRegistration failSubjectRegistration) {
		return new FailSubjectRegistrationController(failSubjectRegistration);
	}

	@Bean
	public StartSubjectRegistrationController startSubjectRegistrationController(
			StartSubjectRegistration startSubjectRegistration) {
		return new StartSubjectRegistrationController(startSubjectRegistration);
	}
}
