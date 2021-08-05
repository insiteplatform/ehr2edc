package com.custodix.insite.local.ehr2edc.usecase.impl;

import static java.util.Collections.emptyList;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventRepository;
import com.custodix.insite.local.ehr2edc.query.GetSubmittedEvent;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.submitted.*;
import com.custodix.insite.local.ehr2edc.user.User;
import com.custodix.insite.local.ehr2edc.user.UserRepository;
import com.custodix.insite.local.ehr2edc.vocabulary.FormId;

@Query
class GetSubmittedEventQuery implements GetSubmittedEvent {
	private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
	private final SubmittedEventRepository submittedEventRepository;
	private final PopulatedEventRepository populatedEventRepository;
	private final UserRepository userRepository;

	GetSubmittedEventQuery(SubmittedEventRepository submittedEventRepository,
			PopulatedEventRepository populatedEventRepository, UserRepository userRepository) {
		this.submittedEventRepository = submittedEventRepository;
		this.populatedEventRepository = populatedEventRepository;
		this.userRepository = userRepository;
	}

	@Override
	public Response get(Request request) {
		return submittedEventRepository.findById(request.getSubmittedEventId())
				.map(this::createResponse)
				.orElse(getBlankResponse());
	}

	private Response getBlankResponse() {
		return Response.newBuilder()
				.withReviewedForms(emptyList())
				.build();
	}

	private Response createResponse(SubmittedEvent submittedEvent) {
		PopulatedEvent populatedEvent = populatedEventRepository.getEvent(submittedEvent.getPopulatedEventId());
		return Response.newBuilder()
				.withReviewTime(submittedEvent.getSubmittedDate())
				.withReviewedForms(mapReviewedForms(submittedEvent))
				.withReviewer(submittedEvent.getSubmitter())
				.withPopulationTime(getPopulationTime(submittedEvent, populatedEvent))
				.withReferenceDate(getReferenceDate(submittedEvent, populatedEvent))
				.withPopulator(getPopulator(submittedEvent))
				.build();
	}

	private String getPopulator(SubmittedEvent submittedEvent) {
		return submittedEvent.getPopulator()
				.flatMap(userRepository::findUser)
				.map(User::getName)
				.orElse(null);
	}

	private LocalDate getReferenceDate(SubmittedEvent submittedEvent, PopulatedEvent populatedEvent) {
		return submittedEvent.getReferenceDate() != null ? submittedEvent.getReferenceDate() : populatedEvent.getReferenceDate();
	}

	private Instant getPopulationTime(SubmittedEvent submittedEvent, PopulatedEvent populatedEvent) {
		return submittedEvent.getPopulationTime() != null ? submittedEvent.getPopulationTime() : populatedEvent.getPopulationTime();
	}

	private List<Form> mapReviewedForms(SubmittedEvent submittedEvent) {
		return submittedEvent
				.getSubmittedForms()
				.stream()
				.map(this::mapReviewedForm)
				.collect(Collectors.toList());
	}

	private Form mapReviewedForm(SubmittedForm form) {
		return Form.newBuilder()
				.withId(FormId.of(form.getFormDefinitionId()
						.getId()))
				.withName(form.getName())
				.withItemGroups(mapReviewedItemGroups(form.getSubmittedItemGroups()))
				.build();
	}

	private List<ItemGroup> mapReviewedItemGroups(final List<SubmittedItemGroup> itemGroups) {
		return itemGroups.stream()
				.map(toItemGroupResponse())
				.collect(Collectors.toList());
	}

	private Function<SubmittedItemGroup, ItemGroup> toItemGroupResponse() {
		return ig -> ItemGroup.newBuilder()
				.withId(ig.getId())
				.withName(ig.getName())
				.withItems(mapReviewedItems(ig.getSubmittedItems()))
				.build();
	}

	private List<Item> mapReviewedItems(final List<SubmittedItem> reviewedItems) {
		return reviewedItems.stream()
				.map(toItemResponse())
				.collect(Collectors.toList());
	}

	private Function<SubmittedItem, Item> toItemResponse() {
		return i -> Item.newBuilder()
				.withId(i.getInstanceId())
				.withValue(i.getLabeledValue().getValue())
				.withValueLabel(i.getDisplayValue(DEFAULT_LOCALE))
				.withName(i.getDisplayLabel(DEFAULT_LOCALE))
				.withSubmittedToEdc(i.isSubmittedToEDC())
				.withUnit(toMeasurementUnitReferenceResponse(i))
				.build();
	}

	private String toMeasurementUnitReferenceResponse(final SubmittedItem reviewedItem) {
		return reviewedItem.getReviewedMeasurementUnitReference()
				.map(SubmittedMeasurementUnitReference::getId)
				.orElse(null);
	}
}