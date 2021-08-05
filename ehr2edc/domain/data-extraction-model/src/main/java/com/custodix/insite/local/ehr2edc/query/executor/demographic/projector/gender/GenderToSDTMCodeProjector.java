package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class GenderToSDTMCodeProjector implements Projector<String, Gender> {

	private static final String NAME = "Gender to SDTM-code";

	@Override
	public Optional<String> project(Optional<Gender> value, ProjectionContext context) {
		return value.map(Gender::getSdtmCode);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
