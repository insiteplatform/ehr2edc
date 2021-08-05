package com.custodix.insite.local.ehr2edc.jackson.mixin.criterium;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class DemographicTypeCriterionMixin {
	@JsonProperty
	private DemographicType demographicType;

	@JsonCreator
	private DemographicTypeCriterionMixin(@JsonProperty("demographicType") final DemographicType demographicType) {
	}
}
