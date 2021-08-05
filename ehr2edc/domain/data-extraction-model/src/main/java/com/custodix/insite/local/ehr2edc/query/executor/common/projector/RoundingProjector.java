package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public class RoundingProjector implements Projector<Long, BigDecimal> {

	private static final String NAME = "Round to whole number";

	@Override
	public Optional<Long> project(final Optional<BigDecimal> value, ProjectionContext context) {
		return value.map(bigDecimal -> bigDecimal.setScale(0, RoundingMode.HALF_UP))
				.map(BigDecimal::longValue);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
