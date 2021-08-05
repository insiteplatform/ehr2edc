package com.custodix.insite.local.ehr2edc.jackson.mixin.projector;

import java.util.Map;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.Gender;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class GenderToStringProjectorMixin {
	@JsonProperty
	private Map<Gender, String> mapping;

	@JsonCreator
	private GenderToStringProjectorMixin(@JsonProperty("mapping") Map<Gender, String> mapping) {
	}
}
