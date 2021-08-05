package com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import com.custodix.insite.local.ehr2edc.query.executor.common.domain.Interval;

public final class FreshnessCriterion implements Criterion {
	private final Period maxAge;

	private FreshnessCriterion(Period maxAge) {
		this.maxAge = maxAge;
	}

	public static FreshnessCriterion maxAge(Period period) {
		return new FreshnessCriterion(period);
	}

	public Period getMaxAge() {
		return maxAge;
	}

	public Interval getInterval(LocalDate referenceDate) {
		LocalDate dateIncludingReferenceDate = referenceDate.plusDays(1);
		return Interval.of(maxAge, dateIncludingReferenceDate);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final FreshnessCriterion that = (FreshnessCriterion) o;
		return Objects.equals(maxAge, that.maxAge);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maxAge);
	}
}
