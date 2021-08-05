package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept;

public final class LabConceptToFastingBooleanProjector implements Projector<Boolean, LabConcept>{

	private static final String NAME = "'is fasting'-boolean";

	@Override
	public Optional<Boolean> project(Optional<LabConcept> value, ProjectionContext context) {
		return value.map(LabConcept::getFastingStatus)
				.flatMap(FastingStatus::isFasting);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
