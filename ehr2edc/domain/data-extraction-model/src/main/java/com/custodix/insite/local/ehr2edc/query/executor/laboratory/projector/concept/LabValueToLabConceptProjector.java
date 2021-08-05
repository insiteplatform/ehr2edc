package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.LabValueProjector;

public final class LabValueToLabConceptProjector implements LabValueProjector<LabConcept> {

	private static final String NAME = "Concept";

	@Override
	public Optional<LabConcept> project(Optional<LabValue> value, ProjectionContext context) {
		return value.map(LabValue::getLabConcept);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
