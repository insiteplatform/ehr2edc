package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class DateOfBirthToAgeProjector implements Projector<Age, DateOfBirth> {
	private static final String NAME = "Date of birth to Age";
	private final AgeUnit unit;

	public DateOfBirthToAgeProjector(final AgeUnit unit) {
		this.unit = unit;
	}

	@Override
	public Optional<Age> project(Optional<DateOfBirth> value, ProjectionContext context) {
		return value.map(dateOfBirth -> dateOfBirth.getAge(context.getReferenceDate(), unit));
	}

	@Override
	public String getName() {
		return NAME;
	}
}
