package com.custodix.insite.local.ehr2edc.query.executor.medication.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.MedicationConcept;

public final class ProjectMedication implements MedicationProjector<ProjectedValue> {
	private static final String NAME = "Project to ProjectedValue";
	private final MedicationField field;

	public ProjectMedication(MedicationField field) {
		this.field = field;
	}

	@Override
	public Optional<ProjectedValue> project(Optional<Medication> med, ProjectionContext context) {
		return med.flatMap(this::doProject);
	}

	@Override
	public String getName() {
		return NAME;
	}

	private Optional<ProjectedValue> doProject(Medication med) {
		return field.getValue(med)
				.map(v -> createProjectedValue(v, med));
	}

	private ProjectedValue createProjectedValue(Object value, Medication med) {
		return new ProjectedValue<>(value, getUnit(med), getCode(med));
	}

	private String getUnit(Medication med) {
		return Optional.ofNullable(med.getDosage())
				.map(Dosage::getUnit)
				.orElse(null);
	}

	private String getCode(Medication med) {
		return Optional.ofNullable(med.getMedicationConcept())
				.map(MedicationConcept::getConcept)
				.map(ConceptCode::getCode)
				.orElse(null);
	}
}
