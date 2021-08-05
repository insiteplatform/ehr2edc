package com.custodix.insite.local.ehr2edc.usecase.impl;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.command.SubmitReviewedEvent;
import com.custodix.insite.local.ehr2edc.populator.*;
import com.custodix.insite.local.ehr2edc.shared.annotations.Command;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;
import com.custodix.insite.local.ehr2edc.vocabulary.FormId;

@Command
class SubmitReviewedEventCommand implements SubmitReviewedEvent {
	private final PopulatedEventRepository populatedEventRepository;
	private final GetCurrentUser getCurrentUser;
	private final Consumer<SubmittedEvent> reviewedEventConsumer;

	SubmitReviewedEventCommand(PopulatedEventRepository populatedEventRepository,
			GetCurrentUser getCurrentUser, Consumer<SubmittedEvent> submittedEventHandler) {
		this.populatedEventRepository = populatedEventRepository;
		this.getCurrentUser = getCurrentUser;
		this.reviewedEventConsumer = submittedEventHandler;
	}

	@Override
	public Response submit(Request request) {
		final PopulatedEvent populatedEvent = populatedEventRepository.getEvent(request.getEventId());
		final SubmittedEvent submittedEvent = submit(request, populatedEvent);
		return Response.newBuilder()
				.withSubmittedEventId(submittedEvent.getId())
				.build();
	}

	private SubmittedEvent submit(Request request, PopulatedEvent populatedEvent) {
		return populatedEvent.review(getGroupSelection(request), getCurrentUser, reviewedEventConsumer);
	}

	private Map<FormId, PopulatedFormSelection> getGroupSelection(Request request) {
		return request.getReviewedForms()
				.stream()
				.collect(Collectors.toMap(ReviewedForm::getId, this::toPopulatedItemsGroupSelection));
	}

	private PopulatedFormSelection toPopulatedItemsGroupSelection(ReviewedForm reviewedForm1) {
		return PopulatedFormSelection.newBuilder()
				.withLabName(reviewedForm1.getLabName())
				.withItemGroupSelections(getGroupSelection(reviewedForm1)).build();
	}

	private List<PopulatedItemGroupSelection> getGroupSelection(ReviewedForm reviewedForm1) {
		return reviewedForm1.getItemGroups()
			.stream()
			.map(SubmitReviewedEventCommand::toPopulatedItemGroupSelection)
			.collect(toList());
	}

	private static PopulatedItemGroupSelection toPopulatedItemGroupSelection(ItemGroup group) {
		return PopulatedItemGroupSelection.of(group.getId(), mapItemSelection(group.getItems()));
	}

	private static List<PopulatedItemSelection> mapItemSelection(List<Item> items) {
		return items.stream()
				.map(item -> PopulatedItemSelection.of(item.getId()))
				.collect(toList());
	}

}