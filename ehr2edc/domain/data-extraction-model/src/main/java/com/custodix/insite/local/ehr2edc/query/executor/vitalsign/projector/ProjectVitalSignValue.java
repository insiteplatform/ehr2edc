package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept;

public final class ProjectVitalSignValue implements VitalSignProjector<ProjectedValue> {
	private static final String NAME = "Project to ProjectedValue";
	private final VitalSignField field;

	public ProjectVitalSignValue(VitalSignField field) {
		this.field = field;
	}

	@Override
	public Optional<ProjectedValue> project(Optional<VitalSign> vitalSign, ProjectionContext context) {
		return vitalSign.flatMap(this::doProject);
	}

	@Override
	public String getName() {
		return NAME;
	}

	private Optional<ProjectedValue> doProject(VitalSign vitalSign) {
		return field.getValue(vitalSign)
				.map(v -> createProjectedValue(v, vitalSign));
	}

	private ProjectedValue createProjectedValue(Object value, VitalSign vitalSign) {
		Measurement measurement = vitalSign.getMeasurement();
		return new ProjectedValue<>(value, getUnit(measurement), getCode(vitalSign));
	}

	private String getUnit(Measurement measurement) {
		return Optional.ofNullable(measurement)
				.map(Measurement::getUnit)
				.orElse(null);
	}

	private String getCode(VitalSign value) {
		return Optional.ofNullable(value.getVitalSignConcept())
				.map(VitalSignConcept::getConcept)
				.map(ConceptCode::getCode)
				.orElse(null);
	}
}
