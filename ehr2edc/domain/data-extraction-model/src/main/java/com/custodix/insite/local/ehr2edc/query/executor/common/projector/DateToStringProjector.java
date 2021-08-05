package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class DateToStringProjector implements Projector<String, TemporalAccessor> {
	private static final String NAME = "Date to formatted String";
	private final String pattern;

	public DateToStringProjector(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public Optional<String> project(Optional<TemporalAccessor> value, ProjectionContext context) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
		return value.map(dateTimeFormatter::format);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
