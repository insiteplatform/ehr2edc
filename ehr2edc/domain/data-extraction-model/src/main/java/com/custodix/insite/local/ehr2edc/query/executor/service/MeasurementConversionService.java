package com.custodix.insite.local.ehr2edc.query.executor.service;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import org.apache.commons.lang3.StringUtils;
import org.fhir.ucum.Decimal;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;

import java.math.BigDecimal;
import java.util.Optional;

public class MeasurementConversionService {
	private final UcumService measurementService;

	public MeasurementConversionService() {
		try {
			this.measurementService = new UcumEssenceService(
					MeasurementConversionService.class.getResourceAsStream("/ucum-essence.xml")
			);
		} catch (UcumException e) {
			throw new IllegalStateException("Failed to init measurement conversion service", e);
		}
	}

	public Optional<BigDecimal> convert(Measurement measurement, String targetUnit) {
		if (isValueAndUnitPresent(measurement)) {
			return doConvert(measurement, targetUnit);
		}
		return Optional.empty();
	}

	public Optional<ProjectedValue<BigDecimal>> convert(ProjectedValue<BigDecimal> measurement, String targetUnit) {
		if (isValueAndUnitPresent(measurement)) {
			return doConvert(measurement, targetUnit);
		}
		return Optional.empty();
	}

	private Optional<BigDecimal> doConvert(Measurement measurement, String targetUnit) {
		return Optional.of(doConvert(measurement.getValue(), measurement.getUnit(), targetUnit));
	}

	private Optional<ProjectedValue<BigDecimal>> doConvert(ProjectedValue<BigDecimal> measurement, String targetUnit) {
		BigDecimal convertedValue = doConvert(measurement.getValue(), measurement.getUnit(), targetUnit);
		return Optional.of(new ProjectedValue<>(convertedValue, targetUnit, measurement.getCode()));
	}

	private BigDecimal doConvert(BigDecimal sourceValue, String sourceUnit, String targetUnit) {
		try {
			Decimal targetValue = measurementService.convert(new Decimal(sourceValue.toString()), sourceUnit, targetUnit);
			return new BigDecimal(targetValue.toString());
		} catch (UcumException e) {
			throw new IllegalArgumentException(
					String.format("Cannot convert %s %s to %s", sourceValue, sourceUnit, targetUnit), e);
		}
	}

	private boolean isValueAndUnitPresent(Measurement measurement) {
		return hasValue(measurement) && hasUnit(measurement);
	}

	private boolean hasValue(Measurement measurement) {
		return measurement.getValue() != null;
	}

	private boolean hasUnit(Measurement measurement) {
		return StringUtils.isNotBlank(measurement.getUnit());
	}

	private boolean isValueAndUnitPresent(ProjectedValue measurement) {
		return hasValue(measurement) && hasUnit(measurement);
	}

	private boolean hasValue(ProjectedValue measurement) {
		return measurement.getValue() != null;
	}

	private boolean hasUnit(ProjectedValue measurement) {
		return StringUtils.isNotBlank(measurement.getUnit());
	}
}
