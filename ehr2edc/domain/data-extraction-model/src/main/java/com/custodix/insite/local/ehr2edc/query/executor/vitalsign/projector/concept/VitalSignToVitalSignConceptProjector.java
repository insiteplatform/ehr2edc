package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.concept;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.VitalSignProjector;

public final class VitalSignToVitalSignConceptProjector implements VitalSignProjector<VitalSignConcept> {

	private static final String NAME = "Concept";

	@Override
	public Optional<VitalSignConcept> project(Optional<VitalSign> value, ProjectionContext context) {
		return value.map(VitalSign::getVitalSignConcept);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
