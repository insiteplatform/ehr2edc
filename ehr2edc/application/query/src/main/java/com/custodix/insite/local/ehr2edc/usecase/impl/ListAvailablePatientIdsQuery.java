package com.custodix.insite.local.ehr2edc.usecase.impl;

import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.domain.service.PatientEHRGateway;
import com.custodix.insite.local.ehr2edc.query.ListAvailablePatientIds;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@Query
class ListAvailablePatientIdsQuery implements ListAvailablePatientIds {

	private static final Long DEFAULT_LIMIT = 100L;

	private final PatientEHRGateway patientEHRGateway;
	private final StudyRepository studyRepository;

	public ListAvailablePatientIdsQuery(PatientEHRGateway patientEHRGateway, StudyRepository studyRepository) {
		this.patientEHRGateway = patientEHRGateway;
		this.studyRepository = studyRepository;
	}

	@Override
	public Response list(Request request) {
		final StudyId studyId = request.getStudyId();
		final Study study = studyRepository.getStudyById(studyId);
		final int maxSize = limitFrom(request).intValue();
		return patientEHRGateway
				.getFiltered(studyId, request.getPatientDomain(), request.getFilter(), maxSize)
				.stream()
				.filter(study::patientIsNotRegistered)
				.map(PatientCDWReference::getId)
				.sorted(naturalOrder())
				.limit(maxSize)
				.collect(collectingAndThen(toList(), toResponse()));
	}

	private Long limitFrom(Request request) {
		return Optional.of(request)
				.map(Request::getLimit)
				.orElse(DEFAULT_LIMIT);
	}

	private Function<List<String>, Response> toResponse() {
		return patientIds -> Response.newBuilder()
				.withPatientIds(patientIds)
				.build();
	}
}
