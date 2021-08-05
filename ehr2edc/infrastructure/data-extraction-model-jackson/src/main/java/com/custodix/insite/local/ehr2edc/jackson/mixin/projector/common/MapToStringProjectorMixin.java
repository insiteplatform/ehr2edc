package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class MapToStringProjectorMixin {
	@JsonProperty
	private Map<Object, String> mapping;

	@JsonCreator
	public MapToStringProjectorMixin(@JsonProperty("mapping") Map<Object, String> mapping) {

	}
}
