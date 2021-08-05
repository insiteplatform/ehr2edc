package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patientsearchcriteriainformation;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation.PatientSearchCriteriaInformationRecord;

public class PatientSearchCriteriaInformationRowMapper implements RowMapper<PatientSearchCriteriaInformationRecord> {
	private static final String ID_COLUMN = "patientId";
	private static final String NAMESPACE_COLUMN = "namespace";
	private static final String BIRTHDATE_COLUMN = "birthDate";

	@Override
	public PatientSearchCriteriaInformationRecord mapRow(ResultSet resultSet, int i) throws SQLException {
		return PatientSearchCriteriaInformationRecord.newBuilder()
				.withId(resultSet.getString(ID_COLUMN))
				.withNamespace(resultSet.getString(NAMESPACE_COLUMN))
				.withBirthDate(resultSet.getTimestamp(BIRTHDATE_COLUMN))
				.build();
	}
}
