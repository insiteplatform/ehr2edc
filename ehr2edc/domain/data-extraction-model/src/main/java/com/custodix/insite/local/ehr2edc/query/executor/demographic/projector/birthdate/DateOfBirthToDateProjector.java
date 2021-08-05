package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate;

import java.time.LocalDate;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class DateOfBirthToDateProjector implements Projector<LocalDate, DateOfBirth> {

	private static final String NAME = "Date of Birth to Date";

	@Override
	public Optional<LocalDate> project(Optional<DateOfBirth> value, ProjectionContext context) {
		return value.map(DateOfBirth::getBirthDate);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
