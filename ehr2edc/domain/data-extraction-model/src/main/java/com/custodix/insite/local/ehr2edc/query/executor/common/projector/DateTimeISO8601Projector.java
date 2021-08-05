package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class DateTimeISO8601Projector implements Projector<String, TemporalAccessor> {
	private static final DateTimeFormatter TO_ISO8601_DATETIME = DateTimeFormatter.ISO_DATE_TIME;
	private static final String NAME = "Date to ISO-8601 format";

	@Override
	public Optional<String> project(Optional<TemporalAccessor> value, ProjectionContext context) {
		return value.map(TO_ISO8601_DATETIME::format);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
