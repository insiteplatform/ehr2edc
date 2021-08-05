package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.vitalstatus;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.DemographicProjector;

public final class VitalStatusProjector implements DemographicProjector<VitalStatus> {

	private static final String NAME = "Fact to vital status";

	@Override
	public Optional<VitalStatus> project(Optional<Demographic> value, ProjectionContext context) {
		return value.map(Demographic::getValue)
				.map(VitalStatus::valueOf);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
