package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SetProjectedValueUnitMixin {
	@JsonProperty
	private String unit;

	@JsonCreator
	private SetProjectedValueUnitMixin(@JsonProperty("unit") String unit) {

	}
}
