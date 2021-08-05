package com.custodix.insite.local.ehr2edc.usecase.impl.getformoverview;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventRepository;
import com.custodix.insite.local.ehr2edc.query.GetEvent;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEventRepository;
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Query
class GetEventQuery implements GetEvent {
	private final FormOverviewFactory formOverviewFactory;
	private final SubmittedEventRepository submittedEventRepository;
	private final PopulatedEventRepository populatedEventRepository;

	@Autowired
	GetEventQuery(SubmittedEventRepository submittedEventRepository, PopulatedEventRepository populatedEventRepository) {
		this.submittedEventRepository = submittedEventRepository;
		this.populatedEventRepository = populatedEventRepository;
		this.formOverviewFactory = new FormOverviewFactory();
	}

	@Override
	public Response getEvent(final GetEvent.Request request) {
		PopulatedEvent populatedEvent = populatedEventRepository.getEvent(request.getEventId());

		List<Form> forms = populatedEvent.getPopulatedForms()
				.stream()
				.map(f -> formOverviewFactory.createForm(f, populatedEvent))
				.collect(toList());

		return Response.newBuilder()
				.withEvent(Event.newBuilder()
						.withForms(forms)
						.withLastSubmissionTime(
								getLastSubmissionTime(request.getSubjectId(), populatedEvent.getEventDefinitionId()))
						.withName(getEventName(populatedEvent))
						.build())
				.build();
	}

	private String getEventName(final PopulatedEvent populatedEvent) {
		return StringUtils.isEmpty(populatedEvent.getName()) ? populatedEvent.getInstanceId().getId() : populatedEvent.getName();
	}

	private Instant getLastSubmissionTime(SubjectId subjectId, EventDefinitionId eventDefinitionId) {
		return submittedEventRepository.findMostRecentSubmittedEvent(subjectId, eventDefinitionId)
				.map(SubmittedEvent::getSubmittedDate)
				.orElse(null);
	}
}