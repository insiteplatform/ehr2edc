package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;

public final class GetMeasurementValueAndUnit implements Projector<String, Measurement> {

	private static final String NAME = "Value with unit";

	@Override
	public Optional<String> project(Optional<Measurement> value, ProjectionContext context) {
		if (isMeasurementPresent(value)) {
			return value.map(GetMeasurementValueAndUnit::apply);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	private static String apply(Measurement v) {
		if (!hasUnit(v)) {
			return v.getValue()
					.toString();
		} else {
			return v.getValue() + " " + v.getUnit();
		}
	}

	private static boolean hasUnit(Measurement v) {
		return v.getUnit() != null && !StringUtils.isEmpty(v.getUnit());
	}

	private boolean isMeasurementPresent(Optional<Measurement> measurement) {
		return measurement.isPresent() && hasValue(measurement.get());
	}

	private boolean hasValue(Measurement measurement) {
		return measurement.getValue() != null;
	}
}