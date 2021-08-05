package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class MapToBooleanProjectorMixin {
	@JsonProperty
	private Map<Object, Boolean> mapping;

	@JsonCreator
	public MapToBooleanProjectorMixin(@JsonProperty("mapping") Map<Object, Boolean> mapping) {

	}
}
