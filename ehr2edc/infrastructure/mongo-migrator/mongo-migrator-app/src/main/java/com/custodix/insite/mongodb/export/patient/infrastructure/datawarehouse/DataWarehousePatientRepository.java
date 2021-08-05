package com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.custodix.insite.mongodb.export.patient.domain.repository.PatientRepository;
import com.custodix.insite.mongodb.vocabulary.Namespace;
import com.custodix.insite.mongodb.vocabulary.PatientId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public class DataWarehousePatientRepository implements PatientRepository {
	private static final String PATIENT_DIMENSION_SQL =
			"SELECT pm.patient_ide as patientId " + "FROM patient_mapping pm "
					+ "WHERE pm.patient_ide = '%s' and pm.patient_ide_source = '%s' " + "LIMIT 1";

	private final JdbcTemplate jdbcTemplate;

	public DataWarehousePatientRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public boolean patientExists(PatientIdentifier patientIdentifier) {
		String query = getQuery(patientIdentifier.getPatientId(), patientIdentifier.getNamespace());
		List<String> patientIds = jdbcTemplate.queryForList(query, String.class);
		return !patientIds.isEmpty();
	}

	private String getQuery(PatientId patientId, Namespace namespace) {
		return String.format(PATIENT_DIMENSION_SQL, patientId.getId(), namespace.getName());
	}
}