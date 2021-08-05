package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector;

import java.time.LocalDateTime;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;

public final class LabValueToEndDateProjector implements LabValueProjector<LocalDateTime> {

	private static final String NAME = "End date";

	@Override
	public Optional<LocalDateTime> project(Optional<LabValue> value, ProjectionContext context) {
		return value.map(LabValue::getEndDate);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
