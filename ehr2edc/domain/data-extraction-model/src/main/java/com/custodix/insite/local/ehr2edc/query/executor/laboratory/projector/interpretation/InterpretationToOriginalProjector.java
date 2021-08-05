package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.interpretation;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValueInterpretation;

public final class InterpretationToOriginalProjector implements Projector<String, LabValueInterpretation> {

	private static final String NAME = "Original interpretation";

	@Override
	public Optional<String> project(Optional<LabValueInterpretation> value, ProjectionContext context) {
		return value.map(LabValueInterpretation::getOriginalInterpretation);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
