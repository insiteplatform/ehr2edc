package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.medication;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "export.medication")
class MedicationExportSettings {
	private String conceptNamespace = "ATC";

	public String getConceptNamespace() {
		return conceptNamespace;
	}

	public void setConceptNamespace(String conceptNamespace) {
		this.conceptNamespace = conceptNamespace;
	}
}