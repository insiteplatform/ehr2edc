package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.number.FormatNumber;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;

public final class FormatProjectedValueNumber implements Projector<ProjectedValue<String>, ProjectedValue<Number>> {
	private static final String NAME = "Format ProjectedValue";
	private final int maximumFractionDigits;
	private final Character decimalSeparator;

	public FormatProjectedValueNumber(int maximumFractionDigits, Character decimalSeparator) {
		this.maximumFractionDigits = maximumFractionDigits;
		this.decimalSeparator = decimalSeparator;
	}

	@Override
	public Optional<ProjectedValue<String>> project(Optional<ProjectedValue<Number>> value, ProjectionContext context) {
		return value.flatMap(v -> doProject(v, context));
	}

	@Override
	public String getName() {
		return NAME;
	}

	private Optional<ProjectedValue<String>> doProject(ProjectedValue<Number> projectedValue, ProjectionContext context) {
		Optional<Number> value = Optional.ofNullable(projectedValue.getValue());
		Optional<String> formattedValue = new FormatNumber(maximumFractionDigits, decimalSeparator)
				.project(value, context);
		return formattedValue.map(v -> new ProjectedValue<>(v, projectedValue.getUnit(), projectedValue.getCode()));
	}
}
