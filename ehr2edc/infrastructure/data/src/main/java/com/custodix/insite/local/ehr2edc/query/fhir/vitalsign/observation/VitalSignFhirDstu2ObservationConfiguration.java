package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation.mapping.VitalSignFactoryForObservation;

@Configuration
public class VitalSignFhirDstu2ObservationConfiguration {

	@Bean
	VitalSignObservationFhirQueryFactory vitalSignObservationFhirQueryFactory(FhirQueryFactory fhirQueryFactory) {
		return new VitalSignObservationFhirQueryFactory(fhirQueryFactory);
	}

	@Bean
	VitalSignObservationRepository vitalSignObservationRepository(
			VitalSignObservationFhirQueryFactory observationFhirQueryFactory) {
		return new VitalSignObservationRepository(observationFhirQueryFactory);
	}

	@Bean
	VitalSignFactoryForObservation vitalSignFactoryForObservation() {
		return new VitalSignFactoryForObservation();
	}

	@Bean
	VitalSignFhirDstu2ObservationQuery vitalSignFhirDstu2ObservationQuery(
			VitalSignObservationRepository observationRepository,
			VitalSignFactoryForObservation vitalSignFactoryForObservation) {
		return new VitalSignFhirDstu2ObservationQuery(observationRepository, vitalSignFactoryForObservation);
	}

}
