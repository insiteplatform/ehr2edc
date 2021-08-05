package com.custodix.insite.local.ehr2edc.usecase.impl;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventRepository;
import com.custodix.insite.local.ehr2edc.query.GetSubmittedEventHistory;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEventRepository;

@Query
class GetSubmittedEventHistoryQuery implements GetSubmittedEventHistory {
	private final SubmittedEventRepository submittedEventRepository;
	private final PopulatedEventRepository populatedEventRepository;

	GetSubmittedEventHistoryQuery(SubmittedEventRepository submittedEventRepository, PopulatedEventRepository populatedEventRepository) {
		this.submittedEventRepository = submittedEventRepository;
		this.populatedEventRepository = populatedEventRepository;
	}

	@Override
	public Response get(Request request) {
		List<SubmittedEventHistoryItem> historyItems = getHistoryItems(request);
		return Response.newBuilder()
				.withHistoryItems(historyItems)
				.build();
	}

	private List<SubmittedEventHistoryItem> getHistoryItems(Request request) {
		PopulatedEvent populatedEvent = populatedEventRepository.getEvent(request.getEventId());
		List<SubmittedEvent> submittedEvents = submittedEventRepository.findAllSubmittedEvents(request.getSubjectId(),
				populatedEvent.getEventDefinitionId());
		return submittedEvents
				.stream()
				.map(this::mapHistoryItem)
				.collect(toList());
	}

	private SubmittedEventHistoryItem mapHistoryItem(SubmittedEvent submittedEvent) {
		return SubmittedEventHistoryItem.newBuilder()
				.withReviewedEventId(submittedEvent.getId())
				.withReviewDateTime(submittedEvent.getSubmittedDate())
				.withReviewer(submittedEvent.getSubmitter())
				.build();
	}
}