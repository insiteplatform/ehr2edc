package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patient;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.custodix.insite.mongodb.export.patient.domain.model.PatientRecord;

class PatientRecordRowMapper implements RowMapper<PatientRecord> {
	private static final String ID_COLUMN = "patientId";
	private static final String NAMESPACE_COLUMN = "namespace";
	private static final String GENDER_COLUMN = "gender";
	private static final String BIRTH_DATE_COLUMN = "birthDate";
	private static final String DEATH_DATE_COLUMN = "deathDate";
	private static final String VITAL_STATUS_COLUMN = "vitalStatus";

	@Override
	public PatientRecord mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
		return PatientRecord.newBuilder()
				.withId(resultSet.getString(ID_COLUMN))
				.withNamespace(resultSet.getString(NAMESPACE_COLUMN))
				.withGender(resultSet.getString(GENDER_COLUMN))
				.withBirthDate(resultSet.getTimestamp(BIRTH_DATE_COLUMN))
				.withDeathDate(resultSet.getTimestamp(DEATH_DATE_COLUMN))
				.withVitalStatus(resultSet.getString(VITAL_STATUS_COLUMN))
				.build();
	}
}