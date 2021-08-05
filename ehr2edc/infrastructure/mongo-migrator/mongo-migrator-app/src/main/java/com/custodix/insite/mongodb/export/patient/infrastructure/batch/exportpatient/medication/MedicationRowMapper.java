package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.medication;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationItem;
import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationRecord;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.AbstractCollectingRowMapper;

class MedicationRowMapper extends AbstractCollectingRowMapper<MedicationItem> {
	private static final String OBSERVATION_ID_COLUMN = "observation_id";
	private static final String PATIENT_ID_COLUMN = "patient_ide";
	private static final String PATIENT_SOURCE_COLUMN = "patient_ide_source";
	private static final String START_DATE_COLUMN = "start_date";
	private static final String END_DATE_COLUMN = "end_date";
	private static final String CODE_COLUMN = "concept_cd";
	private static final String CONCEPT_NAME_COLUMN = "name_char";
	private static final String DOSAGE_VALUE_COLUMN = "nval_num";
	private static final String DOSAGE_UNIT_COLUMN = "units_cd";
	private static final String MODIFIER_CODE_COLUMN = "modifier_cd";
	private static final String NO_MODIFIER_CODE = "@";
	private static final String DOSE_MODIFIER = "MOD:Dose";

	@Override
	protected MedicationItem mapRow(ResultSet rs, MedicationItem partialResult, int rowNum) throws SQLException {
		if (partialResult == null) {
			partialResult = createMedicationItem(rs);
		}
		processModifiers(partialResult, rs);
		return partialResult;
	}

	private MedicationItem createMedicationItem(ResultSet resultSet) throws SQLException {
		MedicationRecord build = MedicationRecord.newBuilder()
				.withPatientNum(resultSet.getString(PATIENT_ID_COLUMN))
				.withNamespace(resultSet.getString(PATIENT_SOURCE_COLUMN))
				.withStartDate(resultSet.getTimestamp(START_DATE_COLUMN))
				.withEndDate(resultSet.getTimestamp(END_DATE_COLUMN))
				.withConceptCode(resultSet.getString(CODE_COLUMN))
				.withConceptName(resultSet.getString(CONCEPT_NAME_COLUMN))
				.withObservationId(resultSet.getLong(OBSERVATION_ID_COLUMN))
				.build();
		return new MedicationItem(build);
	}

	@Override
	protected boolean isRelated(ResultSet rs, MedicationItem partialResult) throws SQLException {
		return partialResult.getMedicationRecord()
				.getObservationId()
				.equals(rs.getLong(OBSERVATION_ID_COLUMN));
	}

	private void processModifiers(MedicationItem partialResult, ResultSet rs) throws SQLException {
		String modifierCode = rs.getString(MODIFIER_CODE_COLUMN);

		if (DOSE_MODIFIER.equals(modifierCode)) {
			partialResult.addDosage(rs.getString(DOSAGE_UNIT_COLUMN), rs.getBigDecimal(DOSAGE_VALUE_COLUMN));
		} else if (!NO_MODIFIER_CODE.equals(modifierCode)) {
			partialResult.addModifierCode(modifierCode);
		}
	}

}