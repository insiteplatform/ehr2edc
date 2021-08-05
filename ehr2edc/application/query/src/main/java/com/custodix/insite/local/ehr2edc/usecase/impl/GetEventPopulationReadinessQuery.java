package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.query.GetEventPopulationReadiness;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.vocabulary.EventPopulationReadiness;

@Query
class GetEventPopulationReadinessQuery implements GetEventPopulationReadiness {

	private final StudyRepository studyRepository;

	GetEventPopulationReadinessQuery(final StudyRepository studyRepository) {
		this.studyRepository = studyRepository;
	}

	@Override
	public Response getEventPopulationReadiness(final Request request) {
		Study study = studyRepository.getStudyById(request.getStudyId());

		EventPopulationReadiness eventPopulationReadiness = study.getEventPopulationReadiness(request.getSubjectId());

		return toResponse(eventPopulationReadiness);
	}

	private Response toResponse(final EventPopulationReadiness populationReadiness) {
		Response.Builder builder = Response.newBuilder()
				.withNotReadyReason(populationReadiness.getNotReadyReason())
				.withReady(populationReadiness.isReady())
				.withSubjectMigrationInProgress(populationReadiness.isSubjectMigrationInProgress());

		return builder.build();
	}
}
