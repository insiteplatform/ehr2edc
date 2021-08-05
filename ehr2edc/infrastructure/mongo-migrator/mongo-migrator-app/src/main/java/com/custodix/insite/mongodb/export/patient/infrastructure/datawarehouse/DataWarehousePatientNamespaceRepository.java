package com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.custodix.insite.mongodb.export.patient.domain.repository.PatientNamespaceRepository;
import com.custodix.insite.mongodb.vocabulary.Namespace;

public class DataWarehousePatientNamespaceRepository implements PatientNamespaceRepository {
	private static final String PATIENT_DIMENSION_SQL =
			"SELECT pm.patient_ide as patientId " +
					"FROM patient_mapping pm " +
					"WHERE pm.patient_ide_source = '%s' " +
					"LIMIT 1";

	private final JdbcTemplate jdbcTemplate;

	public DataWarehousePatientNamespaceRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public boolean exists(Namespace namespace) {
		String query = getQuery(namespace);
		List<String> patientIds = jdbcTemplate.queryForList(query, String.class);
		return !patientIds.isEmpty();
	}

	private String getQuery(Namespace namespace) {
		return String.format(PATIENT_DIMENSION_SQL, namespace.getName());
	}
}