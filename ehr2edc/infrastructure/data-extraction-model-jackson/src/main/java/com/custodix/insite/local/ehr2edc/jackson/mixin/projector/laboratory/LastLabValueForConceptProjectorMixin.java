package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.laboratory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class LastLabValueForConceptProjectorMixin {
	@JsonProperty
	private String concept;

	@JsonCreator
	public LastLabValueForConceptProjectorMixin(@JsonProperty("concept") String concept) {

	}
}
