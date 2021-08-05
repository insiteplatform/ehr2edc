package com.custodix.insite.local.ehr2edc.jackson.mixin.criterium;

import java.time.Period;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class FreshnessCriterionMixin {
	@JsonCreator
	private FreshnessCriterionMixin(@JsonProperty("maxAge") final Period maxAge) {
	}
}
