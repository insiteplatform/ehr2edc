package com.custodix.insite.local.ehr2edc.query.fhir.demographic;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2ClientFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientRepository;

@Configuration
public class FhirDstu2DemographicConfiguration {

	@Bean
	FhirDstu2DemographicFactoryForGender fhirDstu2DemographicFactoryForGender() {
		return new FhirDstu2DemographicFactoryForGender();
	}

	@Bean
	FhirDstu2DemographicFactoryForBirthDate fhirDstu2DemographicFactoryForBirthDate() {
		return new FhirDstu2DemographicFactoryForBirthDate();
	}

	@Bean
	FhirDstu2DemographicFactoryForDeceasedDate fhirDstu2DemographicFactoryForDeceasedDate() {
		return new FhirDstu2DemographicFactoryForDeceasedDate();
	}

	@Bean
	FhirDstu2DemographicFactoryForVitalStatus fhirDstu2DemographicFactoryForVitalStatus() {
		return new FhirDstu2DemographicFactoryForVitalStatus();
	}

	@Bean
	FhirDstu2DemographicGatewayFactory fhirDstu2DemographicGatewayFactory(
			List<FhirDstu2DemographicFactory> fhirDstu2DemographicFactories,
			FhirDstu2ClientFactory fhirDstu2ClientFactory, FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		return new FhirDstu2DemographicGatewayFactory(fhirDstu2DemographicFactories, fhirDstu2ClientFactory,
				fhirDstu2PatientRepository);
	}
}
