package com.custodix.insite.local.ehr2edc.query.fhir.patient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2ClientFactory;

@Configuration
public class FhirDstu2PatientConfiguration {

	@Bean
	FhirDstu2PatientRepository fhirDstu2PatientRepository() {
		return new FhirDstu2PatientRepository();
	}

	@Bean
	FhirDstu2PatientGatewayFactory fhirDstu2PatientGatewayFactory(FhirDstu2ClientFactory fhirDstu2ClientFactory,
			FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		return new FhirDstu2PatientGatewayFactory(fhirDstu2ClientFactory, fhirDstu2PatientRepository);
	}
}
