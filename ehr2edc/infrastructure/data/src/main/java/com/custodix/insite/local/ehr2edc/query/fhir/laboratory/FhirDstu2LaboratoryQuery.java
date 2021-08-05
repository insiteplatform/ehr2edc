package com.custodix.insite.local.ehr2edc.query.fhir.laboratory;

import java.time.LocalDate;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LaboratoryQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;

import ca.uhn.fhir.rest.client.api.IGenericClient;

public interface FhirDstu2LaboratoryQuery {
	Stream<LabValue> query(IGenericClient genericClient, LaboratoryQuery laboratoryQuery, LocalDate referenceDate,
			FhirResourceId fhirPatientResourceId);
}
