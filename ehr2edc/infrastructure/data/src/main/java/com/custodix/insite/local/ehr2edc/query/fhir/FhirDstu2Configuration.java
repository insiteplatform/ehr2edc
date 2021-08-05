package com.custodix.insite.local.ehr2edc.query.fhir;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.demographic.FhirDstu2DemographicConfiguration;
import com.custodix.insite.local.ehr2edc.query.fhir.laboratory.FhirDstu2LaboratoryConfiguration;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.FhirDstu2MedicationConfiguration;
import com.custodix.insite.local.ehr2edc.query.fhir.mongo.configuration.Ehr2EdcFhirDbConfiguration;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientConfiguration;
import com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.FhirDstu2VitalSignConfiguration;

@Configuration
@Import({ Ehr2EdcFhirDbConfiguration.class,
		  FhirDstu2PatientConfiguration.class,
		  FhirDstu2DemographicConfiguration.class,
		  FhirDstu2MedicationConfiguration.class,
		  FhirDstu2LaboratoryConfiguration.class,
		  FhirDstu2VitalSignConfiguration.class })
public class FhirDstu2Configuration {

	@Bean
	FhirEHRGatewayFactory fhirEHRGatewayFactory(List<FhirDstu2GatewayFactory> fhirDstu2GatewayFactories,
			EHRConnectionRepository ehrConnectionRepository) {
		return new FhirEHRGatewayFactory(fhirDstu2GatewayFactories, ehrConnectionRepository);
	}

	@Bean
	FhirDstu2ClientFactory fhirDstu2ClientFactory(EHRConnectionRepository ehrConnectionRepository) {
		return new FhirDstu2ClientFactory(ehrConnectionRepository);
	}

}
