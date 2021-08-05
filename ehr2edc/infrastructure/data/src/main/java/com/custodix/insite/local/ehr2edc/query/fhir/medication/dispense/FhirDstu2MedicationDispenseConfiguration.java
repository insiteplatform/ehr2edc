package com.custodix.insite.local.ehr2edc.query.fhir.medication.dispense;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory;

@Configuration
public class FhirDstu2MedicationDispenseConfiguration {

	@Bean
	MedicationDispenseRepository medicationResourcesForMedicationDispenseQuery( FhirQueryFactory fhirQueryFactory) {
		return new MedicationDispenseRepository(fhirQueryFactory);
	}

	@Bean
	MedicationFactoryForMedicationDispense medicationFactoryForMedicationDispense() {
		return new MedicationFactoryForMedicationDispense();
	}

	@Bean
	FhirDstu2MedicationDispenseQuery fhirDstu2MedicationDispenseQuery(
			MedicationDispenseRepository medicationResourcesForMedicationDispenseQuery,
			MedicationFactoryForMedicationDispense medicationFactoryForMedicationDispense) {
		return new FhirDstu2MedicationDispenseQuery(medicationResourcesForMedicationDispenseQuery,
				medicationFactoryForMedicationDispense);
	}
}
