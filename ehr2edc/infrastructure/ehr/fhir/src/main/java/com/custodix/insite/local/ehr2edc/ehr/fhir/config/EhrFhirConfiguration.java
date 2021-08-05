package com.custodix.insite.local.ehr2edc.ehr.fhir.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.ehr.fhir.command.FhirSubjectRegistration;
import com.custodix.insite.local.ehr2edc.ehr.fhir.command.impl.FhirSubjectRegistrationCommand;
import com.custodix.insite.local.ehr2edc.query.patient.PatientEHRGatewayFactory;

@Configuration
public class EhrFhirConfiguration {
	@Bean
	public FhirSubjectRegistration fhirSubjectRegistration(final PatientEHRGatewayFactory patientEHRGatewayFactory) {
		return new FhirSubjectRegistrationCommand(patientEHRGatewayFactory);
	}
}
