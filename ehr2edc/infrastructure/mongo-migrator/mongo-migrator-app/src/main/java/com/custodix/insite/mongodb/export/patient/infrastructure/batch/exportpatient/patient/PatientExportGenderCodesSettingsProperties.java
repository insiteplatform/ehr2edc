package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patient;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "export.patient.gender.codes")
class PatientExportGenderCodesSettingsProperties implements PatientExportGenderCodesSettings {
	private String male;
	private String female;

	@Override
	public List<String> getMaleCodes() {
		return Arrays.asList(male.split(";"));
	}

	@Override
	public List<String> getFemaleCodes() {
		return Arrays.asList(female.split(";"));
	}

	public void setMale(String male) {
		this.male = male;
	}

	public void setFemale(String female) {
		this.female = female;
	}
}