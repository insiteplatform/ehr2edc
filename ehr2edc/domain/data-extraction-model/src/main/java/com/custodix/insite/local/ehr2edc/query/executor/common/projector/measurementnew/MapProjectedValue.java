package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew;

import java.util.Map;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.MapToStringProjector;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class MapProjectedValue implements Projector<ProjectedValue, ProjectedValue> {
	private static final String NAME = "Map ProjectedValue";
	private final Map<Object, String> mapping;
	private final ProjectedValueField field;
	private final boolean projectMissing;

	public MapProjectedValue(Map<Object, String> mapping, ProjectedValueField field, boolean projectMissing) {
		this.mapping = mapping;
		this.field = field == null ? ProjectedValueField.VALUE : field;
		this.projectMissing = projectMissing;
	}

	@Override
	public Optional<ProjectedValue> project(Optional<ProjectedValue> value, ProjectionContext context) {
		return value.flatMap(v -> doProject(v, context));
	}

	@Override
	public String getName() {
		return NAME;
	}

	private Optional<ProjectedValue> doProject(ProjectedValue projectedValue, ProjectionContext context) {
		Optional<Object> fieldValue = field.getFieldValue(projectedValue);
		Optional<String> mappedValue = new MapToStringProjector(mapping).project(fieldValue, context);
		if(mappedValue.isPresent()) {
			return Optional.of(field.updateFieldValue(projectedValue, mappedValue.get()));
		} else if(projectMissing) {
			return Optional.of(field.updateFieldValue(projectedValue, fieldValue.map(Object::toString).orElse(null)));
		} else {
			return Optional.empty();
		}
	}
}
