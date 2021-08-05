package com.custodix.insite.local.ehr2edc.query.mongo.converter;

import org.springframework.core.convert.converter.Converter;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientExporterId;

public class PatientExporterIdWriterConverter implements Converter<PatientExporterId, String> {

	@Override
	public String convert(final PatientExporterId patientExporterId) {
		return patientExporterId.getId();
	}
}
