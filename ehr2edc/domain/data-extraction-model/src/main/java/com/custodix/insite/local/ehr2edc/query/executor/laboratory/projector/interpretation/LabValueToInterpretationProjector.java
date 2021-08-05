package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.interpretation;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValueInterpretation;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.LabValueProjector;

public final class LabValueToInterpretationProjector implements LabValueProjector<LabValueInterpretation> {

	private static final String NAME = "Interpretation";

	@Override
	public Optional<LabValueInterpretation> project(Optional<LabValue> value, ProjectionContext context) {
		return value.map(LabValue::getQualitativeResult);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
