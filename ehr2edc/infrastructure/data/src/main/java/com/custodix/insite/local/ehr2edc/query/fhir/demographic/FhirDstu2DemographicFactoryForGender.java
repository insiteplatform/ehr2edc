package com.custodix.insite.local.ehr2edc.query.fhir.demographic;

import static com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType.GENDER;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.dstu2.resource.Patient;

class FhirDstu2DemographicFactoryForGender implements FhirDstu2DemographicFactory{

	@Override
	public DemographicType supports() {
		return GENDER;
	}

	@Override
	public Optional<Demographic> findDemographic(Patient patient, SubjectId subjectId) {
		return Optional.ofNullable(createDemographic(patient, subjectId));
	}

	private Demographic createDemographic(Patient patient, SubjectId subjectId) {
		return StringUtils.isNotBlank(patient.getGender()) ?
				Demographic.newBuilder()
						.withSubjectId(subjectId)
						.withDemographicType(GENDER)
						.withValue(patient.getGender())
						.build():
				null;
	}
}
