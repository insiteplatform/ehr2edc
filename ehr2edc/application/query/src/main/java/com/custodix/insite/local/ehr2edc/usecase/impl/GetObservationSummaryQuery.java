package com.custodix.insite.local.ehr2edc.usecase.impl;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.custodix.insite.local.ehr2edc.query.GetObservationSummary;
import com.custodix.insite.local.ehr2edc.query.observationsummary.ObservationSummaryEHRGateway;
import com.custodix.insite.local.ehr2edc.query.observationsummary.ObservationSummaryItem;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;

@Query
class GetObservationSummaryQuery implements GetObservationSummary {

	private final ObservationSummaryEHRGateway observationSummaryGateway;

	public GetObservationSummaryQuery(ObservationSummaryEHRGateway observationSummaryGateway) {
		this.observationSummaryGateway = observationSummaryGateway;
	}

	@Override
	public Response getSummary(Request request) {
		final List<SummaryItem> summaryItems = getSummaryItems(request);
		return Response.newBuilder()
				.withSummaryItems(summaryItems)
				.build();
	}

	private List<SummaryItem> getSummaryItems(Request request) {
		Map<LocalDate, Integer> totalsGroupedByDate = observationSummaryGateway.findForSubject(request.getSubjectId())
				.stream()
				.collect(groupingBy(ObservationSummaryItem::getDate,
						summingInt(ObservationSummaryItem::getAmountOfObservations)));
		return totalsGroupedByDate.entrySet()
				.stream()
				.map(this::makeSummaryItem)
				.sorted(comparing(SummaryItem::getDate))
				.collect(toList());
	}

	private SummaryItem makeSummaryItem(Map.Entry<LocalDate, Integer> dateToTotalEntry) {
		return SummaryItem.newBuilder()
				.withDate(dateToTotalEntry.getKey())
				.withAmountOfObservations(dateToTotalEntry.getValue())
				.build();
	}
}