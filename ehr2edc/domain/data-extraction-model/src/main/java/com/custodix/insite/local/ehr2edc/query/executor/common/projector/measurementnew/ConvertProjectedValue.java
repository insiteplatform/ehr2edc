package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.service.MeasurementConversionService;

public final class ConvertProjectedValue implements NumericalProjectedValueProjector {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertProjectedValue.class);
	private static final String NAME = "Convert value";

	private final MeasurementConversionService measurementConversionService;
	private final String unit;

	public ConvertProjectedValue(String unit) {
		this.measurementConversionService = new MeasurementConversionService();
		this.unit = unit;
	}

	@Override
	public Optional<ProjectedValue<BigDecimal>> project(Optional<ProjectedValue<BigDecimal>> value,
			ProjectionContext context) {
		return value.flatMap(this::convertMeasurement);
	}

	@Override
	public String getName() {
		return NAME;
	}

	private Optional<ProjectedValue<BigDecimal>> convertMeasurement(ProjectedValue<BigDecimal> measurement) {
		try {
			return measurementConversionService.convert(measurement, unit);
		} catch (Exception e) {
			LOGGER.error("Cannot convert {} to {} (code: {})",measurement.getUnit(), unit, measurement.getCode());
		}
		return Optional.empty();
	}
}
