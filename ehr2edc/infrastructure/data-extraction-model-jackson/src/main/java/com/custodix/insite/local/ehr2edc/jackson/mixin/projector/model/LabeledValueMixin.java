package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.model;

import java.util.List;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.Label;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class LabeledValueMixin {
	@JsonProperty
	private String value;
	@JsonProperty
	private List<Label> labels;

	@JsonCreator
	private LabeledValueMixin(@JsonProperty("value") String value, @JsonProperty("labels") List<Label> labels) {
	}
}
