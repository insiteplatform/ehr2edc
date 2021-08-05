package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;

public final class ConvertProjectedValueComposite implements NumericalProjectedValueProjector {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertProjectedValueComposite.class);
	private static final String NAME = "Convert ProjectedValue";

	private final Map<List<String>, NumericalProjectedValueProjector> projectors;

	public ConvertProjectedValueComposite(Map<List<String>, NumericalProjectedValueProjector> projectors) {
		this.projectors = projectors;
	}

	@Override
	public Optional<ProjectedValue<BigDecimal>> project(Optional<ProjectedValue<BigDecimal>> value,
			ProjectionContext context) {
		return value.flatMap(v -> doProject(v, context));
	}

	@Override
	public String getName() {
		return NAME;
	}

	private Optional<ProjectedValue<BigDecimal>> doProject(ProjectedValue<BigDecimal> value, ProjectionContext context) {
		Optional<NumericalProjectedValueProjector> projector = findProjectorForCode(value);
		return projector.flatMap(p -> p.project(Optional.of(value), context));
	}

	private Optional<NumericalProjectedValueProjector> findProjectorForCode(ProjectedValue<BigDecimal> value) {
		Optional<NumericalProjectedValueProjector> projector = projectors.entrySet()
				.stream()
				.filter(e -> e.getKey()
						.contains(value.getCode()))
				.map(Map.Entry::getValue)
				.findFirst();
		if (!projector.isPresent()) {
			LOGGER.error("No mapping found for {}", value.getCode());
		}
		return projector;
	}
}
