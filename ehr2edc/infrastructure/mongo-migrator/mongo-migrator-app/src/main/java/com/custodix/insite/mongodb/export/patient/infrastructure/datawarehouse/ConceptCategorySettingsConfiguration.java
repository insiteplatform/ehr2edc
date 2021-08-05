package com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "concept.categories")
public class ConceptCategorySettingsConfiguration implements ConceptCategorySettings {
	private String laboratory;
	private String clinicalfinding;
	private String medication;

	@Override
	public String getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(String laboratory) {
		this.laboratory = laboratory;
	}

	@Override
	public String getClinicalfinding() {
		return clinicalfinding;
	}

	public void setClinicalfinding(String clinicalfinding) {
		this.clinicalfinding = clinicalfinding;
	}

	@Override
	public String getMedication() {
		return medication;
	}

	public void setMedication(String medication) {
		this.medication = medication;
	}
}
