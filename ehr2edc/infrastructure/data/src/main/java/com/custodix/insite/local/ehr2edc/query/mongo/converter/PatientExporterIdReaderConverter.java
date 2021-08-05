package com.custodix.insite.local.ehr2edc.query.mongo.converter;

import org.springframework.core.convert.converter.Converter;

import com.custodix.insite.local.ehr2edc.vocabulary.PatientExporterId;

public class PatientExporterIdReaderConverter implements Converter<String, PatientExporterId> {

	@Override
	public PatientExporterId convert(final String patientExporterId) {
		if (patientExporterId == null) {
			return null;
		}
		return PatientExporterId.of(patientExporterId);
	}
}
