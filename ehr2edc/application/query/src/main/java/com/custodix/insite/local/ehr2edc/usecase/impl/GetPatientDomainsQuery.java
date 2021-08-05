package com.custodix.insite.local.ehr2edc.usecase.impl;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.custodix.insite.local.ehr2edc.domain.service.PatientEHRGateway;
import com.custodix.insite.local.ehr2edc.query.GetPatientDomains;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@Query
class GetPatientDomainsQuery implements GetPatientDomains {
	private final PatientEHRGateway patientEHRGateway;

	GetPatientDomainsQuery(PatientEHRGateway patientEHRGateway) {
		this.patientEHRGateway = patientEHRGateway;
	}

	@Override
	public Response getAll(Request request) {
		return Response.newBuilder()
				.withPatientDomains(getPatientDomains(request.getStudyId()))
				.build();
	}

	private List<PatientDomain> getPatientDomains(StudyId studyId) {
		return patientEHRGateway.getPatientDomains(studyId)
				.stream()
				.map(this::mapPatientDomain)
				.collect(toList());
	}

	private PatientDomain mapPatientDomain(com.custodix.insite.local.ehr2edc.patient.PatientDomain patientDomain) {
		return PatientDomain.newBuilder()
				.withName(patientDomain.getName())
				.build();
	}
}