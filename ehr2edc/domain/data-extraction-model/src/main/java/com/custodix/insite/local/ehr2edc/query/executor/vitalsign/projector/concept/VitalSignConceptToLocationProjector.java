package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.concept;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept;

public class VitalSignConceptToLocationProjector implements Projector<String, VitalSignConcept>{

	private static final String NAME = "Location";

	@Override
	public Optional<String> project(Optional<VitalSignConcept> value, ProjectionContext context) {
		return value.map(VitalSignConcept::getLocation);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
