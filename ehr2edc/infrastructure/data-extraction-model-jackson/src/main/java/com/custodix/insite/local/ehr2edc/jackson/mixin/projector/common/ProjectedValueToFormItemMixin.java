package com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common;

import java.util.Map;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValueField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ProjectedValueToFormItemMixin {
	@JsonProperty
	private Map<Object, String> indexMapping;
	@JsonProperty
	private ProjectedValueField outputField;
	@JsonProperty
	private ProjectedValueToFormItem.UnitMapping unitMapping;
	@JsonProperty
	private boolean readOnly;
	@JsonProperty
	private boolean key;

	@JsonCreator
	public ProjectedValueToFormItemMixin(@JsonProperty("indexMapping") Map<Object, String> indexMapping,
			@JsonProperty("outputField") ProjectedValueField outputField,
			@JsonProperty("unitMapping") ProjectedValueToFormItem.UnitMapping unitMapping,
			@JsonProperty("readOnly") boolean readOnly, @JsonProperty("key") boolean key) {
	}
}
