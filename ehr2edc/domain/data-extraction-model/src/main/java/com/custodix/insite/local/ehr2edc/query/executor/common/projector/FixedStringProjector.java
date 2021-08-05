package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public class FixedStringProjector implements Projector<String, Object> {
	private static final String NAME = "Fixed String";

	private final String string;

	public FixedStringProjector(String string) {
		this.string = string;
	}

	@Override
	public Optional<String> project(Optional<Object> value, ProjectionContext context) {
		return value.isPresent() ? Optional.of(string) : Optional.empty();
	}

	@Override
	public String getName() {
		return NAME;
	}
}
