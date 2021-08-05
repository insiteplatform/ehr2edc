package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew;

import java.util.Optional;

public final class ProjectedValue<T> {
	private final T value;
	private final String unit;
	private final String code;

	public ProjectedValue(T value, String unit, String code) {
		this.value = value;
		this.unit = unit;
		this.code = code;
	}

	public T getValue() {
		return value;
	}

	public String getUnit() {
		return unit;
	}

	public String getCode() {
		return code;
	}

	public ProjectedValue copyWithFieldUpdate(ProjectedValueField field, String fieldValue) {
		return field.updateFieldValue(this, fieldValue);
	}

	public Optional<Object> getFieldValue(ProjectedValueField field) {
		return field.getFieldValue(this);
	}
}
