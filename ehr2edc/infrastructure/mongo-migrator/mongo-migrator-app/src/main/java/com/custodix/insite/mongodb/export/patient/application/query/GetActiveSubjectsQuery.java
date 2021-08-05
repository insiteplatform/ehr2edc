package com.custodix.insite.mongodb.export.patient.application.query;

import java.util.List;
import java.util.stream.Collectors;

import com.custodix.insite.mongodb.export.patient.application.api.GetActiveSubjects;
import com.custodix.insite.mongodb.export.patient.domain.model.ActiveSubject;
import com.custodix.insite.mongodb.export.patient.domain.repository.ActiveSubjectEHRGateway;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public class GetActiveSubjectsQuery implements GetActiveSubjects {

	private final ActiveSubjectEHRGateway activeSubjectEHRGateway;

	public GetActiveSubjectsQuery(final ActiveSubjectEHRGateway activeSubjectEHRGateway) {
		this.activeSubjectEHRGateway = activeSubjectEHRGateway;
	}

	@Override
	public Response getAll() {
		return Response.newBuilder().withPatientIdentifiers(getAllActiveSubjects()).build();
	}

	private List<PatientIdentifier> getAllActiveSubjects() {
		return activeSubjectEHRGateway.getAll().stream()
				.map(ActiveSubject::getPatientIdentifier)
				.collect(Collectors.toList());
	}
}
