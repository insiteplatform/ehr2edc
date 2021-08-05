package com.custodix.insite.local.ehr2edc.query.executor.common.domain;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public final class Interval {
	private final LocalDate startDate;
	private final LocalDate endDate;

	private Interval(LocalDate startDate, LocalDate endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static Interval of(Period period, LocalDate endDate) {
		return new Interval(endDate.minus(period), endDate);
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Interval interval = (Interval) o;
		return Objects.equals(startDate, interval.startDate) && Objects.equals(endDate, interval.endDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(startDate, endDate);
	}
}
