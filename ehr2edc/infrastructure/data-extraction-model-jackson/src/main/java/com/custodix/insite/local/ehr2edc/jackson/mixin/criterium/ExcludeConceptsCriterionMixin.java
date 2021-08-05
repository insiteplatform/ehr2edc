package com.custodix.insite.local.ehr2edc.jackson.mixin.criterium;

import java.util.List;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ExcludeConceptsCriterionMixin {
	@JsonProperty
	private List<ConceptCode> concepts;

	@JsonCreator
	public ExcludeConceptsCriterionMixin(@JsonProperty("concepts") final List<ConceptCode> concepts) {
	}
}
