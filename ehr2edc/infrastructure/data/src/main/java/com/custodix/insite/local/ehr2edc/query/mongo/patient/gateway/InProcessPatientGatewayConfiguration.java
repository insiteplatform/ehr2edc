package com.custodix.insite.local.ehr2edc.query.mongo.patient.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.query.mongo.patient.repository.PatientIdDocumentRepository;

@Configuration
public class InProcessPatientGatewayConfiguration {
	@Bean
	public InProcessPatientGateway inProcessPatientEHRGateway(PatientIdDocumentRepository patientIdDocumentRepository) {
		return new InProcessPatientGateway(patientIdDocumentRepository);
	}

	@Bean
	public InProcessPatientGatewayFactory inProcessPatientEHRGatewayFactory(InProcessPatientGateway inProcessPatientGateway) {
		return new InProcessPatientGatewayFactory(inProcessPatientGateway);
	}
}
