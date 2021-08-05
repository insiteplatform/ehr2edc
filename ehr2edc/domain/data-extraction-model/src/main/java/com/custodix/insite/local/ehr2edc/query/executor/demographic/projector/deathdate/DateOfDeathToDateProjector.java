package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.deathdate;

import java.time.LocalDate;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class DateOfDeathToDateProjector implements Projector<LocalDate, DateOfDeath> {

	private static final String NAME = "Date of death to Date";

	@Override
	public Optional<LocalDate> project(Optional<DateOfDeath> value, ProjectionContext context) {
		return value.map(DateOfDeath::getDeathDate);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
