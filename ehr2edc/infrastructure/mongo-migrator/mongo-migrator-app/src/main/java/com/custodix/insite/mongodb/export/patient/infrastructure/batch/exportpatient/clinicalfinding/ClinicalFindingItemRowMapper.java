package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.clinicalfinding;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFactRecord;
import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingItem;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.AbstractCollectingRowMapper;

class ClinicalFindingItemRowMapper extends AbstractCollectingRowMapper<ClinicalFindingItem> {
	private static final String OBSERVATION_ID_COLUMN = "observation_id";
	private static final String MODIFIER_CODE_COLUMN = "modifier_cd";
	private static final String PATIENT_SOURCE_COLUMN = "patient_ide_source";
	private static final String PATIENT_ID_COLUMN = "patient_ide";
	private static final String START_DATE_COLUMN = "start_date";
	private static final String LLN_COLUMN = "val_lln";
	private static final String ULN_COLUMN = "val_uln";
	private static final String UNIT_COLUMN = "units_cd";
	private static final String VALUE_COLUMN = "nval_num";
	private static final String CODE_COLUMN = "concept_cd";
	private static final String LABEL_COLUMN = "name_char";
	private static final String NO_MODIFIER_CODE = "@";

	@Override
	protected ClinicalFindingItem mapRow(ResultSet rs, ClinicalFindingItem partialResult, int rowNum)
			throws SQLException {
		if (partialResult == null) {
			partialResult = createClinicalFindingItem(rs);
		}
		addModifierCode(partialResult, rs);
		return partialResult;
	}

	@Override
	protected boolean isRelated(ResultSet rs, ClinicalFindingItem partialResult) throws SQLException {
		return partialResult.getClinicalFactRecord()
				.getObservationId()
				.equals(rs.getLong(OBSERVATION_ID_COLUMN));
	}

	private void addModifierCode(ClinicalFindingItem partialResult, ResultSet rs) throws SQLException {
		String modifierCode = rs.getString(MODIFIER_CODE_COLUMN);
		if (!modifierCode.equals(NO_MODIFIER_CODE)) {
			partialResult.addModifierCode(modifierCode);
		}
	}

	private ClinicalFindingItem createClinicalFindingItem(ResultSet resultSet) throws SQLException {
		ClinicalFactRecord record = ClinicalFactRecord.newBuilder()
				.withObservationId(resultSet.getLong(OBSERVATION_ID_COLUMN))
				.withPatientId(resultSet.getString(PATIENT_ID_COLUMN))
				.withNamespace(resultSet.getString(PATIENT_SOURCE_COLUMN))
				.withStartDate(resultSet.getTimestamp(START_DATE_COLUMN))
				.withLlnValue(resultSet.getBigDecimal(LLN_COLUMN))
				.withUlnValue(resultSet.getBigDecimal(ULN_COLUMN))
				.withUnit(resultSet.getString(UNIT_COLUMN))
				.withValue(resultSet.getBigDecimal(VALUE_COLUMN))
				.withConceptCode(resultSet.getString(CODE_COLUMN))
				.withLabel(resultSet.getString(LABEL_COLUMN))
				.build();
		return new ClinicalFindingItem(record);
	}
}
