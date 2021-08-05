package eu.ehr4cr.workbench.local.entities.dao;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class HSQLSChemaCreator {

	private String schemaName;

	private DataSource dataSource;

	public HSQLSChemaCreator(String schemaName, DataSource dataSource) {
		this.schemaName = schemaName;
		this.dataSource = dataSource;
	}

	@PostConstruct
	public void postConstruct() throws Exception {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName + " AUTHORIZATION DBA");
	}
}
