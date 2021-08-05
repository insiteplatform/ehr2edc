package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.laboratory;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryObservationFactRecord;

class LaboratoryObservationFactRowMapper implements RowMapper<LaboratoryObservationFactRecord> {
	private static final String CONCEPT_COLUMN = "concept_cd";
	private static final String CONCEPT_LABEL_COLUMN = "name_char";
	private static final String END_DATE_COLUMN = "end_date";
	private static final String LLN_COLUMN = "val_lln";
	private static final String PATIENT_SOURCE_COLUMN = "patient_ide_source";
	private static final String PATIENT_ID_COLUMN = "patient_ide";
	private static final String START_DATE_COLUMN = "start_date";
	private static final String ULN_COLUMN = "val_uln";
	private static final String UNIT_COLUMN = "units_cd";
	private static final String VALUE_COLUMN = "nval_num";
	private static final String VENDOR_COLUMN = "sourcesystem_cd";

	@Override
	public LaboratoryObservationFactRecord mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		return LaboratoryObservationFactRecord.newBuilder()
				.withConceptCD(resultSet.getString(CONCEPT_COLUMN))
				.withConceptLabel(resultSet.getString(CONCEPT_LABEL_COLUMN))
				.withEndDate(resultSet.getTimestamp(END_DATE_COLUMN))
				.withLlnValue(resultSet.getBigDecimal(LLN_COLUMN))
				.withPatientMasterIndex(resultSet.getString(PATIENT_SOURCE_COLUMN))
				.withPatientNum(resultSet.getString(PATIENT_ID_COLUMN))
				.withStartDate(resultSet.getTimestamp(START_DATE_COLUMN))
				.withUlnValue(resultSet.getBigDecimal(ULN_COLUMN))
				.withUnit(resultSet.getString(UNIT_COLUMN))
				.withValue(resultSet.getBigDecimal(VALUE_COLUMN))
				.withVendor(resultSet.getString(VENDOR_COLUMN))
				.build();
	}
}