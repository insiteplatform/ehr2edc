package com.custodix.insite.local.ehr2edc.query.fhir.demographic;

import static com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType.BIRTH_DATE;

import java.text.SimpleDateFormat;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic.Builder;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.dstu2.resource.Patient;

class FhirDstu2DemographicFactoryForBirthDate implements FhirDstu2DemographicFactory{

	private static final String BIRTH_DATE_PATTERN = "yyyy-MM-dd";

	@Override
	public DemographicType supports() {
		return BIRTH_DATE;
	}

	@Override
	public Optional<Demographic> findDemographic(Patient patient, SubjectId subjectId) {
		Builder builder = Demographic.newBuilder().withSubjectId(subjectId).withDemographicType(BIRTH_DATE);
		return Optional.ofNullable(patient.getBirthDate())
				.map(new SimpleDateFormat(BIRTH_DATE_PATTERN)::format)
				.map(builder::withValue)
				.map(Builder::build);
	}
}
