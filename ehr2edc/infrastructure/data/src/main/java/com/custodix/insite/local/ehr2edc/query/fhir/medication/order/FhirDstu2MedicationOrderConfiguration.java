package com.custodix.insite.local.ehr2edc.query.fhir.medication.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.MedicationConceptCodeProcessor;

@Configuration
public class FhirDstu2MedicationOrderConfiguration {

	@Bean
	MedicationOrderRepository medicationResourcesForMedicationOrderQuery(final FhirQueryFactory fhirQueryFactory) {
		return new MedicationOrderRepository(fhirQueryFactory);
	}

	@Bean
	MedicationFactoryForMedicationOrder medicationFactoryForMedicationOrder(
			final MedicationConceptCodeProcessor medicationConceptCodeProcessor) {
		return new MedicationFactoryForMedicationOrder(medicationConceptCodeProcessor);
	}

	@Bean
	FhirDstu2MedicationOrderQuery fhirDstu2MedicationOrderQuery(
			final MedicationOrderRepository medicationResourcesForMedicationOrderQuery,
			final MedicationFactoryForMedicationOrder medicationFactoryForMedicationOrder) {
		return new FhirDstu2MedicationOrderQuery(medicationResourcesForMedicationOrderQuery,
				medicationFactoryForMedicationOrder);
	}
}
