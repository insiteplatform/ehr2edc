package com.custodix.insite.local.ehr2edc.usecase.impl;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.command.PopulateEvent;
import com.custodix.insite.local.ehr2edc.populator.EventPopulator;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventRepository;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Command
class PopulateEventCommand implements PopulateEvent {
	private final StudyRepository studyRepository;
	private final GetCurrentUser getCurrentUser;
	private final PopulatedEventRepository populatedEventRepository;
	private final PopulationAuditing populationAuditing;
	private final EventPopulator eventPopulator;

	PopulateEventCommand(StudyRepository studyRepository, GetCurrentUser getCurrentUser, PopulatedEventRepository populatedEventRepository,
			PopulationAuditing populationAuditing, EventPopulator eventPopulator) {
		this.studyRepository = studyRepository;
		this.getCurrentUser = getCurrentUser;
		this.populatedEventRepository = populatedEventRepository;
		this.populationAuditing = populationAuditing;
		this.eventPopulator = eventPopulator;
	}

	@Override
	public Response populate(Request request) {
		Study study = studyRepository.getStudyById(request.getStudyId());
		SubjectId subjectId = request.getSubjectId();

		PopulatedEvent populatedEvent = study.populateEvent(request.getEventDefinitionId(), subjectId,
				request.getReferenceDate(), getCurrentUser.getUserId(),
				eventPopulator::populateEvent);

		populatedEventRepository.save(populatedEvent);
		populationAuditing.createPopulationContext(subjectId, study, populatedEvent);

		return Response.newBuilder()
				.withPopulatedDataPoints(populatedEvent.countPopulatedDataPoints())
				.build();
	}

}
