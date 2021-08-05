package com.custodix.insite.local.ehr2edc.query.fhir.medication.administration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory;

@Configuration
public class FhirDstu2MedicationAdministrationConfiguration {

	@Bean
	MedicationAdministrationRepository medicationResourcesForMedicationAdministrationQuery(
			FhirQueryFactory fhirQueryFactory) {
		return new MedicationAdministrationRepository(fhirQueryFactory);
	}

	@Bean
	MedicationFactoryForMedicationAdministration medicationFactoryForMedicationAdministration() {
		return new MedicationFactoryForMedicationAdministration();
	}

	@Bean
	FhirDstu2MedicationAdministrationQuery fhirDstu2MedicationAdministrationQuery(
			MedicationAdministrationRepository medicationResourcesForMedicationAdministrationQuery,
			MedicationFactoryForMedicationAdministration medicationFactoryForMedicationAdministration) {
		return new FhirDstu2MedicationAdministrationQuery(medicationResourcesForMedicationAdministrationQuery,
				medicationFactoryForMedicationAdministration);
	}
}
