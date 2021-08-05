package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.util.Map;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class MapToBooleanProjector implements Projector<Boolean, Object> {
	private static final String NAME = "Map to boolean";
	private final Map<Object, Boolean> mapping;

	public MapToBooleanProjector(Map<Object, Boolean> mapping) {
		this.mapping = mapping;
	}

	@Override
	public Optional<Boolean> project(Optional<Object> value, ProjectionContext context) {
		return value.map(mapping::get);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
