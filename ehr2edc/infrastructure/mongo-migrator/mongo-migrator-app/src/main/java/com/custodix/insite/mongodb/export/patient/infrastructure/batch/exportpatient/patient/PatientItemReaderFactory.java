package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patient;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;

import com.custodix.insite.mongodb.export.patient.domain.model.PatientRecord;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

final class PatientItemReaderFactory {
	private static final String PATIENT_DIMENSION_SQL =
			"SELECT pm.patient_ide as patientId, pm.patient_ide_source as namespace, "
					+ "pd.birth_date as birthDate, pd.sex_cd as gender, "
					+ "pd.vital_status_cd as vitalStatus, pd.death_date as deathDate "
					+ "FROM patient_dimension pd JOIN patient_mapping pm ON pd.patient_num = pm.patient_num "
					+ "WHERE pm.patient_ide = ? and pm.patient_ide_source = ?";
	private static final String READER_NAME = "patientReader";

	private final DataSource dataSource;

	PatientItemReaderFactory(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	ItemReader<PatientRecord> buildItemReader(PatientIdentifier patientIdentifier) {
		return new JdbcCursorItemReaderBuilder<PatientRecord>().name(READER_NAME)
				.dataSource(dataSource)
				.sql(PATIENT_DIMENSION_SQL)
				.queryArguments(new Object[] { patientIdentifier.getPatientId().getId(),
											   patientIdentifier.getNamespace().getName() })
				.fetchSize(1)
				.maxRows(1)
				.rowMapper(new PatientRecordRowMapper())
				.build();
	}
}