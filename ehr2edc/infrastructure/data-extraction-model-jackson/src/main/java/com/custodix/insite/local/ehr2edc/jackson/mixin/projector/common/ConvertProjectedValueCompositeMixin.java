package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common;

import java.util.List;
import java.util.Map;

import com.custodix.insite.local.ehr2edc.jackson.mixin.deserializer.ListDeserializer;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.NumericalProjectedValueProjector;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public abstract class ConvertProjectedValueCompositeMixin {
	@JsonDeserialize(keyUsing = ListDeserializer.class)
	@JsonProperty
	private Map<List<String>, NumericalProjectedValueProjector> projectors;

	@JsonCreator
	private ConvertProjectedValueCompositeMixin(@JsonProperty("projectors") Map<List<String>, NumericalProjectedValueProjector> projectors) {

	}
}
