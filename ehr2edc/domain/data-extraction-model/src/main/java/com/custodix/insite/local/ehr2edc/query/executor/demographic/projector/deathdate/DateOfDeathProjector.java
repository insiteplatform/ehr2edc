package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.deathdate;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.DemographicProjector;

public final class DateOfDeathProjector implements DemographicProjector<DateOfDeath> {

	private static final String NAME = "Fact to Date of death";

	@Override
	public Optional<DateOfDeath> project(Optional<Demographic> value, ProjectionContext context) {
		return value.map(Demographic::getValue)
				.filter(StringUtils::isNotBlank)
				.map(LocalDate::parse)
				.map(DateOfDeath::new);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
