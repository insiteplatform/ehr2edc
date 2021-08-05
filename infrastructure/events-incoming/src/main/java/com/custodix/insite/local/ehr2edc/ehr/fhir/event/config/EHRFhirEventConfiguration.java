package com.custodix.insite.local.ehr2edc.ehr.fhir.event.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.ehr.fhir.command.FhirSubjectRegistration;
import com.custodix.insite.local.ehr2edc.ehr.fhir.event.controller.FhirSubjectRegistrationController;

@Configuration
public class EHRFhirEventConfiguration {
	@Bean
	public FhirSubjectRegistrationController fhirSubjectRegistrationController(FhirSubjectRegistration fhirSubjectRegistration) {
		return new FhirSubjectRegistrationController(fhirSubjectRegistration);
	}
}
