package com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.custodix.insite.mongodb.export.patient.domain.model.common.ModifierCategory;

@ConfigurationProperties(prefix = "modifier")
public class ModifierSettings {
	private List<String> conceptNamespaces = Arrays.asList("SNOMED-CT", "MOD");
	private ModifierCategorySettings categories;

	Map<String, ModifierCategory> getCategoryMapping() {
		return categories.getCategoryMapping();
	}

	public List<String> getConceptNamespaces() {
		return conceptNamespaces;
	}

	public void setConceptNamespaces(List<String> conceptNamespaces) {
		this.conceptNamespaces = conceptNamespaces;
	}

	public ModifierCategorySettings getCategories() {
		return categories;
	}

	public void setCategories(ModifierCategorySettings categories) {
		this.categories = categories;
	}
}
