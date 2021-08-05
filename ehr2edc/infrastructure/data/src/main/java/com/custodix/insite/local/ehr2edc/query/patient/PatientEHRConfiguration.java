package com.custodix.insite.local.ehr2edc.query.patient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository;
import com.custodix.insite.local.ehr2edc.domain.service.PatientEHRGateway;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2Configuration;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientGatewayFactory;
import com.custodix.insite.local.ehr2edc.query.mongo.patient.gateway.InProcessPatientGatewayConfiguration;
import com.custodix.insite.local.ehr2edc.query.mongo.patient.gateway.InProcessPatientGatewayFactory;

@Configuration
@Import({ FhirDstu2Configuration.class, InProcessPatientGatewayConfiguration.class })
public class PatientEHRConfiguration {

	@Bean
	public PatientEHRGatewayFactory patientEHRGatewayFactory(EHRConnectionRepository ehrConnectionRepository,
			FhirDstu2PatientGatewayFactory fhirDstu2PatientGatewayFactory,
			InProcessPatientGatewayFactory inProcessPatientGatewayFactory) {
		return new PatientEHRGatewayFactory(ehrConnectionRepository, fhirDstu2PatientGatewayFactory,
				inProcessPatientGatewayFactory);
	}

	@Bean
	public PatientEHRGateway patientEHRGateway(PatientEHRGatewayFactory patientEHRGatewayFactory) {
		return new PatientEHRGatewayImpl(patientEHRGatewayFactory);
	}
}
