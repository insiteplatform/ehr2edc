package com.custodix.insite.local.ehr2edc.query.fhir.medication;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2ClientFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirQueryFactory;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.administration.FhirDstu2MedicationAdministrationConfiguration;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.dispense.FhirDstu2MedicationDispenseConfiguration;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.order.FhirDstu2MedicationOrderConfiguration;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.statement.FhirDstu2MedicationStatementConfiguration;
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientRepository;

@Configuration
@Import({ FhirDstu2MedicationAdministrationConfiguration.class,
		  FhirDstu2MedicationDispenseConfiguration.class,
		  FhirDstu2MedicationOrderConfiguration.class,
		  FhirDstu2MedicationStatementConfiguration.class })
public class FhirDstu2MedicationConfiguration {

	@Bean
	MedicationConceptCodeProcessor medicationConceptCodeProcessor() {
		return new MedicationConceptCodeProcessor();
	}

	@Bean
	FhirQueryFactory fhirQueryFactory() {
		return new FhirQueryFactory();
	}

	@Bean
	FhirDstu2MedicationGatewayFactory fhirDstu2MedicationGatewayFactory(
			List<FhirDstu2MedicationQuery> medicationQueries, FhirDstu2ClientFactory fhirDstu2ClientFactory,
			FhirDstu2PatientRepository fhirDstu2PatientRepository) {
		return new FhirDstu2MedicationGatewayFactory(medicationQueries, fhirDstu2ClientFactory,
				fhirDstu2PatientRepository);
	}
}
