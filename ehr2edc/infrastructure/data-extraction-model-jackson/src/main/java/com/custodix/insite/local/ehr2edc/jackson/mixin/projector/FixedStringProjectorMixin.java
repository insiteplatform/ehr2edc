package com.custodix.insite.local.ehr2edc.jackson.mixin.projector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class FixedStringProjectorMixin {
	@JsonProperty
	private String string;

	@JsonCreator
	public FixedStringProjectorMixin(@JsonProperty("string") String string) {
	}
}
