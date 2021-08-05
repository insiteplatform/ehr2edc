package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;

public final class LabValueToVendorProjector implements LabValueProjector<String> {

	private static final String NAME = "Vendor";

	@Override
	public Optional<String> project(Optional<LabValue> value, ProjectionContext context) {
		return value.map(LabValue::getVendor);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
