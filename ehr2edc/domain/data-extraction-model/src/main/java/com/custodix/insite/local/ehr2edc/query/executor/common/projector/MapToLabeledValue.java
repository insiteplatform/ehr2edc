package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.Label;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class MapToLabeledValue implements Projector<LabeledValue, String> {
	private static final String NAME = "Map to labeled value";
	private final Map<String, List<Label>> mapping;

	public MapToLabeledValue(Map<String, List<Label>> mapping) {
		this.mapping = mapping;
	}

	@Override
	public Optional<LabeledValue> project(Optional<String> value, ProjectionContext context) {
		return value.map(this::createLabeledValue);
	}

	@Override
	public String getName() {
		return NAME;
	}

	private LabeledValue createLabeledValue(String value) {
		return new LabeledValue(value, mapping.getOrDefault(value, emptyList()));
	}
}
