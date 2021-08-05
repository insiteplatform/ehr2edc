package com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse;

import static java.lang.String.format;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.custodix.insite.mongodb.export.patient.domain.model.common.ConceptPath;
import com.custodix.insite.mongodb.export.patient.domain.repository.ConceptPathRepository;

public class DataWarehouseConceptPathRepository implements ConceptPathRepository {
	private static final String CONCEPT_QUERY = "select concept_path from concept_dimension where concept_cd = '%s'";
	private static final String CONCEPT_PATH_COLUMN = "concept_path";

	private final JdbcTemplate jdbcTemplate;

	public DataWarehouseConceptPathRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<ConceptPath> getConceptPaths(String baseCode) {
		return jdbcTemplate.query(format(CONCEPT_QUERY, baseCode), (row, nr) -> {
			String path = row.getString(CONCEPT_PATH_COLUMN);
			return ConceptPath.newBuilder()
					.withPath(path)
					.build();
		});
	}
}