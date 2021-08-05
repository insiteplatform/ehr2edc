package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class FormatNumberMixin {
	@JsonProperty
	private int maximumFractionDigits;
	@JsonProperty
	private Character decimalSeparator;

	@JsonCreator
	public FormatNumberMixin(@JsonProperty("maximumFractionDigits") int maximumFractionDigits,
			@JsonProperty("decimalSeparator") Character decimalSeparator) {
	}
}
