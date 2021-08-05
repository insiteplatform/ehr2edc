package com.custodix.insite.local.ehr2edc.query.mongo.patient.gateway;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Set;

import com.custodix.insite.local.ehr2edc.patient.PatientDomain;
import com.custodix.insite.local.ehr2edc.query.mongo.patient.model.PatientIdDocument;
import com.custodix.insite.local.ehr2edc.query.mongo.patient.repository.PatientIdDocumentRepository;
import com.custodix.insite.local.ehr2edc.query.patient.PatientEHRGatewayStrategy;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientSearchCriteria;

public class InProcessPatientGateway implements PatientEHRGatewayStrategy {
	private final PatientIdDocumentRepository patientIdDocumentRepository;

	InProcessPatientGateway(PatientIdDocumentRepository patientIdDocumentRepository) {
		this.patientIdDocumentRepository = patientIdDocumentRepository;
	}

	@Override
	public boolean exists(PatientSearchCriteria patientSearchCriteria) {
		// Disable find with birth date  until is E2E-630 is finished
		// @NotNull
		return patientIdDocumentRepository.findBySourceAndIdentifier(
				patientSearchCriteria.getPatientCDWReference().getSource(),
				patientSearchCriteria.getPatientCDWReference().getId())
				.isPresent();
//		return patientIdDocumentRepository.findBySourceAndIdentifierAndBirthDate(
//				patientSearchCriteria.getPatientCDWReference().getSource(),
//				patientSearchCriteria.getPatientCDWReference().getId(),
//				patientSearchCriteria.getBirthDate())
//				.isPresent();
	}

	@Override
	public Set<PatientCDWReference> getFiltered(String patientDomain, String filter, int limit) {
		return getFilteredDocuments(patientDomain, filter).stream()
				.map(this::toReference)
				.limit(limit)
				.collect(toSet());
	}

	@Override
	public List<PatientDomain> getPatientDomains() {
		return patientIdDocumentRepository.findDistinctSources()
				.stream()
				.map(PatientDomain::of)
				.collect(toList());
	}

	private List<PatientIdDocument> getFilteredDocuments(String patientDomain, String filter) {
		return isNull(filter) ?
				patientIdDocumentRepository.findAllBySource(patientDomain) :
				patientIdDocumentRepository.findAllBySourceAndIdentifierLike(patientDomain, filter);
	}

	private PatientCDWReference toReference(PatientIdDocument doc) {
		return PatientCDWReference.newBuilder()
				.withId(doc.getIdentifier())
				.withSource(doc.getSource())
				.build();
	}
}
