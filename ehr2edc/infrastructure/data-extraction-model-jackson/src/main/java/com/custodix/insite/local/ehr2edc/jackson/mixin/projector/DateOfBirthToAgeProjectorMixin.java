package com.custodix.insite.local.ehr2edc.jackson.mixin.projector;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate.AgeUnit;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class DateOfBirthToAgeProjectorMixin {
	@JsonProperty
	private AgeUnit unit;

	@JsonCreator
	private DateOfBirthToAgeProjectorMixin(@JsonProperty("unit") final AgeUnit unit) {
	}
}
