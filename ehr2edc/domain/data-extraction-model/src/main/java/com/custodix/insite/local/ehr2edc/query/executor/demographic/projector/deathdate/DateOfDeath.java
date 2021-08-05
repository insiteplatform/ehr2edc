package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.deathdate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateOfDeath {
	private LocalDate deathDate;

	public DateOfDeath(final LocalDate deathDate) {
		this.deathDate = deathDate;
	}

	public LocalDate getDeathDate() {
		return deathDate;
	}

	public String toString(String format) {
		return deathDate.format(DateTimeFormatter.ofPattern(format));
	}

	@Override
	public String toString() {
		return deathDate.format(DateTimeFormatter.ISO_DATE);
	}
}
