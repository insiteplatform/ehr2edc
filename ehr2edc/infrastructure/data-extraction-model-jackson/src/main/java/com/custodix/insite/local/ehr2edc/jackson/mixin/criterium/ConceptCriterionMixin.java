package com.custodix.insite.local.ehr2edc.jackson.mixin.criterium;

import java.util.List;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ConceptCriterionMixin {
	@JsonProperty
	private List<ConceptCode> concepts;
	@JsonProperty
	private boolean includeMissing;

	@JsonCreator
	public ConceptCriterionMixin(@JsonProperty("concepts") final List<ConceptCode> concepts,
			@JsonProperty("includeMissing") final boolean includeMissing) {
	}
}
