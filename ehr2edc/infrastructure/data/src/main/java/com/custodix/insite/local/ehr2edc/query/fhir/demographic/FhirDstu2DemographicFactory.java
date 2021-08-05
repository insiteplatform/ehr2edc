package com.custodix.insite.local.ehr2edc.query.fhir.demographic;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.dstu2.resource.Patient;

public interface FhirDstu2DemographicFactory {

	DemographicType supports();

	Optional<Demographic> findDemographic(Patient patient, SubjectId subjectId);
}
