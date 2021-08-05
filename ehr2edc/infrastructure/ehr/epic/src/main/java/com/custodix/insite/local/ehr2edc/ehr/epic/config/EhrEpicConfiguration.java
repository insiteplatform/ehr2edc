package com.custodix.insite.local.ehr2edc.ehr.epic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.ehr.epic.command.PatientRegistration;
import com.custodix.insite.local.ehr2edc.ehr.epic.command.impl.PatientRegistrationEmptyImpl;

@Configuration
public class EhrEpicConfiguration {
	@Bean
	public PatientRegistration patientRegistration() {
		return new PatientRegistrationEmptyImpl();
	}
}