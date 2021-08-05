package com.custodix.insite.local.ehr2edc.query.fhir.medication.statement;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory;

@Configuration
public class FhirDstu2MedicationStatementConfiguration {

	@Bean
	MedicationStatementRepository medicationResourcesForMedicationStatementQuery( FhirQueryFactory fhirQueryFactory) {
		return new MedicationStatementRepository(fhirQueryFactory);
	}

	@Bean
	MedicationFactoryForMedicationStatement medicationFactoryForMedicationStatement() {
		return new MedicationFactoryForMedicationStatement();
	}

	@Bean
	FhirDstu2MedicationStatementQuery fhirDstu2MedicationStatementQuery(MedicationStatementRepository medicationStatementRepository,
			MedicationFactoryForMedicationStatement medicationFactoryForMedicationStatement) {
		return new FhirDstu2MedicationStatementQuery(medicationStatementRepository,
				medicationFactoryForMedicationStatement);
	}

}
