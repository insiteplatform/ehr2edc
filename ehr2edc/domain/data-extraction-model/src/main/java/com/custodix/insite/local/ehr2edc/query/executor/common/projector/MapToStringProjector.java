package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class MapToStringProjector implements Projector<String, Object> {
	private final static Logger LOGGER = LoggerFactory.getLogger(MapToStringProjector.class);
	private static final String NAME = "Map to string";

	private final Map<Object, String> mapping;

	public MapToStringProjector(Map<Object, String> mapping) {
		this.mapping = mapping;
	}

	@Override
	public Optional<String> project(Optional<Object> value, ProjectionContext context) {
		return value.map(key -> {
			final String mappedValue = mapping.get(key);
			if (mappedValue == null) {
				LOGGER.error("No mapping found for {}", key);
			}
			return mappedValue;
		});
	}

	@Override
	public String getName() {
		return NAME;
	}
}