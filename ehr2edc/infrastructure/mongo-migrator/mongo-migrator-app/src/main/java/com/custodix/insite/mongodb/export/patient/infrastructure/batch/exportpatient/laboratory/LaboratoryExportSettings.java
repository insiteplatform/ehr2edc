package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.laboratory;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "export.laboratory")
public class LaboratoryExportSettings {
	private String conceptNamespace = "LOINC";

	public String getConceptNamespace() {
		return conceptNamespace;
	}

	public void setConceptNamespace(String conceptNamespace) {
		this.conceptNamespace = conceptNamespace;
	}
}