package com.custodix.insite.local.ehr2edc.jackson.mixin.projector;

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.LabValueField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ProjectLabValueMixin {
	@JsonProperty
	private LabValueField field;

	@JsonCreator
	private ProjectLabValueMixin(@JsonProperty("field") LabValueField field) {

	}
}
