package com.custodix.insite.local.ehr2edc.usecase.impl;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventRepository;
import com.custodix.insite.local.ehr2edc.query.GetPopulatedEventHistory;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.user.User;
import com.custodix.insite.local.ehr2edc.user.UserRepository;

@Query
class GetPopulatedEventHistoryQuery implements GetPopulatedEventHistory {

	private final PopulatedEventRepository populatedEventRepository;
	private final UserRepository userRepository;

	GetPopulatedEventHistoryQuery(PopulatedEventRepository populatedEventRepository,
			UserRepository userRepository) {
		this.populatedEventRepository = populatedEventRepository;
		this.userRepository = userRepository;
	}

	@Override
	public Response get(Request request) {
		return Response.newBuilder()
				.withHistoryItems(getHistoryItems(request))
				.build();
	}

	private List<PopulatedEventHistoryItem> getHistoryItems(Request request) {
		return populatedEventRepository.findBy(request.getSubjectId(), request.getEventId())
				.stream()
				.map(this::toHistoryItem)
				.collect(toList());
	}

	private PopulatedEventHistoryItem toHistoryItem(PopulatedEvent populatedEvent) {
		return PopulatedEventHistoryItem.newBuilder()
				.withEventId(populatedEvent.getInstanceId())
				.withPopulationTime(populatedEvent.getPopulationTime())
				.withReferenceDate(populatedEvent.getReferenceDate())
				.withPopulator(resolvePopulator(populatedEvent))
				.build();
	}

	private String resolvePopulator(PopulatedEvent populatedEvent) {
		return populatedEvent.getPopulator()
				.flatMap(userRepository::findUser)
				.map(User::getName)
				.orElse(null);
	}
}