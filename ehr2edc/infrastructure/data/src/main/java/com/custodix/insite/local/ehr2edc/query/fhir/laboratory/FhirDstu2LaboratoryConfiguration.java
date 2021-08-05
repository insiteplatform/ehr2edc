package com.custodix.insite.local.ehr2edc.query.fhir.laboratory;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2ClientFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.laboratory.observation.LaboratoryFhirDstu2ObservationConfiguration;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientRepository;

@Configuration
@Import({ LaboratoryFhirDstu2ObservationConfiguration.class })
public class FhirDstu2LaboratoryConfiguration {

	@Bean
	FhirDstu2LaboratoryGatewayFactory fhirDstu2LaboratoryGatewayFactory(
			List<FhirDstu2LaboratoryQuery> laboratoryQueries, FhirDstu2ClientFactory fhirDstu2ClientFactory,
			FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		return new FhirDstu2LaboratoryGatewayFactory(laboratoryQueries, fhirDstu2ClientFactory,
				fhirDstu2PatientRepository);
	}
}
