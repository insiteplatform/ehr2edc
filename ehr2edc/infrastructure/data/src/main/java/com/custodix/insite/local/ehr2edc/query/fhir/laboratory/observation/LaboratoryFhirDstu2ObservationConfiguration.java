package com.custodix.insite.local.ehr2edc.query.fhir.laboratory.observation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory;

@Configuration
public class LaboratoryFhirDstu2ObservationConfiguration {

	@Bean
	LaboratoryObservationFhirQueryFactory laboratoryObservationFhirQueryFactory(FhirQueryFactory fhirQueryFactory) {
		return new LaboratoryObservationFhirQueryFactory(fhirQueryFactory);
	}

	@Bean
	LaboratoryObservationRepository laboratoryObservationRepository(
			LaboratoryObservationFhirQueryFactory observationFhirQueryFactory) {
		return new LaboratoryObservationRepository(observationFhirQueryFactory);
	}

	@Bean
	LabValueFactoryForObservation labValueFactoryForObservation() {
		return new LabValueFactoryForObservation();
	}

	@Bean
	LaboratoryFhirDstu2ObservationQuery fhirDstu2ObservationQuery(LaboratoryObservationRepository observationRepository,
			LabValueFactoryForObservation labValueFactoryForObservation) {
		return new LaboratoryFhirDstu2ObservationQuery(observationRepository, labValueFactoryForObservation);
	}

}
