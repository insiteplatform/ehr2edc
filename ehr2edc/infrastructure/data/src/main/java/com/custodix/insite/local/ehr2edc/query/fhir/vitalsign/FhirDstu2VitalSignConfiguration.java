package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2ClientFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation.VitalSignFhirDstu2ObservationConfiguration;

@Configuration
@Import({ VitalSignFhirDstu2ObservationConfiguration.class })
public class FhirDstu2VitalSignConfiguration {

	@Bean
	FhirDstu2VitalSignGatewayFactory fhirDstu2VitalSignGatewayFactory(List<FhirDstu2VitalSignQuery> vitalSignQueries,
			FhirDstu2ClientFactory fhirDstu2ClientFactory, FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		return new FhirDstu2VitalSignGatewayFactory(vitalSignQueries, fhirDstu2ClientFactory,
				fhirDstu2PatientRepository);
	}
}
