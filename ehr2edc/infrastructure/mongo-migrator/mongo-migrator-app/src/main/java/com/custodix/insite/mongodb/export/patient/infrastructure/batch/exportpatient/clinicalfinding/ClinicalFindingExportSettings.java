package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.clinicalfinding;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "export.clinicalfindings")
class ClinicalFindingExportSettings {
	private List<String> conceptNamespaces = Arrays.asList("LOINC", "SNOCT", "SNOMED-CT");

	public List<String> getConceptNamespaces() {
		return conceptNamespaces;
	}

	public void setConceptNamespaces(List<String> conceptNamespaces) {
		this.conceptNamespaces = conceptNamespaces;
	}
}