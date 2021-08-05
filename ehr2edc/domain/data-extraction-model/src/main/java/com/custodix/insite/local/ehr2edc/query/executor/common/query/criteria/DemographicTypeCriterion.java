package com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria;

import java.util.Objects;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;

public final class DemographicTypeCriterion implements Criterion {
	private final DemographicType demographicType;

	private DemographicTypeCriterion(DemographicType demographicType) {
		this.demographicType = demographicType;
	}

	public static DemographicTypeCriterion type(DemographicType type) {
		return new DemographicTypeCriterion(type);
	}

	public DemographicType getDemographicType() {
		return demographicType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final DemographicTypeCriterion that = (DemographicTypeCriterion) o;
		return demographicType == that.demographicType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(demographicType);
	}
}
