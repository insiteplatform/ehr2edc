package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class DateToStringProjectorMixin {
	@JsonProperty
	private String pattern;

	@JsonCreator
	public DateToStringProjectorMixin(@JsonProperty("pattern") String pattern) {

	}
}
