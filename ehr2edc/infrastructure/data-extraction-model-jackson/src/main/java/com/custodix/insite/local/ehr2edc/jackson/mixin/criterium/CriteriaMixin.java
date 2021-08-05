package com.custodix.insite.local.ehr2edc.jackson.mixin.criterium;

import java.util.List;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criterion;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class CriteriaMixin {
	@JsonProperty
	private List<Criterion> criteria;
}
