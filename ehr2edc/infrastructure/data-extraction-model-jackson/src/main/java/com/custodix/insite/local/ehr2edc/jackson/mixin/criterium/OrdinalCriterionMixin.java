package com.custodix.insite.local.ehr2edc.jackson.mixin.criterium;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.OrdinalCriterion;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class OrdinalCriterionMixin {
	@JsonProperty
	private OrdinalCriterion.Ordinal ordinal;

	@JsonCreator
	public OrdinalCriterionMixin(@JsonProperty("ordinal") final OrdinalCriterion.Ordinal ordinal) {
	}
}
