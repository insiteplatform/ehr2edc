package com.custodix.insite.local.ehr2edc.query.fhir.medication;

import java.time.LocalDate;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;

import ca.uhn.fhir.rest.client.api.IGenericClient;

public interface FhirDstu2MedicationQuery {
	Stream<Medication> query(IGenericClient genericClient, MedicationQuery medicationQuery, LocalDate referenceDate,
			FhirResourceId fhirPatientResourceId);
}
