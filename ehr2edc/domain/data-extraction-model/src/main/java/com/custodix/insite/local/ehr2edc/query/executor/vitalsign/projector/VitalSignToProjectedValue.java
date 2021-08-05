package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector;

import java.math.BigDecimal;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept;

/**
 * @deprecated use {@link com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.ProjectVitalSignValue} instead
 */
@Deprecated
public final class VitalSignToProjectedValue implements VitalSignProjector<ProjectedValue<BigDecimal>> {

	private static final String NAME = "To ProjectedValue (DEPRECATED)";

	@Override
	public Optional<ProjectedValue<BigDecimal>> project(Optional<VitalSign> value, ProjectionContext context) {
		return value.map(vitalSign -> {
			Measurement measurement = vitalSign.getMeasurement();
			return new ProjectedValue<>(getValue(measurement), getUnit(measurement), getCode(vitalSign));
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

	private String getCode(VitalSign value) {
		return Optional.ofNullable(value.getVitalSignConcept())
				.map(VitalSignConcept::getConcept)
				.map(ConceptCode::getCode)
				.orElse(null);
	}
}
