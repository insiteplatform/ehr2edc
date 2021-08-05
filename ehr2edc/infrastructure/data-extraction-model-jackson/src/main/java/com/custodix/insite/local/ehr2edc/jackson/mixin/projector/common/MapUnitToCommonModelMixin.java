package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValueField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MapUnitToCommonModelMixin {
	@JsonProperty
	private ProjectedValueField field;
	@JsonProperty
	private boolean projectMissing;

	@JsonCreator
	public MapUnitToCommonModelMixin(@JsonProperty("field") ProjectedValueField field,
			@JsonProperty("projectMissing") boolean projectMissing) {

	}
}
