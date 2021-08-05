package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector;

import java.math.BigDecimal;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;

/**
 * @deprecated use {@link com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.ProjectLabValue} instead
 */
@Deprecated
public final class LabValueToProjectedValue implements LabValueProjector<ProjectedValue<BigDecimal>> {

	private static final String NAME = "To ProjectedValue (DEPRECATED)";

	@Override
	public Optional<ProjectedValue<BigDecimal>> project(Optional<LabValue> value, ProjectionContext context) {
		return value.map(labValue -> {
			Measurement measurement = labValue.getQuantitativeResult();
			return new ProjectedValue<>(getValue(measurement), getUnit(measurement), getCode(labValue));
		});
	}

	@Override
	public String getName() {
		return NAME;
	}

	private BigDecimal getValue(Measurement measurement) {
		return Optional.ofNullable(measurement)
				.map(Measurement::getValue)
				.orElse(null);
	}

	private String getUnit(Measurement measurement) {
		return Optional.ofNullable(measurement)
				.map(Measurement::getUnit)
				.orElse(null);
	}

	private String getCode(LabValue value) {
		return Optional.ofNullable(value.getLabConcept())
				.map(LabConcept::getConcept)
				.map(ConceptCode::getCode)
				.orElse(null);
	}
}
