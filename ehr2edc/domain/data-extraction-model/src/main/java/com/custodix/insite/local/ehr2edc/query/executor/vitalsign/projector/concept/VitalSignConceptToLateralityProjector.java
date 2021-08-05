package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.concept;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept;

public class VitalSignConceptToLateralityProjector implements Projector<String, VitalSignConcept> {

	private static final String NAME = "Laterality";

	@Override
	public Optional<String> project(Optional<VitalSignConcept> value, ProjectionContext context) {
		return value.map(VitalSignConcept::getLaterality);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
