package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common;

import java.util.Map;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValueField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class MapProjectedValueMixin {
	@JsonProperty
	private Map<Object, String> mapping;
	@JsonProperty
	private ProjectedValueField field;
	@JsonProperty
	private boolean projectMissing;

	@JsonCreator
	public MapProjectedValueMixin(@JsonProperty("mapping") Map<Object, String> mapping,
			@JsonProperty("field") ProjectedValueField field, @JsonProperty("projectMissing") boolean projectMissing) {

	}
}
