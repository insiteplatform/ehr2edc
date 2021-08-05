package com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse;

import java.util.HashMap;
import java.util.Map;

import com.custodix.insite.mongodb.export.patient.domain.model.common.ModifierCategory;

public class ModifierCategorySettings {
	private String laterality = "EHR2EDC_Laterality";
	private String position = "EHR2EDC_Position";
	private String location = "EHR2EDC_Anatomic_location";
	private String route = "EHR2EDC_Route";
	private String doseFormat = "EHR2EDC_Dose_format";
	private String frequency = "EHR2EDC_Medication_frequency";

	Map<String, ModifierCategory> getCategoryMapping() {
		Map<String, ModifierCategory> mapping = new HashMap<>();
		mapping.put(laterality, ModifierCategory.LATERALITY);
		mapping.put(position, ModifierCategory.POSITION);
		mapping.put(location, ModifierCategory.LOCATION);
		mapping.put(route, ModifierCategory.ROUTE);
		mapping.put(doseFormat, ModifierCategory.DOSE_FORMAT);
		mapping.put(frequency, ModifierCategory.FREQUENCY);
		return mapping;
	}

	public String getLaterality() {
		return laterality;
	}

	public void setLaterality(String laterality) {
		this.laterality = laterality;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getDoseFormat() {
		return doseFormat;
	}

	public void setDoseFormat(String doseFormat) {
		this.doseFormat = doseFormat;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

}
