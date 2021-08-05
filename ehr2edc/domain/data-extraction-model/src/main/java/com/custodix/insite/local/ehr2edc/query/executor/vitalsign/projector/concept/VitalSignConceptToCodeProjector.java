package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.concept;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept;

public final class VitalSignConceptToCodeProjector implements Projector<String, VitalSignConcept> {

	private static final String NAME = "Code";

	@Override
	public Optional<String> project(Optional<VitalSignConcept> value, ProjectionContext context) {
		return value.map(VitalSignConcept::getConcept)
				.map(ConceptCode::getCode);
	}

	@Override
	public String getName() {
		return NAME;
	}
}