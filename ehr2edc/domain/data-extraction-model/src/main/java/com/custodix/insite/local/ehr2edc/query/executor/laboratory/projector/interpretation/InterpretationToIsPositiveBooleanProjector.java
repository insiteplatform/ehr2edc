package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.interpretation;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValueInterpretation;

public final class InterpretationToIsPositiveBooleanProjector implements Projector<Boolean, LabValueInterpretation> {

	private static final String NAME = "to 'positive interpretation?'-boolean";

	@Override
	public Optional<Boolean> project(Optional<LabValueInterpretation> value, ProjectionContext context) {
		return value.map(LabValueInterpretation::getParsedInterpretation)
				.map(this::parsedInterpretationToBoolean);
	}

	@Override
	public String getName() {
		return NAME;
	}

	private Boolean parsedInterpretationToBoolean(Integer parsed) {
		return parsed == 0 ? Boolean.FALSE : parsed == 1 ? Boolean.TRUE : null;
	}
}
