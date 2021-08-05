package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ComposeProjectedValueProjectionMixin {
	@JsonProperty
	private Projector projector;

	@JsonCreator
	private ComposeProjectedValueProjectionMixin(@JsonProperty("projector") Projector projector) {

	}
}
