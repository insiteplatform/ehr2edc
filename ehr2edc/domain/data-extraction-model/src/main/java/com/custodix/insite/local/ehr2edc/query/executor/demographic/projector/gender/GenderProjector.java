package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.DemographicProjector;

public final class GenderProjector implements DemographicProjector<Gender> {

	private static final String NAME = "Fact to gender";

	@Override
	public Optional<Gender> project(Optional<Demographic> value, ProjectionContext context) {
		return value.map(Demographic::getValue)
				.map(Gender::fromCode);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
