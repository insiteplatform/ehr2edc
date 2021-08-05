package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common;

import com.custodix.insite.local.ehr2edc.query.executor.service.MeasurementConversionService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class MeasurementToValueInUnitMixin {
	@JsonIgnore
	private MeasurementConversionService measurementConversionService;
	@JsonProperty
	private String unit;

	@JsonCreator
	private MeasurementToValueInUnitMixin(@JsonProperty("unit") String unit) {

	}
}
