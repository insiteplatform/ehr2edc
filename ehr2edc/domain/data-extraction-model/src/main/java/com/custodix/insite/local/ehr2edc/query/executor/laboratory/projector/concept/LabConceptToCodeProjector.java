package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept;

public final class LabConceptToCodeProjector implements Projector<String, LabConcept> {

	private static final String NAME = "Code";

	@Override
	public Optional<String> project(Optional<LabConcept> value, ProjectionContext context) {
		return value.map(LabConcept::getConcept)
				.map(ConceptCode::getCode);
	}

	@Override
	public String getName() {
		return NAME;
	}
}