package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common;

import java.util.List;
import java.util.Map;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.Label;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class MapToLabeledValueMixin {
	@JsonProperty
	private Map<String, List<Label>>  mapping;

	@JsonCreator
	public MapToLabeledValueMixin(@JsonProperty("mapping") Map<String, List<Label>>  mapping) {

	}
}
