package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender;

import java.util.Map;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.google.common.collect.ImmutableMap;

public final class GenderToStringProjector implements Projector<String, Gender> {
	private static final String NAME = "Gender to string";
	private final Map<Gender, String> mapping;

	public GenderToStringProjector(Map<Gender, String> mapping) {
		this.mapping = ImmutableMap.copyOf(mapping);
	}

	@Override
	public Optional<String> project(Optional<Gender> value, ProjectionContext context) {
		return value.map(mapping::get);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
