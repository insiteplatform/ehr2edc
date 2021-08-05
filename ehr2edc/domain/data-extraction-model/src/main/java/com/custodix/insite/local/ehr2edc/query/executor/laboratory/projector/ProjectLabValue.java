package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;

public final class ProjectLabValue implements LabValueProjector<ProjectedValue> {
	private static final String NAME = "Project to ProjectedValue";
	private final LabValueField field;

	public ProjectLabValue(LabValueField field) {
		this.field = field;
	}

	@Override
	public Optional<ProjectedValue> project(Optional<LabValue> labValue, ProjectionContext context) {
		return labValue.flatMap(this::doProject);
	}

	@Override
	public String getName() {
		return NAME;
	}

	private Optional<ProjectedValue> doProject(LabValue labValue) {
		return field.getValue(labValue)
				.map(v -> createProjectedValue(v, labValue));
	}

	private ProjectedValue createProjectedValue(Object value, LabValue labValue) {
		Measurement measurement = labValue.getQuantitativeResult();
		return new ProjectedValue<>(value, getUnit(measurement), getCode(labValue));
	}

	private String getUnit(Measurement measurement) {
		return Optional.ofNullable(measurement)
				.map(Measurement::getUnit)
				.orElse(null);
	}

	private String getCode(LabValue labValue) {
		return Optional.ofNullable(labValue.getLabConcept())
				.map(LabConcept::getConcept)
				.map(ConceptCode::getCode)
				.orElse(null);
	}
}
