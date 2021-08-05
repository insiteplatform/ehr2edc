package com.custodix.insite.local.ehr2edc.jackson.mixin.concept;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class CodedConceptMixin {
	@JsonCreator
	private CodedConceptMixin(@JsonProperty("code") final String code) {}
}
