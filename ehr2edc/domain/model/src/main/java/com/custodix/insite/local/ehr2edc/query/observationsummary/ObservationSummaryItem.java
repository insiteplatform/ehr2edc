package com.custodix.insite.local.ehr2edc.query.observationsummary;

import java.time.LocalDate;

public final class ObservationSummaryItem {
	private final LocalDate date;
	private final String category;
	private final int amountOfObservations;

	public ObservationSummaryItem(LocalDate date, String category, int amountOfObservations) {
		this.date = date;
		this.category = category;
		this.amountOfObservations = amountOfObservations;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getCategory() {
		return category;
	}

	public int getAmountOfObservations() {
		return amountOfObservations;
	}
}