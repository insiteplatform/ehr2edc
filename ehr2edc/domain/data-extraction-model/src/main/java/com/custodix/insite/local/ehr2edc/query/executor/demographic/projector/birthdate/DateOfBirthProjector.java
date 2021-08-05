package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.DemographicProjector;

public final class DateOfBirthProjector implements DemographicProjector<DateOfBirth> {

	private static final String NAME = "Fact to date of birth";

	@Override
	public Optional<DateOfBirth> project(Optional<Demographic> value, ProjectionContext context) {
		return value.map(Demographic::getValue)
				.filter(StringUtils::isNotBlank)
				.map(LocalDate::parse)
				.map(DateOfBirth::new);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
