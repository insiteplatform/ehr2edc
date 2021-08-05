package com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria;

import java.util.Objects;

public class OrdinalCriterion implements Criterion {
	private final Ordinal ordinal;

	private OrdinalCriterion(Ordinal ordinal) {
		this.ordinal = ordinal;
	}

	public static OrdinalCriterion ordinal(Ordinal ordinal) {
		return new OrdinalCriterion(ordinal);
	}

	public Ordinal getOrdinal() {
		return ordinal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		OrdinalCriterion that = (OrdinalCriterion) o;
		return ordinal == that.ordinal;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ordinal);
	}

	public enum Ordinal {
		FIRST,
		LAST
	}
}
