package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.model;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class LabelMixin {
	@JsonProperty
	private Locale locale;
	@JsonProperty
	private String text;

	@JsonCreator
	private LabelMixin(@JsonProperty("locale") Locale locale, @JsonProperty("text") String text) {
	}
}