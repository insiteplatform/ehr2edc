package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ConvertProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.service.MeasurementConversionService;

public final class MeasurementToValueInUnit implements Projector<BigDecimal, Measurement> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertProjectedValue.class);
	private static final String NAME = "Converted value";

	private final MeasurementConversionService measurementConversionService;
	private final String unit;

	public MeasurementToValueInUnit(String unit) {
		this.measurementConversionService = new MeasurementConversionService();
		this.unit = unit;
	}

	@Override
	public Optional<BigDecimal> project(Optional<Measurement> value, ProjectionContext context) {
		return value.flatMap(this::convertMeasurement);
	}

	@Override
	public String getName() {
		return NAME;
	}

	private Optional<BigDecimal> convertMeasurement(Measurement measurement) {
		try {
			return measurementConversionService.convert(measurement, unit);
		} catch (Throwable t) {
			LOGGER.error("Cannot convert {} to {}", measurement.getUnit(), unit, t);
		}
		return Optional.empty();
	}
}
