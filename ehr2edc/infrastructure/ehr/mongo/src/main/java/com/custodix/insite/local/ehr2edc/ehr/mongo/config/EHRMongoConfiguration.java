package com.custodix.insite.local.ehr2edc.ehr.mongo.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository;
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.FailSubjectRegistration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.StartSubjectRegistration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.SubjectMigration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.SucceedSubjectRegistration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.gateway.ExportPatientGateway;
import com.custodix.insite.local.ehr2edc.ehr.mongo.gateway.ExportPatientGatewayImpl;
import com.custodix.insite.local.ehr2edc.ehr.mongo.usecase.FailSubjectRegistrationCommand;
import com.custodix.insite.local.ehr2edc.ehr.mongo.usecase.StartSubjectRegistrationCommand;
import com.custodix.insite.local.ehr2edc.ehr.mongo.usecase.SubjectMigrationCommand;
import com.custodix.insite.local.ehr2edc.ehr.mongo.usecase.SucceedSubjectRegistrationCommand;
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient;

@Configuration
@ImportAutoConfiguration({ ValidationAutoConfiguration.class})
public class EHRMongoConfiguration {

	@Bean
	public SubjectMigration subjectMigration(ExportPatientGateway exportPatientGateway,
			EHRConnectionRepository ehrConnectionRepository) {
		return new SubjectMigrationCommand(exportPatientGateway, ehrConnectionRepository);
	}

	@Bean
	public ExportPatientGateway exportPatientGateway(ExportPatient exportPatient) {
		return new ExportPatientGatewayImpl(exportPatient);
	}

	@Bean
	public SucceedSubjectRegistration succeedSubjectRegistration() {
		return new SucceedSubjectRegistrationCommand();
	}

	@Bean
	public FailSubjectRegistration failSubjectRegistration() {
		return new FailSubjectRegistrationCommand();
	}

	@Bean
	public StartSubjectRegistration startSubjectRegistration() {
		return new StartSubjectRegistrationCommand();
	}
}
