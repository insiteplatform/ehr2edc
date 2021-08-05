package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign;

import java.time.LocalDate;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;

import ca.uhn.fhir.rest.client.api.IGenericClient;

public interface FhirDstu2VitalSignQuery {
	Stream<VitalSign> query(IGenericClient genericClient, VitalSignQuery vitalSignQuery, LocalDate referenceDate,
			FhirResourceId fhirPatientResourceId);
}
