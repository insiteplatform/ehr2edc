package com.custodix.insite.local.ehr2edc.jackson.mixin.projector;

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.VitalSignField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ProjectVitalSignValueMixin {
	@JsonProperty
	private VitalSignField field;

	@JsonCreator
	private ProjectVitalSignValueMixin(@JsonProperty("field") VitalSignField field) {

	}
}
