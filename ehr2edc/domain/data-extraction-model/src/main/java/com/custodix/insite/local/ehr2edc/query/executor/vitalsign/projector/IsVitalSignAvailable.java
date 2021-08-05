package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;

public class IsVitalSignAvailable implements VitalSignProjector<ProjectedValue<Boolean>> {

	private static final String NAME = "'has vital sign'-boolean";

	@Override
	public Optional<ProjectedValue<Boolean>> project(Optional<VitalSign> value, ProjectionContext context) {
		return value.map(this::toProjectedValue);
	}

	@Override
	public String getName() {
		return NAME;
	}

	private ProjectedValue<Boolean> toProjectedValue(VitalSign vitalSign) {
		boolean isAvailable = ofNullable(vitalSign.getMeasurement()).map(Measurement::getValue)
				.isPresent();
		String code = vitalSign.getConcept()
				.getCode();
		return new ProjectedValue<>(isAvailable, null, code);
	}

}
