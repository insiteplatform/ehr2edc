package com.custodix.insite.mongodb.export.patient.domain.model.labvalue;

import java.util.Optional;

import com.custodix.insite.mongodb.export.patient.domain.model.common.ObservationValue;

public final class LabMeasurement {

	private static final String LAB_MEASUREMENT_DOCUMENT = "{\"lowerLimit\" : \"%s\",\"upperLimit\" : \"%s\",\"value\" : \"%s\",\"unit\" : \"%s\"}";

	private LabMeasurement() {
	}

	public static String subdocument(ObservationValue lowerLimit, ObservationValue upperLimit,
			ObservationValue actual) {
		return String.format(LAB_MEASUREMENT_DOCUMENT, Optional.ofNullable(lowerLimit)
				.map(ObservationValue::getValue)
				.orElse(""), Optional.ofNullable(upperLimit)
				.map(ObservationValue::getValue)
				.orElse(""), Optional.ofNullable(actual)
				.map(ObservationValue::getValue)
				.orElse(""), Optional.ofNullable(actual)
				.map(ObservationValue::getUnit)
				.orElse(""));
	}
}