package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patientsearchcriteriainformation;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;

import com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation.PatientSearchCriteriaInformationRecord;

public class PatientSearchCriteriaInformationReaderFactory {
	private static final String READER_NAME = "patientReader";
	private static final String PATIENTS_IDS_SQL =
			"SELECT pm.patient_ide as patientId, pm.patient_ide_source as namespace, pd.birth_date as birthDate "
					+ "FROM patient_dimension pd JOIN patient_mapping pm ON pd.patient_num = pm.patient_num";

	private final DataSource dataSource;

	public PatientSearchCriteriaInformationReaderFactory(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	ItemReader<PatientSearchCriteriaInformationRecord> buildItemReader() {
		return new JdbcCursorItemReaderBuilder<PatientSearchCriteriaInformationRecord>().name(READER_NAME)
				.dataSource(dataSource)
				.sql(PATIENTS_IDS_SQL)
				.fetchSize(10000)
				.rowMapper(new PatientSearchCriteriaInformationRowMapper())
				.build();
	}
}
