package com.custodix.insite.local.ehr2edc.ehr.epic.event.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.ehr.epic.command.PatientRegistration;
import com.custodix.insite.local.ehr2edc.ehr.epic.event.controller.PatientRegistrationController;

@Configuration
public class EHREpicEventConfiguration {
	@Bean
	PatientRegistrationController patientRegistrationController(PatientRegistration patientRegistration) {
		return new PatientRegistrationController(patientRegistration);
	}
}
