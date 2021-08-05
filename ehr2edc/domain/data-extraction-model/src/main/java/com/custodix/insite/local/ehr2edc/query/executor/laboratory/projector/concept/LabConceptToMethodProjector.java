package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept;

public final class LabConceptToMethodProjector implements Projector<String, LabConcept> {

	private static final String NAME = "Method";

	@Override
	public Optional<String> project(Optional<LabConcept> value, ProjectionContext context) {
		return value.map(LabConcept::getMethod);
	}

	@Override
	public String getName() {
		return NAME;
	}
}